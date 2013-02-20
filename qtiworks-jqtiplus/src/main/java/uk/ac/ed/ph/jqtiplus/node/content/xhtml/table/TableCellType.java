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
package uk.ac.ed.ph.jqtiplus.node.content.xhtml.table;

import uk.ac.ed.ph.jqtiplus.exception.QtiIllegalChildException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * tableCell types
 *
 * @author Jonathon Hare
 */
public enum TableCellType {
    /**
     * td
     */
    TD(Td.QTI_CLASS_NAME) {

        @Override
        public TableCell create(final Tr parent) {
            return new Td(parent);
        }
    },
    /**
     * th
     */
    TH(Th.QTI_CLASS_NAME) {

        @Override
        public TableCell create(final Tr parent) {
            return new Th(parent);
        }
    };

    private static Map<String, TableCellType> tableCellTypes;

    static {
        tableCellTypes = new HashMap<String, TableCellType>();

        for (final TableCellType tableCellType : TableCellType.values()) {
            tableCellTypes.put(tableCellType.tableCellType, tableCellType);
        }
    }

    private String tableCellType;

    TableCellType(final String inlineType) {
        this.tableCellType = inlineType;
    }

    /**
     * Gets QTI_CLASS_NAME of this tableCell type.
     *
     * @return QTI_CLASS_NAME of this tableCell type
     */
    public String getQtiClassName() {
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
    public String toString() {
        return tableCellType;
    }

    /**
     * Gets tableCell type for given QTI_CLASS_NAME.
     *
     * @param qtiClassName QTI_CLASS_NAME
     * @return tableCell type for given QTI_CLASS_NAME
     */
    public static TableCellType getType(final String qtiClassName) {
        return tableCellTypes.get(qtiClassName);
    }

    /**
     * Creates tableCell element.
     *
     * @param parent parent of created tableCell
     * @param qtiClassName QTI_CLASS_NAME of created tableCell
     * @return created expression
     */
    public static TableCell getInstance(final Tr parent, final String qtiClassName) {
        final TableCellType tableCellType = tableCellTypes.get(qtiClassName);

        if (tableCellType == null) {
            throw new QtiIllegalChildException(parent, qtiClassName);
        }

        return tableCellType.create(parent);
    }

    public static Set<String> getQtiClassNames() {
        return tableCellTypes.keySet();
    }
}
