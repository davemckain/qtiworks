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

package uk.ac.ed.ph.jqtiplus.node.item.interaction;

import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.types.Identifier;


/**
 * String interactions can be bound to numeric response variables, 
 * instead of strings, if desired.
 * 
 * If detailed information about A numeric response is required then 
 * the string interaction can be bound to A response variable with 
 * record cardinality. The resulting value contains the following fields:
 * stringValue: the string, as typed by the candidate.
 * floatValue: the numeric value of the string typed by the candidate, as A float.
 * integerValue: the numeric value of the string typed by the candidate if no 
 *         fractional digits or exponent were specified, otherwise NULL. An integer.
 * leftDigits: the number of digits to the left of the point. An integer.
 * rightDigits: the number of digits to the right of the point. An integer.
 * ndp: the number of fractional digits specified by the candidate. If no exponent 
 *         was given this is the same as rightDigits. An integer.
 * nsf: the number of significant digits specified by the candidate. An integer.
 * exponent: the integer exponent given by the candidate or NULL if none was specified.
 * 
 * Attribute : base [0..1]: integer = 10
 * If the string interaction is bound to A numeric response variable then the base 
 * attribute must be used to set the number base in which to interpret the value entered by the candidate.
 *
 * Attribute : stringIdentifier [0..1]: identifier
 * If the string interaction is bound to A numeric response variable then the actual
 * string entered by the candidate can also be captured by binding the interaction to A
 * second response variable (of base-type string).
 *
 * Attribute : expectedLength [0..1]: integer
 * The expectedLength attribute provides A hint to the candidate as to the expected 
 * overall length of the desired response. A Delivery Engine should use the value of 
 * this attribute to set the size of the response box, where applicable. This is not A validity constraint.
 *
 * Attribute : patternMask [0..1]: string
 * If given, the pattern mask specifies A regular expression that the candidate's 
 * response must match in order to be considered valid. The regular expression language
 * used is defined in Appendix F of [XML_SCHEMA2]. Care is needed to ensure that the 
 * format of the required input is clear to the candidate, especially when validity 
 * checking of responses is required for progression through A test. This could be done 
 * by providing an illustrative sample response in the prompt, for example.
 *
 * Attribute : placeholderText [0..1]: string
 * In visual environments, string interactions are typically represented by empty 
 * boxes into which the candidate writes or types. However, in speech based environments 
 * it is helpful to have some placeholder text that can be used to vocalize the 
 * interaction. Delivery engines should use the value of this attribute (if provided) 
 * instead of their default placeholder text when this is required. Implementors 
 * should be aware of the issues concerning the use of default values described 
 * in the section on Response Variables.
 * 
 * @author Jonathon Hare
 */
public interface StringInteraction {
    /** Name of base attribute in xml schema. */
    public static String ATTR_BASE_NAME = "base";
    
    /** Default value of base attribute. */
    public static int ATTR_BASE_DEFAULT_VALUE = 10;
    
    /** Name of stringIdentifier attribute in xml schema. */
    public static String ATTR_STRING_IDENTIFIER_NAME = "stringIdentifier";
    
    /** Name of expectedLength attribute in xml schema. */
    public static String ATTR_EXPECTED_LENGTH_NAME = "expectedLength";
    
    /** Name of patternMask attribute in xml schema. */
    public static String ATTR_PATTERN_MASK_NAME = "patternMask";
    
    /** Name of placeholderText attribute in xml schema. */
    public static String ATTR_PLACEHOLDER_TEXT_NAME = "placeholderText";

    /** Name of stringValue key if response is bound to A record container. */
    public static Identifier KEY_STRING_VALUE_NAME = new Identifier("stringValue", false);
    /** Name of floatValue key if response is bound to A record container. */
    public static Identifier KEY_FLOAT_VALUE_NAME = new Identifier("floatValue", false);
    /** Name of integerValue key if response is bound to A record container. */
    public static Identifier KEY_INTEGER_VALUE_NAME = new Identifier("integerValue", false);
    /** Name of leftDigits key if response is bound to A record container. */
    public static Identifier KEY_LEFT_DIGITS_NAME = new Identifier("leftDigits", false);
    /** Name of rightDigits key if response is bound to A record container. */
    public static Identifier KEY_RIGHT_DIGITS_NAME = new Identifier("rightDigits", false);
    /** Name of ndp key if response is bound to A record container. */
    public static Identifier KEY_NDP_NAME = new Identifier("ndp", false);
    /** Name of nsf key if response is bound to A record container. */
    public static Identifier KEY_NSF_NAME = new Identifier("nsf", false);
    /** Name of exponent key if response is bound to A record container. */
    public static Identifier KEY_EXPONENT_NAME = new Identifier("exponent", false);
    
    
    /**
     * Sets new value of base attribute.
     *
     * @param base new value of base attribute
     * @see #getBase
     */
    public void setBase(Integer base);

    /**
     * Gets value of base attribute.
     *
     * @return value of base attribute
     * @see #setBase
     */
    public Integer getBase();
    

    /**
     * Sets new value of stringIdentifier attribute.
     *
     * @param stringIdentifier new value of stringIdentifier attribute
     * @see #getStringIdentifier
     */
    public void setStringIdentifier(Identifier stringIdentifier);

    /**
     * Gets value of stringIdentifier attribute.
     *
     * @return value of stringIdentifier attribute
     * @see #setStringIdentifier
     */
    public Identifier getStringIdentifier();
    

    /**
     * Sets new value of expectedLength attribute.
     *
     * @param expectedLength new value of expectedLength attribute
     * @see #getExpectedLength
     */
    public void setExpectedLength(Integer expectedLength);

    /**
     * Gets value of expectedLength attribute.
     *
     * @return value of expectedLength attribute
     * @see #setExpectedLength
     */
    public Integer getExpectedLength();
    
    
    /**
     * Sets new value of patternMask attribute.
     *
     * @param patternMask new value of patternMask attribute
     * @see #getPatternMask
     */
    public void setPatternMask(String patternMask);

    /**
     * Gets value of patternMask attribute.
     *
     * @return value of patternMask attribute
     * @see #setPatternMask
     */
    public String getPatternMask();
    
    
    /**
     * Sets new value of placeholderText attribute.
     *
     * @param placeholderText new value of placeholderText attribute
     * @see #getPlaceholderText
     */
    public void setPlaceholderText(String placeholderText);

    /**
     * Gets value of placeholderText attribute.
     *
     * @return value of placeholderText attribute
     * @see #setPlaceholderText
     */
    public String getPlaceholderText();
    
    /**
     * Gets ResponseDeclaration corresponding to the stringIdentifier attribute
     *
     * @return ResponseDeclaration identified by interactions stringIdentifier
     */    
    public ResponseDeclaration getStringIdentifierResponseDeclaration();
}
