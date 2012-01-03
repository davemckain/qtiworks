/*
<LICENCE>

Copyright (c) 2008, University of Southampton
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

  * Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.

  *    Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

  *    Neither the name of the University of Southampton nor the names of its
    contributors may be used to endorse or promote products derived from this
    software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

</LICENCE>
*/

package uk.ac.ed.ph.jqtiplus.node.expression;


import uk.ac.ed.ph.jqtiplus.control.QTILogicException;
import uk.ac.ed.ph.jqtiplus.control.ToRemove;
import uk.ac.ed.ph.jqtiplus.control.ValidationContext;
import uk.ac.ed.ph.jqtiplus.exception2.QTIIllegalChildException;
import uk.ac.ed.ph.jqtiplus.node.expression.general.BaseValue;
import uk.ac.ed.ph.jqtiplus.node.expression.general.Correct;
import uk.ac.ed.ph.jqtiplus.node.expression.general.Default;
import uk.ac.ed.ph.jqtiplus.node.expression.general.MapResponse;
import uk.ac.ed.ph.jqtiplus.node.expression.general.MapResponsePoint;
import uk.ac.ed.ph.jqtiplus.node.expression.general.Null;
import uk.ac.ed.ph.jqtiplus.node.expression.general.RandomFloat;
import uk.ac.ed.ph.jqtiplus.node.expression.general.RandomFloatEx;
import uk.ac.ed.ph.jqtiplus.node.expression.general.RandomInteger;
import uk.ac.ed.ph.jqtiplus.node.expression.general.RandomIntegerEx;
import uk.ac.ed.ph.jqtiplus.node.expression.general.Variable;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.And;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.AnyN;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.ContainerSize;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.Contains;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.CustomOperator;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.Delete;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.Divide;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.DurationGte;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.DurationLt;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.Equal;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.EqualRounded;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.FieldValue;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.Gt;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.Gte;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.Index;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.Inside;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.IntegerDivide;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.IntegerModulus;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.IntegerToFloat;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.IsNull;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.Lt;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.Lte;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.Match;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.Max;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.Member;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.Min;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.Multiple;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.Not;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.Or;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.Ordered;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.PatternMatch;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.Power;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.Product;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.Random;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.RandomEx;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.RecordEx;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.Round;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.RoundTo;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.StringMatch;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.Substring;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.Subtract;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.Sum;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.Truncate;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.math.MathConstant;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.math.MathOperator;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.math.StatsOperator;
import uk.ac.ed.ph.jqtiplus.node.expression.outcome.NumberCorrect;
import uk.ac.ed.ph.jqtiplus.node.expression.outcome.NumberIncorrect;
import uk.ac.ed.ph.jqtiplus.node.expression.outcome.NumberPresented;
import uk.ac.ed.ph.jqtiplus.node.expression.outcome.NumberResponded;
import uk.ac.ed.ph.jqtiplus.node.expression.outcome.NumberSelected;
import uk.ac.ed.ph.jqtiplus.node.expression.outcome.OutcomeMaximum;
import uk.ac.ed.ph.jqtiplus.node.expression.outcome.OutcomeMinimum;
import uk.ac.ed.ph.jqtiplus.node.expression.outcome.TestVariables;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;

import java.util.HashMap;
import java.util.Map;

/**
 * This class creates all supported expressions from given CLASS_TAG.
 * <p>
 * Supported expressions: and, anyN, baseValue, containerSize, contains, correct, customOperator,
 * default, delete, divide, durationGTE, durationLT, equal, equalRounded, fieldValue, gt, gte, index,
 * inside, integerDivide, integerModulus, integerToFloat, isNull, lt, lte, mapResponse, mapResponsePoint,
 * match, member, multiple, not, null, numberCorrect, numberIncorrect, numberPresented, numberResponded,
 * numberSelected, or, ordered, outcomeMaximum, outcomeMinimum, patternMatch, power, product, random,
 * randomFloat, randomInteger, round, stringMatch, substring, subtract, sum, testVariables, truncate,
 * variable, customOperator, mapResponse, mapResponsePoint.
 * <p>
 * Additional expressions: randomFloatEx, randomIntegerEx, randomEx, recordEx
 * 
 * @author Jiri Kajaba
 * @author Jonathon Hare
 */
