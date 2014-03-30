/*
Copyright (c) 2005-2007, University of Cambridge, University of Kingston upon Thames and Dr Graham Smith
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

*    Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

*    Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.

*    Neither the name of the University of Cambridge nor the names of its
contributors may be used to endorse or promote products derived from
this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/// lines added (//DT) for Desktoprenderer
//THIS WILL NOT AFFECT USE IN DEMO OR TEST SITE.

package uk.ac.ed.ph.jqtiplus;

//Miscellaneous  methods

public class qtiv2utils
    {
//    public static String formatVariable
//        (final String theValue, String formatString, final String thebase)
//        {
//        final String conversionCodes = "EefGgoXx";
//        final String numbers = "0123456789";
//        final String floatconversions = "eEfgG";
//        String codeLetter = "";
//        final String widthCode = "";
//        final String precisionCode = "";
//        final int letterpos = -1;
//        String output;
//        double fltval = 0;
//        int intval = 0;
//        final char c ='|';
//        boolean suppresszeros = false;
//        //System.out.println("FORMAT VARIABLE");
//        codeLetter = formatString.substring(formatString.length()-1);
//        //System.out.println("CODE LETTER: " + codeLetter);
//        if(conversionCodes.indexOf(codeLetter) < 0)
//            {
//            if
//                (
//                (codeLetter.equals("r"))
//                ||
//                (codeLetter.equals("R"))
//                )
//                {
//                //System.out.println("HANDLE rR");
//                output = formatR(formatString, theValue);
//                }
//
//            else if(codeLetter.equals("i"))
//                {
//                //System.out.println("HANDLE i");
//                output = formati(formatString, theValue, thebase);
//                }
//            else
//                throw new gqtiexcept.XMLException("Unrecognised conversion code: '" + formatString + "'");
//
//            // With i or rR
//            //System.out.println("FORMAT VARIABLE OUTPUT: " + output+ ":");//to do
//            output = output.replace(" ","&nbsp;");
//           // System.out.println("FORMAT VARIABLE OUTPUT: " + output+ ":");//to do
//            return output;
//            }
//
//        //standard handling
//        //System.out.println("STANDARD FORMAT WITH . " + formatString);
//        //System.out.println("Value " + theValue);
//        //modify behaviour with g conversion codes with integers
//        //and with #
//        if
//            (
//            (codeLetter.equals("g"))
//            ||
//            (codeLetter.equals("G"))
//            )
//            {
//            if(theValue.indexOf(".") < 0)
//                {
//                //number is in fact an integer
//                suppresszeros = true;
//                //System.out.println(" SUPPRESS Zeros");
//                }
//            if(formatString.indexOf("#") > -1)
//               {
//               formatString = formatString.replace("#","");
//               suppresszeros = false;
//               }
//            }
//
//        if(floatconversions.indexOf(codeLetter) < 0)
//           {
//            intval = Integer.parseInt(theValue);
//            output = String.format(formatString, intval);
//           }
//        else
//            {
//            fltval = Double.parseDouble(theValue);
//            output = String.format(formatString, fltval);
//            }
//
//        //System.out.println(" FORMAT STRING: " + formatString);
//        //System.out.println("FORMAT VARIABLE OUTPUT: " + output+ ":");//to do
//        if(suppresszeros)
//            if(output.indexOf(".0") > -1)
//                output = output.substring(0, output.indexOf(".0"));
//        output = output.replace(" ","&nbsp;");
//        //System.out.println("FORMAT VARIABLE OUTPUT: " + output+ ":");//to do
//        return output;
//        }
//
//
//    public static String formati
//            //Java does not handle i
//            (String formatString, String theValue, final String thebase)
//            {
//            //System.out.println("FORMATi");
//            //System.out.println("theValue: "+ theValue);
//            //System.out.println("formatString: "+ formatString);
//            int base = 10;
//            int theval;
//            final int letterpos = -1;
//            String output;
//            final String numbers = "0123456789";
//            String precisionCode;
//            double numericalvalue;
//            char c;
//            // detect if a precision is indicated;
//
//
//            if (formatString.indexOf(".") > -1)
//                   {//treat as width specifier
//                   precisionCode = formatString.substring
//                    (formatString.indexOf(".") +1, formatString.length()-1);
//                //System.out.println("PRECISION CODE" +precisionCode);
//                final int widthVal = Integer.parseInt(precisionCode);
//                //System.out.println("WIDTHVAL" + widthVal);
//                c = theValue.charAt(0); // c is the sign char  +, - or space
//                //System.out.println("INIT char " + c);
//
//                //Take off the sign char
//                if(numbers.indexOf(c) < 0)
//                    theValue = theValue.substring(1);
//                final int len = theValue.length();
//                numericalvalue = Float.parseFloat(theValue);
//                //System.out.println("NUMERICAL VALUE" + numericalvalue);
//
//                //Formatting zero with precision 0 gives no output
//                if((numericalvalue==0)&&(widthVal==0))
//                    {
//                    output = "";
//                    return output;
//                     }
//
//                //System.out.println("THE VALUE Length" + len);
//                //Adjust width to the minimum
//                if (widthVal > len)
//                    for(int z = 0; z < (widthVal-len); z++)
//                           theValue = "0" + theValue;
//                   if(numbers.indexOf(c) < 0)
//                        theValue = c + theValue;
//               //System.out.println("THE New VALUE" + theValue);
//                output = theValue;
//                return output;
//                }//if there is a "."
//
//            //otherwise replace "i" with "d" and use normal Java
//            formatString = formatString.replace("i","d");
//            //System.out.println("Modifed formatString: "+ formatString);
//            if (! thebase .equals(""))
//                base = Integer.parseInt(thebase);
//            if (base!=10)
//                {
////                theValue = MathHandler.base10toBasex(base, theValue);
//                output = theValue;
//                return output;//may have to do some formatting for base10 numbers
//                }
//            else
//                {
//                theval = Integer.parseInt(theValue);
//                output = String.format(formatString, theval);
//                return output;
//                }
//            }
//
//
//public static String formatR (String formatString, final String theValue)
//        //Special handling required in QTI
//        {
//        char c ='|';
//        int noOfZeros = 0;
//        String tempval;
//        final String widthCode;
//        final String precisionCode;
//        String output;
//        final String Integers;
//        final int Integerlength;
//        int z;
//        double theval = 0;
//
//        //System.out.println("HANDLE Rr");
//        //System.out.println("formatString: " + formatString);
//
//        //find decimal part if the number is not integral
//        //(not necessarily declared as float)
//
//        //find number of zeros
//        int pos = -1;
//        if (theValue.indexOf(".") > -1)
//            {
//            String decimals = theValue.substring(theValue.indexOf(".") + 1);
//            //System.out.println("The Value After Decimal point: " + decimals);
//            for (z = 0; z < decimals.length(); z++)
//                {
//                c = decimals.charAt(z);
//                //System.out.println("Char: " + c);
//                if (c != '0')
//                    {
//                    pos = z;
//                    //System.out.println("OUT");
//                    break;
//                   }
//                 noOfZeros ++;
//                }
//
//            //System.out.println("Number of zeros: " + noOfZeros);
//
//            if(noOfZeros < 4)//can use normal Java g processing)
//                {
//                //System.out.println("No of Zeros less than 4 ");
//                //System.out.println("Format String:" + formatString);
//                theval = Double.parseDouble(theValue);
//                formatString = formatString.replace("r","g").replace("R","G");
//                //System.out.println("Format String:" + formatString);
//                output = String.format(formatString, theval);
//                return output;
//                }
//
//            //System.out.println("No of Zeros more than 4 ");
//            //Would normally use Scientific notation"
//            //Manipulate g conversion allowing unlimited zeros
//                 if (pos > -1)
//                    {
//                    decimals = decimals.substring(pos);
//                    }
//                 decimals = "." + decimals;
//                //System.out.println("Value stripped of zeros: " + decimals);
//                theval = Double.parseDouble(decimals);
//                //System.out.println("theval: " + theval);
//                formatString = formatString.replace("r","g").replace("R","G");
//                tempval = String.format (formatString, theval);
//                //System.out.println("tempval: " + tempval);
//                tempval = tempval.substring(tempval.indexOf(".") + 1);
//                //System.out.println("tempval after decimal point: " + tempval);
//                //add back leading zeros
//                for(z = 0; z < noOfZeros; z++)
//                       tempval = "0" + tempval;
//                //System.out.println("tempval: " + tempval);
//                tempval = "0." + tempval;
//                return tempval;
//              }
//
//        // if there is no "." in value
//        //can use normal Java g processing)
//        //System.out.println("NO DECIMAL POINT");
//        theval = Double.parseDouble(theValue);
//        formatString = formatString.replace("r","g");
//        formatString = formatString.replace("R","G");
//        output = String.format(formatString, theval);
//        return output;
//        }
//
//
//

}//qtiv2utils
