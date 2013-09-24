/* Copyright (c) 2012-2013, University of Edinburgh.
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
package uk.ac.ed.ph.jqtiplus.node.expression;

import uk.ac.ed.ph.jqtiplus.exception.QtiIllegalChildException;
import uk.ac.ed.ph.jqtiplus.exception.QtiLogicException;
import uk.ac.ed.ph.jqtiplus.node.expression.general.BaseValue;
import uk.ac.ed.ph.jqtiplus.node.expression.general.Correct;
import uk.ac.ed.ph.jqtiplus.node.expression.general.Default;
import uk.ac.ed.ph.jqtiplus.node.expression.general.MapResponse;
import uk.ac.ed.ph.jqtiplus.node.expression.general.MapResponsePoint;
import uk.ac.ed.ph.jqtiplus.node.expression.general.Null;
import uk.ac.ed.ph.jqtiplus.node.expression.general.RandomFloat;
import uk.ac.ed.ph.jqtiplus.node.expression.general.RandomInteger;
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
import uk.ac.ed.ph.jqtiplus.node.expression.operator.Gcd;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.Gt;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.Gte;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.Index;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.Inside;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.IntegerDivide;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.IntegerModulus;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.IntegerToFloat;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.IsNull;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.Lcm;
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
import uk.ac.ed.ph.jqtiplus.node.expression.operator.RecordEx;
import uk.ac.ed.ph.jqtiplus.node.expression.operator.Repeat;
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
import uk.ac.ed.ph.jqtiplus.validation.ValidationContext;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class creates all supported expressions from given QTI_CLASS_NAME.
 * <p>
 * Supported expressions: and, anyN, baseValue, containerSize, contains, correct, customOperator, default, delete, divide, durationGTE, durationLT, equal,
 * equalRounded, fieldValue, gcd, gt, gte, index, inside, integerDivide, integerModulus, integerToFloat, isNull, lcm, lt, lte, mapResponse, mapResponsePoint, match,
 * member, multiple, not, null, numberCorrect, numberIncorrect, numberPresented, numberResponded, numberSelected, or, ordered, outcomeMaximum, outcomeMinimum,
 * patternMatch, power, product, random, randomFloat, randomInteger, round, stringMatch, substring, subtract, sum, testVariables, truncate, variable,
 * customOperator, mapResponse, mapResponsePoint.
 * <p>
 * Additional expressions: randomFloatEx, randomIntegerEx, randomEx, recordEx
 *
 * @author Jiri Kajaba
 * @author Jonathon Hare
 */
