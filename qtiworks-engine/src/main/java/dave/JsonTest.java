/* Copyright (c) 2012, University of Edinburgh.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice, this
 *   list of conditions and the following disclaimer in the documentation and/or
 *   other materials provided with the distribution.
 *
 * * Neither the name of the University of Edinburgh nor the names of its
 *   contributors may be used to endorse or promote products derived from this
 *   software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * This software is derived from (and contains code from) QTItools and MathAssessEngine.
 * QTItools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package dave;

import uk.ac.ed.ph.qtiworks.QtiWorksLogicException;

import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.IntegerValue;
import uk.ac.ed.ph.jqtiplus.value.ListValue;
import uk.ac.ed.ph.jqtiplus.value.MultipleValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.RecordValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.StringValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

/**
 * FIXME: Document this type
 *
 * @author David McKain
 */
public class JsonTest {

    private static final String SCHEMA_KEY = "qtiWorksObjectModel";
    private static final String SCHEMA_VALUE = "ItemSessionState-v1";

    public static void main(final String[] args) throws JsonGenerationException, IOException {
        final ItemSessionState itemSessionState = new ItemSessionState();

        final RecordValue rv = new RecordValue();
        rv.add(new Identifier("PMathML"), new StringValue("Hello"));
        rv.add(new Identifier("Number"), new IntegerValue(5));

        itemSessionState.setShuffledInteractionChoiceOrder(new Identifier("dave"), Arrays.asList(new Identifier("a")));
        itemSessionState.setResponseValue(new Identifier("RESPONSE"), new MultipleValue(Arrays.asList(new StringValue("Bad"), new StringValue("Thing"))));
        itemSessionState.setTemplateValue(new Identifier("TEMPLATE"), NullValue.INSTANCE);
        itemSessionState.setOutcomeValue(new Identifier("RECORD"), rv);

        /* OUTPUT */
        final StringWriter w = new StringWriter();

        final JsonFactory jsonFactory = new JsonFactory();
        final JsonGenerator jsonGenerator = jsonFactory.createJsonGenerator(w);
        jsonGenerator.useDefaultPrettyPrinter();

        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField(SCHEMA_KEY, SCHEMA_VALUE);
        jsonGenerator.writeBooleanField("initialized", itemSessionState.isInitialized());

        jsonGenerator.writeFieldName("shuffledInteractionChoiceOrders");
        jsonGenerator.writeStartObject();
        for (final Entry<Identifier, List<Identifier>> entry : itemSessionState.getShuffledInteractionChoiceOrders().entrySet()) {
            final Identifier identifier = entry.getKey();
            final List<Identifier> choiceIdentifiers = entry.getValue();
            jsonGenerator.writeFieldName(identifier.toString());
            jsonGenerator.writeStartArray();
            for (final Identifier choiceIdentifier : choiceIdentifiers) {
                jsonGenerator.writeString(choiceIdentifier.toString());
            }
            jsonGenerator.writeEndArray();
        }
        jsonGenerator.writeEndObject();

        maybeWriteValueMap(jsonGenerator, "responseValues", itemSessionState.getResponseValues());
        maybeWriteValueMap(jsonGenerator, "templateValues", itemSessionState.getTemplateValues());
        maybeWriteValueMap(jsonGenerator, "outcomeValues", itemSessionState.getOutcomeValues());
        maybeWriteValueMap(jsonGenerator, "overriddenTemplateDefaultValues", itemSessionState.getOverriddenTemplateDefaultValues());
        maybeWriteValueMap(jsonGenerator, "overriddenResponseDefaultValues", itemSessionState.getOverriddenResponseDefaultValues());
        maybeWriteValueMap(jsonGenerator, "overriddenOutcomeDefaultValues", itemSessionState.getOverriddenOutcomeDefaultValues());
        maybeWriteValueMap(jsonGenerator, "overriddenCorrectResponseValues", itemSessionState.getOverriddenCorrectResponseValues());

        jsonGenerator.writeEndObject();
        jsonGenerator.close();

        System.out.println(w);

//        /* INPUT */
//        final ItemSessionState s = new ItemSessionState();
//        final JsonParser jsonParser = jsonFactory.createJsonParser(new StringReader(w.toString()));
//        new Builder(s).parseState(jsonParser);
    }

