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
 * This software is derived from (and contains code from) QTITools and MathAssessEngine.
 * QTITools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package uk.ac.ed.ph.jqtiplus.node.accessibility.companion;

import uk.ac.ed.ph.jqtiplus.group.accessibility.AccessibilityNode;
import uk.ac.ed.ph.jqtiplus.group.accessibility.companion.CompanionMaterialGroup;
import uk.ac.ed.ph.jqtiplus.node.AbstractNode;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.utils.QueryUtils;

import java.util.List;

/**
 * FIXME: Document this type
 *
 * @author Zack Pierce
 */
public class CompanionMaterialsInfo extends AbstractNode implements AccessibilityNode {

    private static final long serialVersionUID = -2322734469468312351L;

    public static final String QTI_CLASS_NAME = "companionMaterialsInfo";

    public CompanionMaterialsInfo(final QtiNode parent) {
        super(parent, QTI_CLASS_NAME);
        getNodeGroups().add(new CompanionMaterialGroup(this));
        // TODO : support 3rd party element order lists added via "any" extension point here
    }

    /**
     * Computes a snapshot of all the Calculators within this CompanionMaterialsInfo.
     * The returned list cannot be used for adding new Calculators.
     *
     * (This performs a shallow search.)
     *
     * @return list of Calculators.
     */
    public List<Calculator> findCalculators() {
        return QueryUtils.findShallowInstances(Calculator.class, getNodeGroups().getCompanionMaterialGroup()
                .getChildren());
    }

    /**
     * Computes a snapshot of all the Rules within this CompanionMaterialsInfo.
     * The returned list cannot be used for adding new Rules.
     *
     * (This performs a shallow search.)
     *
     * @return list of Rules.
     */
    public List<Rule> findRules() {
        return QueryUtils.findShallowInstances(Rule.class, getNodeGroups().getCompanionMaterialGroup().getChildren());
    }

    /**
     * Computes a snapshot of all the Protractors within this CompanionMaterialsInfo.
     * The returned list cannot be used for adding new Protractors.
     *
     * (This performs a shallow search.)
     *
     * @return list of Protractors.
     */
    public List<Protractor> findProtractors() {
        return QueryUtils.findShallowInstances(Protractor.class, getNodeGroups().getCompanionMaterialGroup()
                .getChildren());
    }

    /**
     * Computes a snapshot of all the ReadingPassages within this CompanionMaterialsInfo.
     * The returned list cannot be used for adding new ReadingPassages.
     *
     * (This performs a shallow search.)
     *
     * @return list of ReadingPassages.
     */
    public List<ReadingPassage> findReadingPassages() {
        return QueryUtils.findShallowInstances(ReadingPassage.class, getNodeGroups().getCompanionMaterialGroup()
                .getChildren());
    }

    /**
     * Computes a snapshot of all the DigitalMaterials within this CompanionMaterialsInfo.
     * The returned list cannot be used for adding new DigitalMaterials.
     *
     * (This performs a shallow search.)
     *
     * @return list of DigitalMaterials.
     */
    public List<DigitalMaterial> findDigitalMaterials() {
        return QueryUtils.findShallowInstances(DigitalMaterial.class, getNodeGroups().getCompanionMaterialGroup()
                .getChildren());
    }

    /**
     * Computes a snapshot of all the PhysicalMaterials within this CompanionMaterialsInfo.
     * The returned list cannot be used for adding new PhysicalMaterials.
     *
     * (This performs a shallow search.)
     *
     * @return list of PhysicalMaterials.
     */
    public List<PhysicalMaterial> findPhysicalMaterials() {
        return QueryUtils.findShallowInstances(PhysicalMaterial.class, getNodeGroups().getCompanionMaterialGroup()
                .getChildren());
    }
}
