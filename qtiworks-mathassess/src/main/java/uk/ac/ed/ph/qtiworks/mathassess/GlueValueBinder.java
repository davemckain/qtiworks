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
package uk.ac.ed.ph.qtiworks.mathassess;

import uk.ac.ed.ph.qtiworks.mathassess.glue.types.BooleanMultipleValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.BooleanOrderedValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.BooleanValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.FloatMultipleValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.FloatOrderedValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.FloatValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.IntegerMultipleValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.IntegerOrderedValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.IntegerValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.MathsContentInputValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.MathsContentValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.MultipleValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.NullValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.OrderedValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.SingleValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.types.ValueWrapper;
import uk.ac.ed.ph.qtiworks.mathassess.value.ReturnTypeType;

import uk.ac.ed.ph.jqtiplus.exception2.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.internal.util.Assert;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides the required static methods for mapping between JQTI {@link Value}s
 * and the corresponding {@link ValueWrapper}s in the "glue" module.
 *
 * @author Jonathon Hare (original)
 * @author David McKain (rewritten)
 */
public final class GlueValueBinder {

    /**
     * Converts the given {@link ValueWrapper} to a JQTI {@link Value}.
     * <p>
     * Returns null if the given wrapper cannot be converted to a {@link Value}
     * (which indicates that the current implementation is incomplete).
     *
     * @param value {@link ValueWrapper} to convert, which must not be null.
     */
    public static Value casToJqti(final ValueWrapper value) {
        Assert.notNull(value);
        if (value.isNull()) {
            return NullValue.INSTANCE;
        }
        switch (value.getCardinality()) {
            case SINGLE:
                return casToJqti((SingleValueWrapper<?>) value);

            case MULTIPLE:
                return casToJqti((MultipleValueWrapper<?,?>) value);

            case ORDERED:
                return casToJqti((OrderedValueWrapper<?,?>) value);

            case MATHS_CONTENT:
                return casToJqti((MathsContentValueWrapper) value);

            default:
                throw new QtiLogicException("Unexpected switch case " + value.getCardinality());
        }
    }

    private static SingleValue casToJqti(final SingleValueWrapper<?> value) {
        switch (value.getBaseType()) {
            case BOOLEAN:
                return casToJqti((BooleanValueWrapper) value);

            case INTEGER:
                return casToJqti((IntegerValueWrapper) value);

            case FLOAT:
                return casToJqti((FloatValueWrapper) value);

            case POINT:
            case STRING:
                return null;

            default:
                throw new QtiLogicException("Unexpected switch case " + value.getBaseType());
        }
    }

    private static Value casToJqti(final OrderedValueWrapper<?,?> wrapper) {
        final List<SingleValue> values = new ArrayList<SingleValue>();
        for (final SingleValueWrapper<?> v : wrapper) {
            values.add(casToJqti(v));
        }
        return OrderedValue.createOrderedValue(values);
    }

    private static Value casToJqti(final MultipleValueWrapper<?,?> wrapper) {
        final List<SingleValue> values = new ArrayList<SingleValue>();
        for (final SingleValueWrapper<?> v : wrapper) {
            values.add(casToJqti(v));
        }
        return MultipleValue.createMultipleValue(values);
    }


    private static BooleanValue casToJqti(final BooleanValueWrapper value) {
        return BooleanValue.valueOf(value.getValue().booleanValue());
    }

    private static IntegerValue casToJqti(final IntegerValueWrapper value) {
        return new IntegerValue(value.getValue().intValue());
    }

    private static FloatValue casToJqti(final FloatValueWrapper value) {
        return new FloatValue(value.getValue().floatValue());
    }

