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
package org.qtitools.mathassess;

import uk.ac.ed.ph.jqtiplus.exception.QtiEvaluationException;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.BooleanValue;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.IntegerValue;
import uk.ac.ed.ph.jqtiplus.value.MultipleValue;
import uk.ac.ed.ph.jqtiplus.value.NullValue;
import uk.ac.ed.ph.jqtiplus.value.OrderedValue;
import uk.ac.ed.ph.jqtiplus.value.RecordValue;
import uk.ac.ed.ph.jqtiplus.value.SingleValue;
import uk.ac.ed.ph.jqtiplus.value.StringValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import org.qtitools.mathassess.tools.qticasbridge.types.BooleanMultipleValueWrapper;
import org.qtitools.mathassess.tools.qticasbridge.types.BooleanOrderedValueWrapper;
import org.qtitools.mathassess.tools.qticasbridge.types.BooleanValueWrapper;
import org.qtitools.mathassess.tools.qticasbridge.types.FloatMultipleValueWrapper;
import org.qtitools.mathassess.tools.qticasbridge.types.FloatOrderedValueWrapper;
import org.qtitools.mathassess.tools.qticasbridge.types.FloatValueWrapper;
import org.qtitools.mathassess.tools.qticasbridge.types.IntegerMultipleValueWrapper;
import org.qtitools.mathassess.tools.qticasbridge.types.IntegerOrderedValueWrapper;
import org.qtitools.mathassess.tools.qticasbridge.types.IntegerValueWrapper;
import org.qtitools.mathassess.tools.qticasbridge.types.MathsContentInputValueWrapper;
import org.qtitools.mathassess.tools.qticasbridge.types.MathsContentValueWrapper;
import org.qtitools.mathassess.tools.qticasbridge.types.MultipleValueWrapper;
import org.qtitools.mathassess.tools.qticasbridge.types.OrderedValueWrapper;
import org.qtitools.mathassess.tools.qticasbridge.types.SingleValueWrapper;
import org.qtitools.mathassess.tools.qticasbridge.types.ValueWrapper;

/**
 * Provides the required static methods for mapping between JQTI {@link Value}s
 * and the
 * corresponding {@link ValueWrapper}s in MathAssessTools.
 * 
 * @author Jonathon Hare
 * @author David McKain
 */
public final class CasTypeGlue {

    public static BooleanValue convertToJQTI(BooleanValueWrapper value) {
        return BooleanValue.valueOf(value.getValue().booleanValue());
    }

    public static IntegerValue convertToJQTI(IntegerValueWrapper value) {
        return new IntegerValue(value.getValue().intValue());
    }

    public static FloatValue convertToJQTI(FloatValueWrapper value) {
        return new FloatValue(value.getValue().floatValue());
    }

    public static RecordValue convertToJQTI(MathsContentValueWrapper value) {
        final RecordValue rv = new RecordValue();

        /* First add pseudo "class" entry that allows us to determine (to a
         * point) that this
         * record corresponds to a MathsContent value.
         * It would be nice if QTI supported this natively, so we'll kind of
         * hack something
         * together in the interim using a URL identifier that nobody else is
         * likely to use. */
        rv.add(MathAssessConstants.MATHS_CONTENT_RECORD_VARIABLE_IDENTIFIER, new StringValue(MathAssessConstants.MATHS_CONTENT_RECORD_VARIABLE_VALUE));

        /* Fill in other fields */
        if (value.getMaximaInput() != null && value.getMaximaInput().length() > 0) {
            rv.add(MathAssessConstants.FIELD_MAXIMA_IDENTIFIER, new StringValue(value.getMaximaInput()));
        }
        if (value.getPMathML() != null && value.getPMathML().length() > 0) {
            rv.add(MathAssessConstants.FIELD_PMATHML_IDENTIFIER, new StringValue(value.getPMathML()));
        }
        if (value.getCMathML() != null && value.getCMathML().length() > 0) {
            rv.add(MathAssessConstants.FIELD_CMATHML_IDENTIFIER, new StringValue(value.getCMathML()));
        }
        if (value.getAsciiMathInput() != null && value.getAsciiMathInput().length() > 0) {
            rv.add(MathAssessConstants.FIELD_CANDIDATE_INPUT_IDENTIFIER, new StringValue(value
                    .getAsciiMathInput()));
        }
        if (value instanceof MathsContentInputValueWrapper) {
            /* (This goes outside the MathAssess spec, but if this is something
             * that came from
             * ASCIIMath input then we shall also include the bracketed PMathML
             * so that it can
             * be used in rendering. */
            final MathsContentInputValueWrapper inputValue = (MathsContentInputValueWrapper) value;
            if (inputValue.getPMathMLBracketed() != null && inputValue.getPMathMLBracketed().length() > 0) {
                rv.add(MathAssessConstants.FIELD_PMATHML_BRACKETED_IDENTIFIER, new StringValue(inputValue.getPMathMLBracketed()));
            }
        }
        return rv;
    }