public enum ExpressionType
{
    /**
     * Creates and expression.
     *
     * @see And
     */
    AND (And.CLASS_TAG, 1, null
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.BOOLEAN}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.BOOLEAN})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new And(parent);
        }
    },

    /**
     * Creates anyN expression.
     *
     * @see AnyN
     */
    ANY_N (AnyN.CLASS_TAG, 1, null
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.BOOLEAN}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.BOOLEAN})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new AnyN(parent);
        }
    },

    /**
     * Creates baseValue expression.
     *
     * @see BaseValue
     */
    BASE_VALUE (BaseValue.CLASS_TAG, null, 0
        , new Cardinality[] {}
        , new BaseType[] {}
        , new Cardinality[] {Cardinality.SINGLE}
        , BaseType.values())
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new BaseValue(parent);
        }
    },

    /**
     * Creates containerSize expression.
     *
     * @see ContainerSize
     */
    CONTAINER_SIZE (ContainerSize.CLASS_TAG, 1, 1
        , new Cardinality[] {Cardinality.MULTIPLE, Cardinality.ORDERED}
        , BaseType.values()
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.INTEGER})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new ContainerSize(parent);
        }
    },

    /**
     * Creates contains expression.
     *
     * @see Contains
     */
    CONTAINS (Contains.CLASS_TAG, 2, 2
        , new Cardinality[] {Cardinality.MULTIPLE, Cardinality.ORDERED}
        , BaseType.values(new BaseType[] {BaseType.DURATION})
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.BOOLEAN})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new Contains(parent);
        }
    },

    /**
     * Creates correct expression.
     *
     * @see Correct
     */
    CORRECT (Correct.CLASS_TAG, null, 0
        , new Cardinality[] {}
        , new BaseType[] {}
        , Cardinality.values()
        , BaseType.values())
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new Correct(parent);
        }
    },

    /**
     * Creates correct expression.
     *
     * @see Correct
     */
    CUSTOM_OPERATOR (CustomOperator.CLASS_TAG, 0, null
        , Cardinality.values()
        , BaseType.values()
        , Cardinality.values()
        , BaseType.values())
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            throw new QTILogicException("customOperators should have been intercepted before this method got called");
        }
    },
    
    /**
     * Creates default expression.
     *
     * @see Default
     */
    DEFAULT (Default.CLASS_TAG, null, 0
        , new Cardinality[] {}
        , new BaseType[] {}
        , Cardinality.values()
        , BaseType.values())
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new Default(parent);
        }
    },

    /**
     * Creates delete expression.
     *
     * @see Delete
     */
    DELETE (Delete.CLASS_TAG, 2, 2
        , new Cardinality[] {Cardinality.SINGLE, Cardinality.MULTIPLE, Cardinality.ORDERED}
        , BaseType.values(new BaseType[] {BaseType.DURATION})
        , new Cardinality[] {Cardinality.MULTIPLE, Cardinality.ORDERED}
        , BaseType.values(new BaseType[] {BaseType.DURATION}))
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new Delete(parent);
        }

        @Override
        public Cardinality[] getRequiredCardinalities(int index)
        {
            switch (index)
            {
                case 0:    return new Cardinality[] {Cardinality.SINGLE};
                case 1:    return new Cardinality[] {Cardinality.MULTIPLE, Cardinality.ORDERED};
                default: return super.getRequiredCardinalities(index);
            }
        }
    },

    /**
     * Creates divide expression.
     *
     * @see Divide
     */
    DIVIDE (Divide.CLASS_TAG, 2, 2
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.INTEGER, BaseType.FLOAT}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.FLOAT})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new Divide(parent);
        }
    },

    /**
     * Creates durationGTE expression.
     *
     * @see DurationGte
     */
    DURATION_GTE (DurationGte.CLASS_TAG, 2, 2
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.DURATION}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.BOOLEAN})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new DurationGte(parent);
        }
    },

    /**
     * Creates durationLT expression.
     *
     * @see DurationLt
     */
    DURATION_LT (DurationLt.CLASS_TAG, 2, 2
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.DURATION}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.BOOLEAN})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new DurationLt(parent);
        }
    },

    /**
     * Creates equal expression.
     *
     * @see Equal
     */
    EQUAL (Equal.CLASS_TAG, 2, 2
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.INTEGER, BaseType.FLOAT}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.BOOLEAN})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new Equal(parent);
        }
    },

    /**
     * Creates equalRounded expression.
     *
     * @see EqualRounded
     */
    EQUAL_ROUNDED (EqualRounded.CLASS_TAG, 2, 2
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.INTEGER, BaseType.FLOAT}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.BOOLEAN})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new EqualRounded(parent);
        }
    },

    /**
     * Creates fieldValue expression.
     *
     * @see FieldValue
     */
    FIELD_VALUE (FieldValue.CLASS_TAG, 1, 1
        , new Cardinality[] {Cardinality.RECORD}
        , BaseType.values()
        , new Cardinality[] {Cardinality.SINGLE}
        , BaseType.values())
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new FieldValue(parent);
        }
    },

    /**
     * Creates gt expression.
     *
     * @see Gt
     */
    GT (Gt.CLASS_TAG, 2, 2
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.INTEGER, BaseType.FLOAT}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.BOOLEAN})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new Gt(parent);
        }
    },

    /**
     * Creates gte expression.
     *
     * @see Gte
     */
    GTE (Gte.CLASS_TAG, 2, 2
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.INTEGER, BaseType.FLOAT}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.BOOLEAN})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new Gte(parent);
        }
    },

    /**
     * Creates index expression.
     *
     * @see Index
     */
    INDEX (Index.CLASS_TAG, 1, 1
        , new Cardinality[] {Cardinality.ORDERED}
        , BaseType.values()
        , new Cardinality[] {Cardinality.SINGLE}
        , BaseType.values())
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new Index(parent);
        }
    },

    /**
     * Creates inside expression.
     *
     * @see Inside
     */
    INSIDE (Inside.CLASS_TAG, 1, 1
        , new Cardinality[] {Cardinality.SINGLE, Cardinality.MULTIPLE, Cardinality.ORDERED}
        , new BaseType[] {BaseType.POINT}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.BOOLEAN})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new Inside(parent);
        }
    },

    /**
     * Creates integerDivide expression.
     *
     * @see IntegerDivide
     */
    INTEGER_DIVIDE (IntegerDivide.CLASS_TAG, 2, 2
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.INTEGER}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.INTEGER})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new IntegerDivide(parent);
        }
    },

    /**
     * Creates integerModulus expression.
     *
     * @see IntegerModulus
     */
    INTEGER_MODULUS (IntegerModulus.CLASS_TAG, 2, 2
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.INTEGER}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.INTEGER})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new IntegerModulus(parent);
        }
    },

    /**
     * Creates integerToFloat expression.
     *
     * @see IntegerToFloat
     */
    INTEGER_TO_FLOAT (IntegerToFloat.CLASS_TAG, 1, 1
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.INTEGER}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.FLOAT})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new IntegerToFloat(parent);
        }
    },

    /**
     * Creates isNull expression.
     *
     * @see IsNull
     */
    IS_NULL (IsNull.CLASS_TAG, 1, 1
        , Cardinality.values()
        , BaseType.values()
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.BOOLEAN})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new IsNull(parent);
        }
    },

    /**
     * Creates lt expression.
     *
     * @see Lt
     */
    LT (Lt.CLASS_TAG, 2, 2
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.INTEGER, BaseType.FLOAT}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.BOOLEAN})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new Lt(parent);
        }
    },

    /**
     * Creates lte expression.
     *
     * @see Lte
     */
    LTE (Lte.CLASS_TAG, 2, 2
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.INTEGER, BaseType.FLOAT}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.BOOLEAN})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new Lte(parent);
        }
    },

    /**
     * Creates mapResponse expression.
     *
     * @see MapResponse
     */
    MAP_RESPONSE (MapResponse.CLASS_TAG, null, 0
        , new Cardinality[] {}
        , new BaseType[] {}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.FLOAT})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new MapResponse(parent);
        }
    },

    /**
     * Creates mapResponsePoint expression.
     *
     * @see MapResponsePoint
     */
    MAP_RESPONSE_POINT (MapResponsePoint.CLASS_TAG, null, 0
        , new Cardinality[] {}
        , new BaseType[] {}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.FLOAT})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new MapResponsePoint(parent);
        }
    },

    /**
     * Creates match expression.
     *
     * @see Match
     */
    MATCH (Match.CLASS_TAG, 2, 2
        , Cardinality.values()
        , BaseType.values(new BaseType[] {BaseType.DURATION})
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.BOOLEAN})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new Match(parent);
        }
    },
    
    /**
     * Creates mathConstant expression.
     *
     * @see MathConstant
     */
    MATH_CONSTANT (MathConstant.CLASS_TAG, 0, 0
        , new Cardinality[] {}
        , new BaseType[] {}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.FLOAT})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new MathConstant(parent);
        }
    },
    
    /**
     * Creates mathOperator expression.
     *
     * @see MathOperator
     */
    MATH_OPERATOR (MathOperator.CLASS_TAG, 1, null
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.INTEGER, BaseType.FLOAT}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.FLOAT})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new MathOperator(parent);
        }
    },
    
    /**
     * Creates max expression.
     *
     * @see Max
     */
    MAX (Max.CLASS_TAG, 1, null
        , new Cardinality[] {Cardinality.SINGLE, Cardinality.MULTIPLE, Cardinality.ORDERED}
        , new BaseType[] {BaseType.INTEGER, BaseType.FLOAT}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.INTEGER, BaseType.FLOAT})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new Max(parent);
        }
    },
    
    /**
     * Creates min expression.
     *
     * @see Min
     */
    MIN (Min.CLASS_TAG, 1, null
        , new Cardinality[] {Cardinality.SINGLE, Cardinality.MULTIPLE, Cardinality.ORDERED}
        , new BaseType[] {BaseType.INTEGER, BaseType.FLOAT}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.INTEGER, BaseType.FLOAT})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new Min(parent);
        }
    },

    /**
     * Creates member expression.
     *
     * @see Member
     */
    MEMBER (Member.CLASS_TAG, 2, 2
        , new Cardinality[] {Cardinality.SINGLE, Cardinality.MULTIPLE, Cardinality.ORDERED}
        , BaseType.values(new BaseType[] {BaseType.DURATION})
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.BOOLEAN})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new Member(parent);
        }

        @Override
        public Cardinality[] getRequiredCardinalities(int index)
        {
            switch (index)
            {
                case 0:    return new Cardinality[] {Cardinality.SINGLE};
                case 1:    return new Cardinality[] {Cardinality.MULTIPLE, Cardinality.ORDERED};
                default: return super.getRequiredCardinalities(index);
            }
        }
    },

    /**
     * Creates multiple expression.
     *
     * @see Multiple
     */
    MULTIPLE (Multiple.CLASS_TAG, null, null
        , new Cardinality[] {Cardinality.SINGLE, Cardinality.MULTIPLE}
        , BaseType.values()
        , new Cardinality[] {Cardinality.MULTIPLE}
        , BaseType.values())
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new Multiple(parent);
        }
    },

    /**
     * Creates not expression.
     *
     * @see Not
     */
    NOT (Not.CLASS_TAG, 1, 1
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.BOOLEAN}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.BOOLEAN})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new Not(parent);
        }
    },

    /**
     * Creates null expression.
     *
     * @see Null
     */
    NULL (Null.CLASS_TAG, null, 0
        , new Cardinality[] {}
        , new BaseType[] {}
        , Cardinality.values()
        , BaseType.values())
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new Null(parent);
        }
    },

    /**
     * Creates numberCorrect expression.
     *
     * @see NumberCorrect
     */
    NUMBER_CORRECT (NumberCorrect.CLASS_TAG, null, 0
        , new Cardinality[] {}
        , new BaseType[] {}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.INTEGER})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new NumberCorrect(parent);
        }
    },

    /**
     * Creates numberIncorrect expression.
     *
     * @see NumberIncorrect
     */
    NUMBER_INCORRECT (NumberIncorrect.CLASS_TAG, null, 0
        , new Cardinality[] {}
        , new BaseType[] {}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.INTEGER})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new NumberIncorrect(parent);
        }
    },

    /**
     * Creates numberPresented expression.
     *
     * @see NumberPresented
     */
    NUMBER_PRESENTED (NumberPresented.CLASS_TAG, null, 0
        , new Cardinality[] {}
        , new BaseType[] {}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.INTEGER})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new NumberPresented(parent);
        }
    },

    /**
     * Creates numberResponded expression.
     *
     * @see NumberResponded
     */
    NUMBER_RESPONDED (NumberResponded.CLASS_TAG, null, 0
        , new Cardinality[] {}
        , new BaseType[] {}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.INTEGER})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new NumberResponded(parent);
        }
    },

    /**
     * Creates numberSelected expression.
     *
     * @see NumberSelected
     */
    NUMBER_SELECTED (NumberSelected.CLASS_TAG, null, 0
        , new Cardinality[] {}
        , new BaseType[] {}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.INTEGER})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new NumberSelected(parent);
        }
    },

    /**
     * Creates or expression.
     *
     * @see Or
     */
    OR (Or.CLASS_TAG, 1, null
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.BOOLEAN}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.BOOLEAN})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new Or(parent);
        }
    },

    /**
     * Creates outcomeMaximum expression.
     *
     * @see OutcomeMaximum
     */
    OUTCOME_MAXIMUM (OutcomeMaximum.CLASS_TAG, null, 0
        , new Cardinality[] {}
        , new BaseType[] {}
        , new Cardinality[] {Cardinality.MULTIPLE}
        , new BaseType[] {BaseType.FLOAT})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new OutcomeMaximum(parent);
        }
    },

    /**
     * Creates outcomeMinimum expression.
     *
     * @see OutcomeMinimum
     */
    OUTCOME_MINIMUM (OutcomeMinimum.CLASS_TAG, null, 0
        , new Cardinality[] {}
        , new BaseType[] {}
        , new Cardinality[] {Cardinality.MULTIPLE}
        , new BaseType[] {BaseType.FLOAT})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new OutcomeMinimum(parent);
        }
    },

    /**
     * Creates ordered expression.
     *
     * @see Ordered
     */
    ORDERED (Ordered.CLASS_TAG, null, null
        , new Cardinality[] {Cardinality.SINGLE, Cardinality.ORDERED}
        , BaseType.values()
        , new Cardinality[] {Cardinality.ORDERED}
        , BaseType.values())
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new Ordered(parent);
        }
    },

    /**
     * Creates patternMatch expression.
     *
     * @see PatternMatch
     */
    PATTERN_MATCH (PatternMatch.CLASS_TAG, 1, 1
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.STRING}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.BOOLEAN})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new PatternMatch(parent);
        }
    },

    /**
     * Creates power expression.
     *
     * @see Power
     */
    POWER (Power.CLASS_TAG, 2, 2
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.INTEGER, BaseType.FLOAT}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.FLOAT})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new Power(parent);
        }
    },

    /**
     * Creates product expression.
     *
     * @see Product
     */
    PRODUCT (Product.CLASS_TAG, 1, null
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.INTEGER, BaseType.FLOAT}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.INTEGER, BaseType.FLOAT})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new Product(parent);
        }
    },

    /**
     * Creates random expression.
     *
     * @see Random
     */
    RANDOM (Random.CLASS_TAG, 1, 1
        , new Cardinality[] {Cardinality.MULTIPLE, Cardinality.ORDERED}
        , BaseType.values()
        , new Cardinality[] {Cardinality.SINGLE}
        , BaseType.values())
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new Random(parent);
        }
    },

    /**
     * Creates randomEx expression.
     *
     * @see RandomEx
     */
    RANDOM_EX (RandomEx.CLASS_TAG, 1, 1
        , new Cardinality[] {Cardinality.MULTIPLE, Cardinality.ORDERED}
        , BaseType.values()
        , new Cardinality[] {Cardinality.SINGLE}
        , BaseType.values())
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new RandomEx(parent);
        }
    },

    /**
     * Creates randomFloat expression.
     *
     * @see RandomFloat
     */
    RANDOM_FLOAT (RandomFloat.CLASS_TAG, null, 0
        , new Cardinality[] {}
        , new BaseType[] {}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.FLOAT})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new RandomFloat(parent);
        }
    },

    /**
     * Creates randomFloatEx expression.
     *
     * @see RandomFloatEx
     */
    RANDOM_FLOAT_EX (RandomFloatEx.CLASS_TAG, null, 0
        , new Cardinality[] {}
        , new BaseType[] {}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.FLOAT})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new RandomFloatEx(parent);
        }
    },

    /**
     * Creates randomInteger expression.
     *
     * @see RandomInteger
     */
    RANDOM_INTEGER (RandomInteger.CLASS_TAG, null, 0
        , new Cardinality[] {}
        , new BaseType[] {}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.INTEGER})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new RandomInteger(parent);
        }
    },

    /**
     * Creates randomIntegerEx expression.
     *
     * @see RandomIntegerEx
     */
    RANDOM_INTEGER_EX (RandomIntegerEx.CLASS_TAG, null, 0
        , new Cardinality[] {}
        , new BaseType[] {}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.INTEGER})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new RandomIntegerEx(parent);
        }
    },

    /**
     * Creates recordEx expression.
     *
     * @see RecordEx
     */
    RECORD_EX (RecordEx.CLASS_TAG, null, null
        , new Cardinality[] {Cardinality.SINGLE}
        , BaseType.values()
        , new Cardinality[] {Cardinality.RECORD}
        , BaseType.values())
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new RecordEx(parent);
        }
    },

    /**
     * Creates round expression.
     *
     * @see Round
     */
    ROUND (Round.CLASS_TAG, 1, 1
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.FLOAT}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.INTEGER})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new Round(parent);
        }
    },
    
    /**
     * Creates roundTo expression.
     *
     * @see RoundTo
     */
    ROUND_TO (RoundTo.CLASS_TAG, 1, 1
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.FLOAT, BaseType.INTEGER}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.FLOAT})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new RoundTo(parent);
        }
    },
    
    /**
     * Creates statsOperator expression.
     *
     * @see StatsOperator
     */
    STATS_OPERATOR (StatsOperator.CLASS_TAG, 1, 1
        , new Cardinality[] {Cardinality.MULTIPLE, Cardinality.ORDERED}
        , new BaseType[] {BaseType.INTEGER, BaseType.FLOAT}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.FLOAT})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new StatsOperator(parent);
        }
    },

    /**
     * Creates stringMatch expression.
     *
     * @see StringMatch
     */
    STRING_MATCH (StringMatch.CLASS_TAG, 2, 2
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.STRING}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.BOOLEAN})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new StringMatch(parent);
        }
    },

    /**
     * Creates substring expression.
     *
     * @see Substring
     */
    SUBSTRING (Substring.CLASS_TAG, 2, 2
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.STRING}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.BOOLEAN})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new Substring(parent);
        }
    },

    /**
     * Creates subtract expression.
     *
     * @see Subtract
     */
    SUBTRACT (Subtract.CLASS_TAG, 2, 2
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.INTEGER, BaseType.FLOAT}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.INTEGER, BaseType.FLOAT})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new Subtract(parent);
        }
    },

    /**
     * Creates sum expression.
     *
     * @see Sum
     */
    SUM (Sum.CLASS_TAG, 1, null
        , new Cardinality[] {Cardinality.SINGLE, Cardinality.MULTIPLE, Cardinality.ORDERED}
        , new BaseType[] {BaseType.INTEGER, BaseType.FLOAT}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.INTEGER, BaseType.FLOAT})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new Sum(parent);
        }
    },

    /**
     * Creates testVariables expression.
     *
     * @see TestVariables
     */
    TEST_VARIABLES (TestVariables.CLASS_TAG, null, 0
        , new Cardinality[] {}
        , new BaseType[] {}
        , new Cardinality[] {Cardinality.MULTIPLE}
        , BaseType.values())
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new TestVariables(parent);
        }
    },

    /**
     * Creates truncate expression.
     *
     * @see Truncate
     */
    TRUNCATE (Truncate.CLASS_TAG, 1, 1
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.FLOAT}
        , new Cardinality[] {Cardinality.SINGLE}
        , new BaseType[] {BaseType.INTEGER})
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new Truncate(parent);
        }
    },

    /**
     * Creates variable expression.
     *
     * @see Variable
     */
    VARIABLE (Variable.CLASS_TAG, null, 0
        , new Cardinality[] {}
        , new BaseType[] {}
        , Cardinality.values()
        , BaseType.values())
    {
        @Override
        public Expression create(ExpressionParent parent)
        {
            return new Variable(parent);
        }
    };

    private static Map<String, ExpressionType> expressionTypes;

    static
    {
        expressionTypes = new HashMap<String, ExpressionType>();

        for (ExpressionType expressionType : ExpressionType.values())
            expressionTypes.put(expressionType.expressionType, expressionType);
    }

    private String expressionType;
    private Integer minimum;
    private Integer maximum;
    private Cardinality[] requiredCardinalities;
    private BaseType[] requiredBaseTypes;
    private Cardinality[] producedCardinalities;
    private BaseType[] producedBaseTypes;

    private ExpressionType
        ( String expressionType
        , Integer minimum
        , Integer maximum
        , Cardinality[] requiredCardinalities
        , BaseType[] requiredBaseTypes
        , Cardinality[] producedCardinalities
        , BaseType[] producedBaseTypes )
    {
        this.expressionType = expressionType;
        this.minimum = minimum;
        this.maximum = maximum;
        this.requiredCardinalities = requiredCardinalities;
        this.requiredBaseTypes = requiredBaseTypes;
        this.producedCardinalities = producedCardinalities;
        this.producedBaseTypes = producedBaseTypes;
    }

    /**
     * Gets CLASS_TAG of this expression type.
     *
     * @return CLASS_TAG of this expression type
     */
    public String getClassTag()
    {
        return expressionType;
    }

    /**
     * Gets minimum required children or null.
     *
     * @return minimum required children or null
     */
    public Integer getMinimum()
    {
        return minimum;
    }

    /**
     * Gets maximum allowed children or null.
     *
     * @return maximum allowed children or null
     */
    public Integer getMaximum()
    {
        return maximum;
    }

    /**
     * Gets list of all acceptable cardinalities for given position.
     *
     * @param index position
     * @return list of all acceptable cardinalities for given position
     * @see ExpressionParent#getRequiredCardinalities(ValidationContext, int)
     */
    public Cardinality[] getRequiredCardinalities(int index)
    {
        return requiredCardinalities;
    }

    /**
     * Gets list of all acceptable baseTypes for given position.
     *
     * @param index position
     * @return list of all acceptable baseTypes for given position
     * @see ExpressionParent#getRequiredBaseTypes(ValidationContext, int)
     */
    public BaseType[] getRequiredBaseTypes(int index)
    {
        return requiredBaseTypes;
    }

    /**
     * Gets list of all produced cardinalities.
     *
     * @return list of all produced cardinalities
     * @see Expression#getProducedCardinalities(ValidationContext)
     */
    public Cardinality[] getProducedCardinalities()
    {
        return producedCardinalities;
    }

    /**
     * Gets list of all produced baseTypes.
     *
     * @return list of all produced baseTypes
     * @see Expression#getProducedBaseTypes(ValidationContext)
     */
    public BaseType[] getProducedBaseTypes()
    {
        return producedBaseTypes;
    }

    /**
     * Creates expression.
     *
     * @param parent parent of created expression
     * @return created expression
     */
    public abstract Expression create(ExpressionParent parent);

    @Override
    public String toString()
    {
        return expressionType;
    }

    /**
     * Gets expression type for given CLASS_TAG.
     *
     * @param classTag CLASS_TAG
     * @return expression type for given CLASS_TAG
     */
    public static ExpressionType getType(String classTag) {
        return expressionTypes.get(classTag);
    }