public enum ExpressionType {
    /**
     * Creates and expression.
     *
     * @see And
     */
    AND(And.QTI_CLASS_NAME, 1, null
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.BOOLEAN }
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.BOOLEAN }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new And(parent);
        }
    },

    /**
     * Creates anyN expression.
     *
     * @see AnyN
     */
    ANY_N(AnyN.QTI_CLASS_NAME, 1, null
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.BOOLEAN }
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.BOOLEAN }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new AnyN(parent);
        }
    },

    /**
     * Creates baseValue expression.
     *
     * @see BaseValue
     */
    BASE_VALUE(BaseValue.QTI_CLASS_NAME, 0, 0
            , new Cardinality[] {}
            , new BaseType[] {}
            , new Cardinality[] { Cardinality.SINGLE }
            , BaseType.values()) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new BaseValue(parent);
        }
    },

    /**
     * Creates containerSize expression.
     *
     * @see ContainerSize
     */
    CONTAINER_SIZE(ContainerSize.QTI_CLASS_NAME, 1, 1
            , new Cardinality[] { Cardinality.MULTIPLE, Cardinality.ORDERED }
            , BaseType.values()
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.INTEGER }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new ContainerSize(parent);
        }
    },

    /**
     * Creates contains expression.
     *
     * @see Contains
     */
    CONTAINS(Contains.QTI_CLASS_NAME, 2, 2
            , new Cardinality[] { Cardinality.MULTIPLE, Cardinality.ORDERED }
            , BaseType.except(new BaseType[] { BaseType.DURATION })
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.BOOLEAN }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new Contains(parent);
        }
    },

    /**
     * Creates correct expression.
     *
     * @see Correct
     */
    CORRECT(Correct.QTI_CLASS_NAME, 0, 0
            , new Cardinality[] {}
            , new BaseType[] {}
            , Cardinality.values()
            , BaseType.values()) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new Correct(parent);
        }
    },

    /**
     * Creates correct expression.
     *
     * @see Correct
     */
    CUSTOM_OPERATOR(CustomOperator.QTI_CLASS_NAME, 0, null
            , Cardinality.values()
            , BaseType.values()
            , Cardinality.values()
            , BaseType.values()) {

        @Override
        public Expression create(final ExpressionParent parent) {
            throw new QtiLogicException("customOperators should have been intercepted before this method got called");
        }
    },

    /**
     * Creates default expression.
     *
     * @see Default
     */
    DEFAULT(Default.QTI_CLASS_NAME, 0, 0
            , new Cardinality[] {}
            , new BaseType[] {}
            , Cardinality.values()
            , BaseType.values()) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new Default(parent);
        }
    },

    /**
     * Creates delete expression.
     *
     * @see Delete
     */
    DELETE(Delete.QTI_CLASS_NAME, 2, 2
            , new Cardinality[] { Cardinality.SINGLE, Cardinality.MULTIPLE, Cardinality.ORDERED }
            , BaseType.except(new BaseType[] { BaseType.DURATION })
            , new Cardinality[] { Cardinality.MULTIPLE, Cardinality.ORDERED }
            , BaseType.except(new BaseType[] { BaseType.DURATION })) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new Delete(parent);
        }

        @Override
        public Cardinality[] getRequiredCardinalities(final int index) {
            switch (index) {
                case 0:
                    return new Cardinality[] { Cardinality.SINGLE };
                case 1:
                    return new Cardinality[] { Cardinality.MULTIPLE, Cardinality.ORDERED };
                default:
                    return super.getRequiredCardinalities(index);
            }
        }
    },

    /**
     * Creates divide expression.
     *
     * @see Divide
     */
    DIVIDE(Divide.QTI_CLASS_NAME, 2, 2
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.INTEGER, BaseType.FLOAT }
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.FLOAT }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new Divide(parent);
        }
    },

    /**
     * Creates durationGTE expression.
     *
     * @see DurationGte
     */
    DURATION_GTE(DurationGte.QTI_CLASS_NAME, 2, 2
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.DURATION }
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.BOOLEAN }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new DurationGte(parent);
        }
    },

    /**
     * Creates durationLT expression.
     *
     * @see DurationLt
     */
    DURATION_LT(DurationLt.QTI_CLASS_NAME, 2, 2
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.DURATION }
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.BOOLEAN }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new DurationLt(parent);
        }
    },

    /**
     * Creates equal expression.
     *
     * @see Equal
     */
    EQUAL(Equal.QTI_CLASS_NAME, 2, 2
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.INTEGER, BaseType.FLOAT }
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.BOOLEAN }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new Equal(parent);
        }
    },

    /**
     * Creates equalRounded expression.
     *
     * @see EqualRounded
     */
    EQUAL_ROUNDED(EqualRounded.QTI_CLASS_NAME, 2, 2
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.INTEGER, BaseType.FLOAT }
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.BOOLEAN }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new EqualRounded(parent);
        }
    },

    /**
     * Creates fieldValue expression.
     *
     * @see FieldValue
     */
    FIELD_VALUE(FieldValue.QTI_CLASS_NAME, 1, 1
            , new Cardinality[] { Cardinality.RECORD }
            , BaseType.values()
            , new Cardinality[] { Cardinality.SINGLE }
            , BaseType.values()) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new FieldValue(parent);
        }
    },

    /**
     * Creates gcd expression.
     *
     * @see Gcd
     */
    GCD(Gcd.QTI_CLASS_NAME, 1, null
            , new Cardinality[] { Cardinality.SINGLE, Cardinality.MULTIPLE, Cardinality.ORDERED }
            , new BaseType[] { BaseType.INTEGER }
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.INTEGER }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new Gcd(parent);
        }
    },

    /**
     * Creates gt expression.
     *
     * @see Gt
     */
    GT(Gt.QTI_CLASS_NAME, 2, 2
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.INTEGER, BaseType.FLOAT }
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.BOOLEAN }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new Gt(parent);
        }
    },

    /**
     * Creates gte expression.
     *
     * @see Gte
     */
    GTE(Gte.QTI_CLASS_NAME, 2, 2
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.INTEGER, BaseType.FLOAT }
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.BOOLEAN }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new Gte(parent);
        }
    },

    /**
     * Creates index expression.
     *
     * @see Index
     */
    INDEX(Index.QTI_CLASS_NAME, 1, 1
            , new Cardinality[] { Cardinality.ORDERED }
            , BaseType.values()
            , new Cardinality[] { Cardinality.SINGLE }
            , BaseType.values()) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new Index(parent);
        }
    },

    /**
     * Creates inside expression.
     *
     * @see Inside
     */
    INSIDE(Inside.QTI_CLASS_NAME, 1, 1
            , new Cardinality[] { Cardinality.SINGLE, Cardinality.MULTIPLE, Cardinality.ORDERED }
            , new BaseType[] { BaseType.POINT }
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.BOOLEAN }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new Inside(parent);
        }
    },

    /**
     * Creates integerDivide expression.
     *
     * @see IntegerDivide
     */
    INTEGER_DIVIDE(IntegerDivide.QTI_CLASS_NAME, 2, 2
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.INTEGER }
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.INTEGER }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new IntegerDivide(parent);
        }
    },

    /**
     * Creates integerModulus expression.
     *
     * @see IntegerModulus
     */
    INTEGER_MODULUS(IntegerModulus.QTI_CLASS_NAME, 2, 2
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.INTEGER }
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.INTEGER }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new IntegerModulus(parent);
        }
    },

    /**
     * Creates integerToFloat expression.
     *
     * @see IntegerToFloat
     */
    INTEGER_TO_FLOAT(IntegerToFloat.QTI_CLASS_NAME, 1, 1
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.INTEGER }
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.FLOAT }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new IntegerToFloat(parent);
        }
    },

    /**
     * Creates isNull expression.
     *
     * @see IsNull
     */
    IS_NULL(IsNull.QTI_CLASS_NAME, 1, 1
            , Cardinality.values()
            , BaseType.values()
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.BOOLEAN }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new IsNull(parent);
        }
    },

    /**
     * Creates lcm expression.
     *
     * @see Lcm
     */
    LCM(Lcm.QTI_CLASS_NAME, 1, null
            , new Cardinality[] { Cardinality.SINGLE, Cardinality.MULTIPLE, Cardinality.ORDERED }
            , new BaseType[] { BaseType.INTEGER }
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.INTEGER }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new Lcm(parent);
        }
    },

    /**
     * Creates lt expression.
     *
     * @see Lt
     */
    LT(Lt.QTI_CLASS_NAME, 2, 2
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.INTEGER, BaseType.FLOAT }
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.BOOLEAN }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new Lt(parent);
        }
    },

    /**
     * Creates lte expression.
     *
     * @see Lte
     */
    LTE(Lte.QTI_CLASS_NAME, 2, 2
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.INTEGER, BaseType.FLOAT }
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.BOOLEAN }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new Lte(parent);
        }
    },

    /**
     * Creates mapResponse expression.
     *
     * @see MapResponse
     */
    MAP_RESPONSE(MapResponse.QTI_CLASS_NAME, 0, 0
            , new Cardinality[] {}
            , new BaseType[] {}
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.FLOAT }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new MapResponse(parent);
        }
    },

    /**
     * Creates mapResponsePoint expression.
     *
     * @see MapResponsePoint
     */
    MAP_RESPONSE_POINT(MapResponsePoint.QTI_CLASS_NAME, 0, 0
            , new Cardinality[] {}
            , new BaseType[] {}
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.FLOAT }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new MapResponsePoint(parent);
        }
    },

    /**
     * Creates match expression.
     *
     * @see Match
     */
    MATCH(Match.QTI_CLASS_NAME, 2, 2
            , Cardinality.values()
            , BaseType.except(new BaseType[] { BaseType.DURATION })
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.BOOLEAN }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new Match(parent);
        }
    },

    /**
     * Creates mathConstant expression.
     *
     * @see MathConstant
     */
    MATH_CONSTANT(MathConstant.QTI_CLASS_NAME, 0, 0
            , new Cardinality[] {}
            , new BaseType[] {}
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.FLOAT }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new MathConstant(parent);
        }
    },

    /**
     * Creates mathOperator expression.
     *
     * @see MathOperator
     */
    MATH_OPERATOR(MathOperator.QTI_CLASS_NAME, 1, null
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.INTEGER, BaseType.FLOAT }
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.FLOAT }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new MathOperator(parent);
        }
    },

    /**
     * Creates max expression.
     *
     * @see Max
     */
    MAX(Max.QTI_CLASS_NAME, 1, null
            , new Cardinality[] { Cardinality.SINGLE, Cardinality.MULTIPLE, Cardinality.ORDERED }
            , new BaseType[] { BaseType.INTEGER, BaseType.FLOAT }
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.INTEGER, BaseType.FLOAT }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new Max(parent);
        }
    },

    /**
     * Creates min expression.
     *
     * @see Min
     */
    MIN(Min.QTI_CLASS_NAME, 1, null
            , new Cardinality[] { Cardinality.SINGLE, Cardinality.MULTIPLE, Cardinality.ORDERED }
            , new BaseType[] { BaseType.INTEGER, BaseType.FLOAT }
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.INTEGER, BaseType.FLOAT }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new Min(parent);
        }
    },

    /**
     * Creates member expression.
     *
     * @see Member
     */
    MEMBER(Member.QTI_CLASS_NAME, 2, 2
            , new Cardinality[] { Cardinality.SINGLE, Cardinality.MULTIPLE, Cardinality.ORDERED }
            , BaseType.except(new BaseType[] { BaseType.DURATION })
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.BOOLEAN }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new Member(parent);
        }

        @Override
        public Cardinality[] getRequiredCardinalities(final int index) {
            switch (index) {
                case 0:
                    return new Cardinality[] { Cardinality.SINGLE };
                case 1:
                    return new Cardinality[] { Cardinality.MULTIPLE, Cardinality.ORDERED };
                default:
                    return super.getRequiredCardinalities(index);
            }
        }
    },

    /**
     * Creates multiple expression.
     *
     * @see Multiple
     */
    MULTIPLE(Multiple.QTI_CLASS_NAME, 0, null
            , new Cardinality[] { Cardinality.SINGLE, Cardinality.MULTIPLE }
            , BaseType.values()
            , new Cardinality[] { Cardinality.MULTIPLE }
            , BaseType.values()) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new Multiple(parent);
        }
    },

    /**
     * Creates not expression.
     *
     * @see Not
     */
    NOT(Not.QTI_CLASS_NAME, 1, 1
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.BOOLEAN }
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.BOOLEAN }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new Not(parent);
        }
    },

    /**
     * Creates null expression.
     *
     * @see Null
     */
    NULL(Null.QTI_CLASS_NAME, 0, 0
            , new Cardinality[] {}
            , new BaseType[] {}
            , Cardinality.values()
            , BaseType.values()) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new Null(parent);
        }
    },

    /**
     * Creates numberCorrect expression.
     *
     * @see NumberCorrect
     */
    NUMBER_CORRECT(NumberCorrect.QTI_CLASS_NAME, 0, 0
            , new Cardinality[] {}
            , new BaseType[] {}
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.INTEGER }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new NumberCorrect(parent);
        }
    },

    /**
     * Creates numberIncorrect expression.
     *
     * @see NumberIncorrect
     */
    NUMBER_INCORRECT(NumberIncorrect.QTI_CLASS_NAME, 0, 0
            , new Cardinality[] {}
            , new BaseType[] {}
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.INTEGER }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new NumberIncorrect(parent);
        }
    },

    /**
     * Creates numberPresented expression.
     *
     * @see NumberPresented
     */
    NUMBER_PRESENTED(NumberPresented.QTI_CLASS_NAME, 0, 0
            , new Cardinality[] {}
            , new BaseType[] {}
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.INTEGER }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new NumberPresented(parent);
        }
    },

    /**
     * Creates numberResponded expression.
     *
     * @see NumberResponded
     */
    NUMBER_RESPONDED(NumberResponded.QTI_CLASS_NAME, 0, 0
            , new Cardinality[] {}
            , new BaseType[] {}
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.INTEGER }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new NumberResponded(parent);
        }
    },

    /**
     * Creates numberSelected expression.
     *
     * @see NumberSelected
     */
    NUMBER_SELECTED(NumberSelected.QTI_CLASS_NAME, 0, 0
            , new Cardinality[] {}
            , new BaseType[] {}
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.INTEGER }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new NumberSelected(parent);
        }
    },

    /**
     * Creates or expression.
     *
     * @see Or
     */
    OR(Or.QTI_CLASS_NAME, 1, null
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.BOOLEAN }
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.BOOLEAN }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new Or(parent);
        }
    },

    /**
     * Creates outcomeMaximum expression.
     *
     * @see OutcomeMaximum
     */
    OUTCOME_MAXIMUM(OutcomeMaximum.QTI_CLASS_NAME, 0, 0
            , new Cardinality[] {}
            , new BaseType[] {}
            , new Cardinality[] { Cardinality.MULTIPLE }
            , new BaseType[] { BaseType.FLOAT }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new OutcomeMaximum(parent);
        }
    },

    /**
     * Creates outcomeMinimum expression.
     *
     * @see OutcomeMinimum
     */
    OUTCOME_MINIMUM(OutcomeMinimum.QTI_CLASS_NAME, 0, 0
            , new Cardinality[] {}
            , new BaseType[] {}
            , new Cardinality[] { Cardinality.MULTIPLE }
            , new BaseType[] { BaseType.FLOAT }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new OutcomeMinimum(parent);
        }
    },

    /**
     * Creates ordered expression.
     *
     * @see Ordered
     */
    ORDERED(Ordered.QTI_CLASS_NAME, 0, null
            , new Cardinality[] { Cardinality.SINGLE, Cardinality.ORDERED }
            , BaseType.values()
            , new Cardinality[] { Cardinality.ORDERED }
            , BaseType.values()) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new Ordered(parent);
        }
    },

    /**
     * Creates patternMatch expression.
     *
     * @see PatternMatch
     */
    PATTERN_MATCH(PatternMatch.QTI_CLASS_NAME, 1, 1
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.STRING }
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.BOOLEAN }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new PatternMatch(parent);
        }
    },

    /**
     * Creates power expression.
     *
     * @see Power
     */
    POWER(Power.QTI_CLASS_NAME, 2, 2
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.INTEGER, BaseType.FLOAT }
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.FLOAT }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new Power(parent);
        }
    },

    /**
     * Creates product expression.
     *
     * @see Product
     */
    PRODUCT(Product.QTI_CLASS_NAME, 1, null
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.INTEGER, BaseType.FLOAT }
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.INTEGER, BaseType.FLOAT }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new Product(parent);
        }
    },

    /**
     * Creates random expression.
     *
     * @see Random
     */
    RANDOM(Random.QTI_CLASS_NAME, 1, 1
            , new Cardinality[] { Cardinality.MULTIPLE, Cardinality.ORDERED }
            , BaseType.values()
            , new Cardinality[] { Cardinality.SINGLE }
            , BaseType.values()) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new Random(parent);
        }
    },

    /**
     * Creates randomFloat expression.
     *
     * @see RandomFloat
     */
    RANDOM_FLOAT(RandomFloat.QTI_CLASS_NAME, 0, 0
            , new Cardinality[] {}
            , new BaseType[] {}
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.FLOAT }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new RandomFloat(parent);
        }
    },

    /**
     * Creates randomInteger expression.
     *
     * @see RandomInteger
     */
    RANDOM_INTEGER(RandomInteger.QTI_CLASS_NAME, 0, 0
            , new Cardinality[] {}
            , new BaseType[] {}
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.INTEGER }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new RandomInteger(parent);
        }
    },

    /**
     * Creates recordEx expression.
     *
     * @see RecordEx
     */
    RECORD_EX(RecordEx.QTI_CLASS_NAME, 0, null
            , new Cardinality[] { Cardinality.SINGLE }
            , BaseType.values()
            , new Cardinality[] { Cardinality.RECORD }
            , BaseType.values()) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new RecordEx(parent);
        }
    },

    /**
     * Creates repeat expression.
     *
     * @see Repeat
     */
    REPEAT(Repeat.QTI_CLASS_NAME, 0, null
            , new Cardinality[] { Cardinality.SINGLE }
            , BaseType.values()
            , new Cardinality[] { Cardinality.ORDERED }
            , BaseType.values()) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new Repeat(parent);
        }
    },

    /**
     * Creates round expression.
     *
     * @see Round
     */
    ROUND(Round.QTI_CLASS_NAME, 1, 1
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.FLOAT }
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.INTEGER }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new Round(parent);
        }
    },

    /**
     * Creates roundTo expression.
     *
     * @see RoundTo
     */
    ROUND_TO(RoundTo.QTI_CLASS_NAME, 1, 1
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.FLOAT, BaseType.INTEGER }
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.FLOAT }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new RoundTo(parent);
        }
    },

    /**
     * Creates statsOperator expression.
     *
     * @see StatsOperator
     */
    STATS_OPERATOR(StatsOperator.QTI_CLASS_NAME, 1, 1
            , new Cardinality[] { Cardinality.MULTIPLE, Cardinality.ORDERED }
            , new BaseType[] { BaseType.INTEGER, BaseType.FLOAT }
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.FLOAT }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new StatsOperator(parent);
        }
    },

    /**
     * Creates stringMatch expression.
     *
     * @see StringMatch
     */
    STRING_MATCH(StringMatch.QTI_CLASS_NAME, 2, 2
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.STRING }
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.BOOLEAN }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new StringMatch(parent);
        }
    },

    /**
     * Creates substring expression.
     *
     * @see Substring
     */
    SUBSTRING(Substring.QTI_CLASS_NAME, 2, 2
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.STRING }
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.BOOLEAN }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new Substring(parent);
        }
    },

    /**
     * Creates subtract expression.
     *
     * @see Subtract
     */
    SUBTRACT(Subtract.QTI_CLASS_NAME, 2, 2
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.INTEGER, BaseType.FLOAT }
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.INTEGER, BaseType.FLOAT }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new Subtract(parent);
        }
    },

    /**
     * Creates sum expression.
     *
     * @see Sum
     */
    SUM(Sum.QTI_CLASS_NAME, 1, null
            , new Cardinality[] { Cardinality.SINGLE, Cardinality.MULTIPLE, Cardinality.ORDERED }
            , new BaseType[] { BaseType.INTEGER, BaseType.FLOAT }
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.INTEGER, BaseType.FLOAT }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new Sum(parent);
        }
    },

    /**
     * Creates testVariables expression.
     *
     * @see TestVariables
     */
    TEST_VARIABLES(TestVariables.QTI_CLASS_NAME, 0, 0
            , new Cardinality[] {}
            , new BaseType[] {}
            , new Cardinality[] { Cardinality.MULTIPLE }
            , BaseType.values()) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new TestVariables(parent);
        }
    },

    /**
     * Creates truncate expression.
     *
     * @see Truncate
     */
    TRUNCATE(Truncate.QTI_CLASS_NAME, 1, 1
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.FLOAT }
            , new Cardinality[] { Cardinality.SINGLE }
            , new BaseType[] { BaseType.INTEGER }) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new Truncate(parent);
        }
    },

    /**
     * Creates variable expression.
     *
     * @see Variable
     */
    VARIABLE(Variable.QTI_CLASS_NAME, 0, 0
            , new Cardinality[] {}
            , new BaseType[] {}
            , Cardinality.values()
            , BaseType.values()) {

        @Override
        public Expression create(final ExpressionParent parent) {
            return new Variable(parent);
        }
    };

    private static Map<String, ExpressionType> expressionTypes;

    static {
        expressionTypes = new HashMap<String, ExpressionType>();
        for (final ExpressionType expressionType : ExpressionType.values()) {
            expressionTypes.put(expressionType.expressionType, expressionType);
        }
    }

    private final String expressionType;
    private final int minimum;
    private final Integer maximum;
    private final Cardinality[] requiredCardinalities;
    private final BaseType[] requiredBaseTypes;
    private final Cardinality[] producedCardinalities;
    private final BaseType[] producedBaseTypes;

    private ExpressionType(final String expressionType, final int minimum, final Integer maximum,
            final Cardinality[] requiredCardinalities, final BaseType[] requiredBaseTypes,
            final Cardinality[] producedCardinalities, final BaseType[] producedBaseTypes) {
        this.expressionType = expressionType;
        this.minimum = minimum;
        this.maximum = maximum;
        this.requiredCardinalities = requiredCardinalities;
        this.requiredBaseTypes = requiredBaseTypes;
        this.producedCardinalities = producedCardinalities;
        this.producedBaseTypes = producedBaseTypes;
    }

    private ExpressionType(final String expressionType, final int minimum, final int maximum,
            final Cardinality[] requiredCardinalities, final BaseType[] requiredBaseTypes,
            final Cardinality[] producedCardinalities, final BaseType[] producedBaseTypes) {
        this(expressionType, minimum, Integer.valueOf(maximum),
                requiredCardinalities, requiredBaseTypes,
                producedCardinalities, producedBaseTypes);
    }

    /**
     * Gets QTI_CLASS_NAME of this expression type.
     *
     * @return QTI_CLASS_NAME of this expression type
     */
    public String getQtiClassName() {
        return expressionType;
    }

    /**
     * Gets minimum required children
     *
     * @return minimum required children or null
     */
    public int getMinimum() {
        return minimum;
    }

    /**
     * Gets maximum allowed children, returning null to indicate no limit.
     *
     * @return maximum allowed children or null
     */
    public Integer getMaximum() {
        return maximum;
    }

    /**
     * Gets list of all acceptable cardinalities for given position.
     *
     * @param index position
     * @return list of all acceptable cardinalities for given position
     * @see ExpressionParent#getRequiredCardinalities(ValidationContext, int)
     */
    public Cardinality[] getRequiredCardinalities(final int index) {
        return requiredCardinalities;
    }

    /**
     * Gets list of all acceptable baseTypes for given position.
     *
     * @param index position
     * @return list of all acceptable baseTypes for given position
     * @see ExpressionParent#getRequiredBaseTypes(ValidationContext, int)
     */
    public BaseType[] getRequiredBaseTypes(final int index) {
        return requiredBaseTypes;
    }

    /**
     * Gets list of all produced cardinalities.
     *
     * @return list of all produced cardinalities
     * @see Expression#getProducedCardinalities(ValidationContext)
     */
    public Cardinality[] getProducedCardinalities() {
        return producedCardinalities;
    }

    /**
     * Gets list of all produced baseTypes.
     *
     * @return list of all produced baseTypes
     * @see Expression#getProducedBaseTypes(ValidationContext)
     */
    public BaseType[] getProducedBaseTypes() {
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
    public String toString() {
        return expressionType;
    }

    /**
     * Gets expression type for given QTI_CLASS_NAME.
     *
     * @param qtiClassName QTI_CLASS_NAME
     * @return expression type for given QTI_CLASS_NAME
     */
    public static ExpressionType getType(final String qtiClassName) {
        return expressionTypes.get(qtiClassName);
    }

    public static Set<String> getQtiClassNames() {
        return expressionTypes.keySet();
    }

    /**
     * Creates expression.
     *
     * @param parent parent of created expression
     * @param qtiClassName QTI_CLASS_NAME of created expression
     * @return created expression
     */
    public static Expression getInstance(final ExpressionParent parent, final String qtiClassName) {
        final ExpressionType expressionType = expressionTypes.get(qtiClassName);
        if (expressionType == null) {
            throw new QtiIllegalChildException(parent, qtiClassName);
        }
        return expressionType.create(parent);
    }
}