    public static Value convertToJQTI(ValueWrapper value) {
        if (value == null || value.isNull()) {
            return NullValue.INSTANCE;
        }

        switch (value.getCardinality()) {
            case SINGLE:
                return convertToJQTI((SingleValueWrapper<?>) value);

            case MULTIPLE:
                return convertToJQTI((MultipleValueWrapper<?, ?>) value);

            case ORDERED:
                return convertToJQTI((OrderedValueWrapper<?, ?>) value);

            case MATHS_CONTENT:
                return convertToJQTI((MathsContentValueWrapper) value);

            default:
                throw new QtiEvaluationException("Converting type " + value.getCardinality()
                        + " is not supported.");
        }
    }

    public static SingleValue convertToJQTI(SingleValueWrapper<?> value) {
        switch (value.getBaseType()) {
            case BOOLEAN:
                return convertToJQTI((BooleanValueWrapper) value);

            case INTEGER:
                return convertToJQTI((IntegerValueWrapper) value);

            case FLOAT:
                return convertToJQTI((FloatValueWrapper) value);

            default:
                throw new QtiEvaluationException("Converting type " + value.getBaseType()
                        + " is not supported.");
        }
    }

    public static OrderedValue convertToJQTI(OrderedValueWrapper<?, ?> wrapper) {
        final OrderedValue value = new OrderedValue();

        for (final SingleValueWrapper<?> v : wrapper) {
            value.add(convertToJQTI(v));
        }
        return value;
    }

    public static MultipleValue convertToJQTI(MultipleValueWrapper<?, ?> wrapper) {
        final MultipleValue value = new MultipleValue();

        for (final SingleValueWrapper<?> v : wrapper) {
            value.add(convertToJQTI(v));
        }
        return value;
    }

    // ----------------------------------------------------------------------

    public static boolean isMathsContentRecord(Value value) {
        if (!(value instanceof RecordValue)) {
            return false;
        }
        final SingleValue testValue = ((RecordValue) value).get(MathAssessConstants.MATHS_CONTENT_RECORD_VARIABLE_IDENTIFIER);
        return testValue != null && testValue.stringValue().equals(MathAssessConstants.MATHS_CONTENT_RECORD_VARIABLE_VALUE);
    }

    public static ValueWrapper convertFromJQTI(Value value) {
        switch (value.getCardinality()) {
            case SINGLE:
                return convertFromJQTI((SingleValue) value);

            case MULTIPLE:
                return convertFromJQTI((MultipleValue) value);

            case ORDERED:
                return convertFromJQTI((OrderedValue) value);

            case RECORD:
                final RecordValue recordValue = (RecordValue) value;
                if (!isMathsContentRecord(recordValue)) {
                    throw new QtiEvaluationException("RecordValue " + recordValue + " does not appear to hold MathsContent");
                }
                return convertFromJQTI(recordValue);

            default:
                throw new QtiEvaluationException("Unexpected switch case");

        }
    }

