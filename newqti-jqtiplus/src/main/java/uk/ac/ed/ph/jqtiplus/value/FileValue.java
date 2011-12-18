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

package uk.ac.ed.ph.jqtiplus.value;

import uk.ac.ed.ph.jqtiplus.exception.QTIParseException;

/**
 * Implementation of <code>BaseType</code> file value.
 * <p>
 * Files are represented using MIME message streams, including the specification of headers for providing
 * information about content type and encoding. For more information see [RFC2045].
 * <p>
 * There are not implemented any checks right now. Valid file value is any non empty string!
 * <p>
 * Example value:
 * <pre>
 * Content-Type: image/gif; name="works.gif" ; x-mac-type="47494666"
 * ; x-mac-creator="4A565752"
 * Content-Disposition: attachment; filename="works.gif"
 * Content-Transfer-Encoding: base64
 *
 * R0lGODlhGgAXAPcAAP//////zP//mf//Zv//M///AP/M///MzP/Mmf/MZv/MM//MAP+Z
 * //+ZzP+Zmf+ZZv+ZM/+ZAP9m//9mzP9mmf9mZv9mM/9mAP8z//8zzP8zmf8zZv8zM/8z
 * AP8A//8AzP8Amf8AZv8AM/8AAMz//8z/zMz/mcz/Zsz/M8z/AMzM/8zMzMzMmczMZszM
 * M8zMAMyZ/8yZzMyZmcyZZsyZM8yZAMxm/8xmzMxmmcxmZsxmM8xmAMwz/8wzzMwzmcwz
 * ZswzM8wzAMwA/8wAzMwAmcwAZswAM8wAAJn//5n/zJn/mZn/Zpn/M5n/AJnM/5nMzJnM
 * mZnMZpnMM5nMAJmZ/5mZzJmZmZmZZpmZM5mZAJlm/5lmzJlmmZlmZplmM5lmAJkz/5kz
 * zJkzmZkzZpkzM5kzAJkA/5kAzJkAmZkAZpkAM5kAAGb//2b/zGb/mWb/Zmb/M2b/AGbM
 * /2bMzGbMmWbMZmbMM2bMAGaZ/2aZzGaZmWaZZmaZM2aZAGZm/2ZmzGZmmWZmZmZmM2Zm
 * AGYz/2YzzGYzmWYzZmYzM2YzAGYA/2YAzGYAmWYAZmYAM2YAADP//zP/zDP/mTP/ZjP/
 * MzP/ADPM/zPMzDPMmTPMZjPMMzPMADOZ/zOZzDOZmTOZZjOZMzOZADNm/zNmzDNmmTNm
 * ZjNmMzNmADMz/zMzzDMzmTMzZjMzMzMzADMA/zMAzDMAmTMAZjMAMzMAAAD//wD/zAD/
 * mQD/ZgD/MwD/AADM/wDMzADMmQDMZgDMMwDMAACZ/wCZzACZmQCZZgCZMwCZAABm/wBm
 * zABmmQBmZgBmMwBmAAAz/wAzzAAzmQAzZgAzMwAzAAAA/wAAzAAAmQAAZgAAM+4AAN0A
 * ALsAAKoAAIgAAHcAAFUAAEQAACIAABEAAADuAADdAAC7AACqAACIAAB3AABVAABEAAAi
 * AAARAAAA7gAA3QAAuwAAqgAAiAAAdwAAVQAARAAAIgAAEe7u7t3d3bu7u6qqqoiIiHd3
 * d1VVVURERCIiIhEREQAAACH5BAEAAAEALAAAAAAaABcABwigAAMIHEgwwL+D/woqXEjw
 * HzZsCRlKHOjwIcSJEys+jIhRYUUAADZ29PgQpMiRFEuCDHkR5ceVJlt2/OivZkyOGVXW
 * 9LfyZE5sIHf2lMnw5UqbN3/CBCCUJc6CRoPu5Om06EOqTKdS9dnwqlapWLka9Dr1aNOq
 * KbFppWoTKdqxatdKnZsU7tq2bMNe1Gixr9+/Fg8CHkwYoeHDiBMfBAggIAA7
 * </pre>
 * <p>
 * This class is not mutable and cannot contain NULL value.
 * <p>
 * <code>Cardinality</code> of this class is always single and <code>BaseType</code> is always file.
 *
 * @see uk.ac.ed.ph.jqtiplus.value.Cardinality
 * @see uk.ac.ed.ph.jqtiplus.value.BaseType
 * 
 * @author Jiri Kajaba
 */
public class FileValue extends SingleValue
{
    private static final long serialVersionUID = 1L;
    
    private String stringValue;

    /**
     * Constructs <code>FileValue</code> from given <code>String</code> representation.
     *
     * @param value <code>String</code> representation of <code>FileValue</code>
     * @throws QTIParseException if <code>String</code> representation of <code>FileValue</code> is not valid
     */
    public FileValue(String value)
    {
        if (value == null || value.length() == 0)
            throw new QTIParseException("Invalid file '" + value + "'. Length is not valid.");

        this.stringValue = value;
    }

    public BaseType getBaseType()
    {
        return BaseType.FILE;
    }

    /**
     * Returns the value of this <code>FileValue</code> as A <code>String</code>.
     *
     * @return the value of this <code>FileValue</code> as A <code>String</code>
     */
    public String stringValue()
    {
        return stringValue;
    }

    @Override
    public boolean equals(Object object)
    {
        if (object == null || !getClass().equals(object.getClass()))
            return false;

        FileValue value = (FileValue) object;

        return stringValue.equals(value.stringValue);
    }

    @Override
    public int hashCode()
    {
        return stringValue.hashCode();
    }

    @Override
    public String toString()
    {
        return stringValue;
    }
}
