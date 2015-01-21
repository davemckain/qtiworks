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
package uk.ac.ed.ph.qtiworks.web.controller.candidate;

import uk.ac.ed.ph.qtiworks.mathassess.XsltStylesheetCacheAdapter;
import uk.ac.ed.ph.qtiworks.mathassess.glue.AsciiMathHelper;
import uk.ac.ed.ph.qtiworks.mathassess.glue.AsciiMathInputException;

import uk.ac.ed.ph.jqtiplus.xmlutils.xslt.XsltStylesheetCache;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for candidate helpers, such as math input verifiers.
 *
 * @author David McKain
 */
@Controller
public class CandidateHelperController {

    @Resource
    private XsltStylesheetCache stylesheetCache;

    /**
     * Runs the {@link AsciiMathHelper} helper on the given 'input' parameter,
     * expecting to return JSON.
     *
     * Accept: application/json from client expected
     *
     * @throws AsciiMathInputException
     */
    @RequestMapping(value="/verifyAsciiMath", method=RequestMethod.POST)
    public ResponseEntity<Map<String, String>>  verifyAsciiMath(@RequestParam("input") final String asciiMathInput) {
        final AsciiMathHelper asciiMathHelper = new AsciiMathHelper(new XsltStylesheetCacheAdapter(stylesheetCache));
        final Map<String, String> upConvertedAsciiMathInput = asciiMathHelper.upConvertAsciiMathInput(asciiMathInput);

        final HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setCacheControl("private, no-cache, no-store, max-age=0, must-revalidate");

        return new ResponseEntity<Map<String,String>>(upConvertedAsciiMathInput, responseHeaders, HttpStatus.OK);
    }

}