    private static RecordValue casToJqti(final MathsContentValueWrapper value) {
        final Map<Identifier, SingleValue> recordBuilder = new HashMap<Identifier, SingleValue>();

        /* First add pseudo "class" entry that allows us to determine (to a
         * point) that this
         * record corresponds to a MathsContent value.
         * It would be nice if QTI supported this natively, so we'll kind of
         * hack something
         * together in the interim using a URL identifier that nobody else is
         * likely to use. */
        recordBuilder.put(MathAssessConstants.MATHS_CONTENT_RECORD_VARIABLE_IDENTIFIER, new StringValue(MathAssessConstants.MATHS_CONTENT_RECORD_VARIABLE_VALUE));

        /* Fill in other fields */
        if (value.getMaximaInput() != null && value.getMaximaInput().length() > 0) {
            recordBuilder.put(MathAssessConstants.FIELD_MAXIMA_IDENTIFIER, new StringValue(value.getMaximaInput()));
        }
        if (value.getPMathML() != null && value.getPMathML().length() > 0) {
            recordBuilder.put(MathAssessConstants.FIELD_PMATHML_IDENTIFIER, new StringValue(value.getPMathML()));
        }
        if (value.getCMathML() != null && value.getCMathML().length() > 0) {
            recordBuilder.put(MathAssessConstants.FIELD_CMATHML_IDENTIFIER, new StringValue(value.getCMathML()));
        }
        if (value.getAsciiMathInput() != null && value.getAsciiMathInput().length() > 0) {
            recordBuilder.put(MathAssessConstants.FIELD_CANDIDATE_INPUT_IDENTIFIER, new StringValue(value
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
                recordBuilder.put(MathAssessConstants.FIELD_PMATHML_BRACKETED_IDENTIFIER, new StringValue(inputValue.getPMathMLBracketed()));
            }
        }
        return (RecordValue) RecordValue.createRecordValue(recordBuilder);
    }

    // ----------------------------------------------------------------------

    public static boolean isMathsContentRecord(final Value value) {
        if (!(value instanceof RecordValue)) {
            return false;
        }
        final SingleValue testValue = ((RecordValue) value).get(MathAssessConstants.MATHS_CONTENT_RECORD_VARIABLE_IDENTIFIER);
        return testValue != null && testValue.toQtiString().equals(MathAssessConstants.MATHS_CONTENT_RECORD_VARIABLE_VALUE);
    }

    public static boolean isCasCompatibleMathsContentRecord(final Value value) {
        return isMathsContentRecord(value) && ((RecordValue) value).get(MathAssessConstants.FIELD_MAXIMA_IDENTIFIER)!=null;
    }

    /**
     * Converts a JQTI {@link Value} to a {@link ValueWrapper} suitable for passing to the
     * glue code.
     * <p>
     * Returns null if the given {@link Value} is not supported by the glue code.
     *
     * @param value
     * @return
     */
    public static ValueWrapper jqtiToCas(final Value value) {
        if (value.isNull()) {
            return NullValueWrapper.INSTANCE;
        }
        switch (value.getCardinality()) {
            case SINGLE:
                return jqtiToCas((SingleValue) value);

            case MULTIPLE:
                return jqtiToCas((MultipleValue) value);

            case ORDERED:
                return jqtiToCas((OrderedValue) value);

            case RECORD:
                final RecordValue recordValue = (RecordValue) value;
                if (!isCasCompatibleMathsContentRecord(recordValue)) {
                    return null;
                }
                return jqtiToCas(recordValue);

            default:
                throw new QtiLogicException("Unexpected switch case");
        }
    }

    private static SingleValueWrapper<?> jqtiToCas(final SingleValue value) {
        if (value.getBaseType().isBoolean()) {
            return jqtiToCas((BooleanValue) value);
        }
        else if (value.getBaseType().isInteger()) {
            return jqtiToCas((IntegerValue) value);
        }
        else if (value.getBaseType().isFloat()) {
            return jqtiToCas((FloatValue) value);
        }
        return null;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static OrderedValueWrapper<?,?> jqtiToCas(final OrderedValue value) {
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
            return null;
        }

        for (final SingleValue v : value) {
            wrapper.add(jqtiToCas(v));
        }
        return wrapper;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static MultipleValueWrapper<?,?> jqtiToCas(final MultipleValue value) {
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
            return null;
        }

        for (final SingleValue v : value) {
            wrapper.add(jqtiToCas(v));
        }
        return wrapper;
    }

    private static BooleanValueWrapper jqtiToCas(final BooleanValue value) {
        return new BooleanValueWrapper(value.booleanValue());
    }

    private static IntegerValueWrapper jqtiToCas(final IntegerValue value) {
        return new IntegerValueWrapper(value.intValue());
    }

    private static FloatValueWrapper jqtiToCas(final FloatValue value) {
        return new FloatValueWrapper(value.doubleValue());
    }

    private static MathsContentValueWrapper jqtiToCas(final RecordValue value) {
        final MathsContentInputValueWrapper wrapper = new MathsContentInputValueWrapper();
        if (value.get(MathAssessConstants.FIELD_MAXIMA_IDENTIFIER) != null) {
            wrapper.setMaximaInput(value.get(MathAssessConstants.FIELD_MAXIMA_IDENTIFIER).toQtiString());
        }
        if (value.get(MathAssessConstants.FIELD_PMATHML_IDENTIFIER) != null) {
            wrapper.setPMathML(value.get(MathAssessConstants.FIELD_PMATHML_IDENTIFIER).toQtiString());
        }
        if (value.get(MathAssessConstants.FIELD_PMATHML_BRACKETED_IDENTIFIER) != null) {
            wrapper.setPMathMLBracketed(value.get(MathAssessConstants.FIELD_MAXIMA_IDENTIFIER).toQtiString());
        }
        if (value.get(MathAssessConstants.FIELD_CMATHML_IDENTIFIER) != null) {
            wrapper.setCMathML(value.get(MathAssessConstants.FIELD_CMATHML_IDENTIFIER).toQtiString());
        }
        if (value.get(MathAssessConstants.FIELD_CANDIDATE_INPUT_IDENTIFIER) != null) {
            wrapper.setAsciiMathInput(value.get(MathAssessConstants.FIELD_CANDIDATE_INPUT_IDENTIFIER).toQtiString());
        }
        return wrapper;
    }

    // ----------------------------------------------------------------------

    /**
     * Returns the {@link ValueWrapper} class that would be used to wrap the output
     * from the CAS for the {@link ReturnTypeType}. This will never be null.
     */
    public static Class<? extends ValueWrapper> getCasReturnClass(final ReturnTypeType returnType) {
        switch (returnType) {
            case BOOLEAN:
                return BooleanValueWrapper.class;

            case BOOLEAN_MULTIPLE:
                return BooleanMultipleValueWrapper.class;

            case BOOLEAN_ORDERED:
                return BooleanOrderedValueWrapper.class;

            case FLOAT:
                return FloatValueWrapper.class;

            case FLOAT_MULTIPLE:
                return FloatMultipleValueWrapper.class;

            case FLOAT_ORDERED:
                return FloatOrderedValueWrapper.class;

            case INTEGER:
                return IntegerValueWrapper.class;

            case INTEGER_MULTIPLE:
                return IntegerMultipleValueWrapper.class;

            case INTEGER_ORDERED:
                return IntegerOrderedValueWrapper.class;

            case MATHS_CONTENT:
                return MathsContentInputValueWrapper.class;

            default:
                throw new QtiLogicException("Unexpected switch case " + returnType);
        }
    }

    /**
     * Returns the {@link ValueWrapper} class that would be used to wrap the output
     * from the CAS for the given {@link BaseType} and {@link Cardinality},
     * or null if that particular combination is not supported.
     *
     * @param baseType
     * @param cardinality
     * @return
     */
    public static Class<? extends ValueWrapper> getCasReturnClass(final BaseType baseType, final Cardinality cardinality) {
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