    private static void maybeWriteValueMap(final JsonGenerator jsonGenerator, final String name, final Map<Identifier, Value> valueMap)
            throws JsonGenerationException, IOException {
        if (!valueMap.isEmpty()) {
            jsonGenerator.writeFieldName(name);
            jsonGenerator.writeStartObject();
            for (final Entry<Identifier, Value> entry : valueMap.entrySet()) {
                final Identifier identifier = entry.getKey();
                final Value value = entry.getValue();
                jsonGenerator.writeFieldName(identifier.toString());
                writeValue(jsonGenerator, value);
            }
            jsonGenerator.writeEndObject();
        }
    }

    private static void writeValue(final JsonGenerator jsonGenerator, final Value value) throws JsonGenerationException, IOException {
        if (value.isNull()) {
            jsonGenerator.writeNull();
        }
        else {
            final Cardinality cardinality = value.getCardinality();
            jsonGenerator.writeStartArray();
            jsonGenerator.writeString(value.getCardinality().toString());
            switch (cardinality) {
                case SINGLE:
                    jsonGenerator.writeString(value.getBaseType().toQtiString());
                    jsonGenerator.writeString(value.toQtiString());
                    break;

                case MULTIPLE:
                case ORDERED:
                    jsonGenerator.writeString(value.getBaseType().toQtiString());
                    final ListValue listValue = (ListValue) value;
                    for (final SingleValue listItem : listValue) {
                        jsonGenerator.writeString(listItem.toQtiString());
                    }
                    break;

                case RECORD:
                    jsonGenerator.writeStartObject();
                    final RecordValue recordValue = (RecordValue) value;
                    for (final Entry<Identifier, SingleValue> entry : recordValue.entrySet()) {
                        jsonGenerator.writeFieldName(entry.getKey().toString());
                        writeValue(jsonGenerator, entry.getValue());
                    }
                    jsonGenerator.writeEndObject();
                    break;

                default:
                    throw new QtiWorksLogicException("Unexpected logic branch: " + cardinality);

            }
            jsonGenerator.writeEndArray();
        }
    }

    static class Builder {

        private boolean correctSchema = false;
        private final ItemSessionState result;

        public Builder(final ItemSessionState s) {
            this.result = s;
        }

        public void parseState(final JsonParser p) throws JsonParseException, IOException {
            JsonToken token = p.nextToken();

            /* Make sure it's starting an Object */
            if (token!=JsonToken.START_OBJECT) {
                throw new IllegalStateException("Expected start of Object");
            }

            while ((token = p.nextToken()) != JsonToken.END_OBJECT) {
                final String fieldName = p.getCurrentName();
                System.out.println("AT " + fieldName);
                if (fieldName.equals(SCHEMA_KEY)) {
                    p.nextToken();
                    final String value = p.getText();
                    if (!SCHEMA_VALUE.equals(value)) {
                        throw new IllegalStateException("Expected " + SCHEMA_KEY + " to be " + SCHEMA_VALUE + " but got " + value);
                    }
                    correctSchema = true;
                }
                else if (fieldName.equals("initialized")) {
                    p.nextToken();
                    result.setInitialized(p.getBooleanValue());
                }
                else if (fieldName.equals("shuffledInteractionChoiceOrders")) {
                    /* FILL IN */
                }
//                else if (fieldName.equals("responseValues")) {
//                    readValueMap();
//                }
                /* FINISH ME! */
            }
        }

        private Map<Identifier, Value> readValueMap(final JsonParser p) throws JsonParseException, IOException {
            final Map<Identifier, Value> result = new HashMap<Identifier, Value>();
            if (p.nextToken()!=JsonToken.START_OBJECT) {
                throw new IllegalStateException("Expected start of Object");
            }
            while (true) {
                if (p.nextToken()!=JsonToken.FIELD_NAME) {
                    throw new IllegalStateException("Expected QTI variable name");
                }
                final Identifier identifier = new Identifier(p.getCurrentName());
                if (p.nextToken()!=JsonToken.VALUE_STRING) {
                    throw new IllegalStateException("Expected QTI variable value");
                }
                /* FINISH ME OFF! HOW DO WE KNOW WHICH VARIABLE IS WHICH? */
            }
        }
    }
}
