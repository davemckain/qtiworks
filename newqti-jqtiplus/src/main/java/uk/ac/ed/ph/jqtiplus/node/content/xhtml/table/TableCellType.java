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

package uk.ac.ed.ph.jqtiplus.node.content.xhtml.table;

import uk.ac.ed.ph.jqtiplus.exception.QTINodeGroupException;
import uk.ac.ed.ph.jqtiplus.exception.QTIParseException;

import java.util.HashMap;
import java.util.Map;


/**
 * tableCell types
 * 
 * @author Jonathon Hare
 *
 */
public enum TableCellType {
    /**
     * td 
     */
    TD(Td.CLASS_TAG) {
        @Override
        public TableCell create(Tr parent) {
            return new Td(parent);
        }
    },
    /**
     * th 
     */
    TH(Th.CLASS_TAG) {
        @Override
        public TableCell create(Tr parent) {
            return new Th(parent);
        }
    };
    
    private static Map<String, TableCellType> tableCellTypes;
    
    static {
        tableCellTypes = new HashMap<String, TableCellType>();

        for (TableCellType tableCellType : TableCellType.values())
            tableCellTypes.put(tableCellType.tableCellType, tableCellType);
    }
    
    private String tableCellType;
    
    TableCellType(String inlineType) {
        this.tableCellType = inlineType;
    }
    
    /**
     * Gets CLASS_TAG of this tableCell type.
     *
     * @return CLASS_TAG of this tableCell type
     */
    public String getClassTag()
    {
        return tableCellType;
    }

    /**
     * Creates tableCell element.
     *
     * @param parent parent of created tableCell
     * @return created tableCell
     */
    public abstract TableCell create(Tr parent);

    @Override
    public String toString()
    {
        return tableCellType;
    }

    /**
     * Gets tableCell type for given CLASS_TAG.
     *
     * @param classTag CLASS_TAG
     * @return tableCell type for given CLASS_TAG
     */
    public static TableCellType getType(String classTag)
    {
        TableCellType tableCellType = tableCellTypes.get(classTag);

        if (tableCellType == null)
            throw new QTINodeGroupException("Unsupported tableCell element: " + classTag);

        return tableCellType;
    }
    
    /**
     * Creates tableCell element.
     *
     * @param parent parent of created tableCell
     * @param classTag CLASS_TAG of created tableCell
     * @return created expression
     */
    public static TableCell getInstance(Tr parent, String classTag)
    {
        TableCellType tableCellType = tableCellTypes.get(classTag);

        if (tableCellType == null)
            throw new QTIParseException("Unsupported inline element: " + classTag);

        return tableCellType.create(parent);
    }
}