@ToRemove
//    /**
//     * Gets all supported expression types for given requirements.
//     * Every returned expression type produces at least on required object.
//     *
//     * @param requiredCardinalities required cardinalities
//     * @param requiredBaseTypes required baseTypes
//     * @return all supported expression types for given requirements
//     */
//    public static ExpressionType[] getSupportedTypes(Cardinality[] requiredCardinalities, BaseType[] requiredBaseTypes)
//    {
//        List<ExpressionType> types = new ArrayList<ExpressionType>();
//
//        for (ExpressionType type : ExpressionType.values())
//        {
//            boolean cardinalityFound = false;
//
//            for (Cardinality producedCardinality : type.getProducedCardinalities())
//                if (Arrays.binarySearch(requiredCardinalities, producedCardinality) >= 0)
//                {
//                    cardinalityFound = true;
//                    break;
//                }
//
//            boolean baseTypeFound = false;
//
//            for (BaseType producedBaseType : type.getProducedBaseTypes())
//                if (Arrays.binarySearch(requiredBaseTypes, producedBaseType) >= 0)
//                {
//                    baseTypeFound = true;
//                    break;
//                }
//
//            if (cardinalityFound && baseTypeFound)
//                types.add(type);
//        }
//
//        return types.toArray(new ExpressionType[] {});
//    }

    /**
     * Creates expression.
     *
     * @param parent parent of created expression
     * @param classTag CLASS_TAG of created expression
     * @return created expression
     */
    public static Expression getInstance(ExpressionParent parent, String classTag)
    {
        ExpressionType expressionType = expressionTypes.get(classTag);

        if (expressionType == null) {
            throw new QTIIllegalChildException(parent, classTag);
        }

        return expressionType.create(parent);
    }
}