    public static SingleValueWrapper<?> convertFromJQTI(SingleValue value) {
        if (value.getBaseType().isBoolean()) {
            return convertFromJQTI((BooleanValue) value);
        }
        else if (value.getBaseType().isInteger()) {
            return convertFromJQTI((IntegerValue) value);
        }
        else if (value.getBaseType().isFloat()) {
            return convertFromJQTI((FloatValue) value);
        }
        throw new QtiEvaluationException("Converting type " + value.getBaseType()
                + " is not supported.");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static OrderedValueWrapper<?, ?> convertFromJQTI(OrderedValue value) {
        OrderedValueWrapper wrapper;

        if (value.getBaseType().isBoolean()) {
            wrapper = new BooleanOrderedValueWrapper();
        }
        else if (value.getBaseType().isInteger()) {
            wrapper = new IntegerOrderedValueWrapper();
        }
        else if (value.getBaseType().isFloat()) {
            wrapper = new FloatOrderedValueWrapper();
        }
        else {
            throw new QtiEvaluationException("Converting type " + value.getBaseType()
                    + " is not supported.");
        }

        for (final SingleValue v : value) {
            wrapper.add(convertFromJQTI(v));
        }
        return wrapper;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static MultipleValueWrapper<?, ?> convertFromJQTI(MultipleValue value) {
        MultipleValueWrapper wrapper;

        if (value.getBaseType().isBoolean()) {
            wrapper = new BooleanMultipleValueWrapper();
        }
        else if (value.getBaseType().isInteger()) {
            wrapper = new IntegerMultipleValueWrapper();
        }
        else if (value.getBaseType().isFloat()) {
            wrapper = new FloatMultipleValueWrapper();
        }
        else {
            throw new QtiEvaluationException("Converting type " + value.getBaseType()
                    + " is not supported.");
        }

        for (final SingleValue v : value) {
            wrapper.add(convertFromJQTI(v));
        }
        return wrapper;
    }

    public static BooleanValueWrapper convertFromJQTI(BooleanValue value) {
        return new BooleanValueWrapper(value.booleanValue());
    }

    public static IntegerValueWrapper convertFromJQTI(IntegerValue value) {
        return new IntegerValueWrapper(value.intValue());
    }

    public static FloatValueWrapper convertFromJQTI(FloatValue value) {
        return new FloatValueWrapper(value.doubleValue());
    }

    public static MathsContentValueWrapper convertFromJQTI(RecordValue value) {
        final MathsContentInputValueWrapper wrapper = new MathsContentInputValueWrapper();
        if (value.get(MathAssessConstants.FIELD_MAXIMA_IDENTIFIER) != null) {
            wrapper.setMaximaInput(value.get(MathAssessConstants.FIELD_MAXIMA_IDENTIFIER).stringValue());
        }
        if (value.get(MathAssessConstants.FIELD_PMATHML_IDENTIFIER) != null) {
            wrapper.setPMathML(value.get(MathAssessConstants.FIELD_PMATHML_IDENTIFIER).stringValue());
        }
        if (value.get(MathAssessConstants.FIELD_PMATHML_BRACKETED_IDENTIFIER) != null) {
            wrapper.setPMathMLBracketed(value.get(MathAssessConstants.FIELD_MAXIMA_IDENTIFIER).stringValue());
        }
        if (value.get(MathAssessConstants.FIELD_CMATHML_IDENTIFIER) != null) {
            wrapper.setCMathML(value.get(MathAssessConstants.FIELD_CMATHML_IDENTIFIER).stringValue());
        }
        if (value.get(MathAssessConstants.FIELD_CANDIDATE_INPUT_IDENTIFIER) != null) {
            wrapper.setAsciiMathInput(value.get(MathAssessConstants.FIELD_CANDIDATE_INPUT_IDENTIFIER).stringValue());
        }
        return wrapper;
    }
    
    public static ValueWrapper[] convertFromJQTI(Value[] values) {
        final ValueWrapper[] output = new ValueWrapper[values.length];
        for (int i = 0; i < values.length; i++) {
            output[i] = convertFromJQTI(values[i]);
        }
        return output;
    }

    // ----------------------------------------------------------------------

    public static Class<? extends ValueWrapper> getCasClass(BaseType baseType, Cardinality cardinality) {
        switch (cardinality) {
            case SINGLE:
                switch (baseType) {
                    case BOOLEAN:
                        return BooleanValueWrapper.class;
                    case INTEGER:
                        return IntegerValueWrapper.class;
                    case FLOAT:
                        return FloatValueWrapper.class;
                    default:
                        return null;
                }

            case MULTIPLE:
                switch (baseType) {
                    case BOOLEAN:
                        return BooleanMultipleValueWrapper.class;
                    case INTEGER:
                        return IntegerMultipleValueWrapper.class;
                    case FLOAT:
                        return FloatMultipleValueWrapper.class;
                    default:
                        return null;
                }

            case ORDERED:
                switch (baseType) {
                    case BOOLEAN:
                        return BooleanOrderedValueWrapper.class;
                    case INTEGER:
                        return IntegerOrderedValueWrapper.class;
                    case FLOAT:
                        return FloatOrderedValueWrapper.class;
                    default:
                        return null;
                }

            case RECORD:
                return MathsContentValueWrapper.class;
        }
        return null;
    }

}
