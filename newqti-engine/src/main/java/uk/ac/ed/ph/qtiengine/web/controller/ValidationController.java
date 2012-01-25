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
package uk.ac.ed.ph.qtiengine.web.controller;

import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumper;

import uk.ac.ed.ph.qtiengine.UploadException;
import uk.ac.ed.ph.qtiengine.services.UploadService;
import uk.ac.ed.ph.qtiengine.web.domain.AssessmentUpload;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * FIXME: Document this type
 *
 * @author David McKain
 */
@Controller
public class ValidationController {
    
    @Resource
    private UploadService uploadService;
    
    /** 
     * Validates a raw payload sent via POST, returning a Java Object dump.
     * (This could become something better in due course...)
     */
    @RequestMapping(value="/ws/validate", method=RequestMethod.POST)
    @ResponseBody
    public String validate(@RequestHeader("Content-Type") String contentType, HttpServletRequest request)
            throws UploadException, IOException {
        ServletInputStream uploadStream = request.getInputStream();
        AssessmentUpload assessmentUpload = null;
        try {
            assessmentUpload = uploadService.importData(uploadStream, contentType);
            return ObjectDumper.dumpObject(assessmentUpload, DumpMode.DEEP);
        }
        finally {
            if (assessmentUpload!=null) {
                uploadService.deleteUpload(assessmentUpload);
            }
        }
    }
    
    //------------------------------------------------------
    
    @RequestMapping(value="/validator", method=RequestMethod.GET)
    public String showValidatorForm() {
        return "validator-uploadForm";
    }
    
    @RequestMapping(value="/validator", method=RequestMethod.POST)
    public String handleValidatorForm(HttpServletRequest request, Model model)
            throws UploadException, IOException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile uploadFile = multipartRequest.getFile("upload");
        AssessmentUpload assessmentUpload = null;
        try {
            assessmentUpload = uploadService.importData(uploadFile.getInputStream(), uploadFile.getContentType());
            model.addAttribute("assessmentUpload", assessmentUpload);
            return "validator-results";
        }
        finally {
            if (assessmentUpload!=null) {
                uploadService.deleteUpload(assessmentUpload);
            }
        }
    }
}
