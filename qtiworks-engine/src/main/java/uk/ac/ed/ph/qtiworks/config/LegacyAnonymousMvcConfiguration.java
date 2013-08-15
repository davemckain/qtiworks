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
package uk.ac.ed.ph.qtiworks.config;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.Charsets;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

/**
 * Defines beans for the legacy anonymous MVC dispatcher, which is configured in web.xml to
 * handle requests of the form <code>/web/anonymous/...</code>.
 * <p>
 * This is currently being kept purely for Uniqurate, which still uses the old URL schemes. Once
 * it gets updated, this can be removed.
 *
 * @author David McKain
 */
@EnableWebMvc
@Configuration
@ComponentScan(basePackages={"uk.ac.ed.ph.qtiworks.web.controller.legacy"})
public class LegacyAnonymousMvcConfiguration extends WebMvcConfigurerAdapter {

    /**
     * (I'm setting up message converters explicitly. One reason is that
     * @ResponseBody doesn't allow you to set an explicit content type,
     * which can lead to problems. I suppose it's nice and tidy being explicit, so
     * here we are!)
     */
    @Override
    public void configureMessageConverters(final List<HttpMessageConverter<?>> converters) {
      final StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
      stringConverter.setSupportedMediaTypes(Arrays.asList(new MediaType[] {
              new MediaType("text", "html", Charsets.UTF_8),
              new MediaType("text", "plain", Charsets.UTF_8),
      }));
      converters.add(stringConverter);
      converters.add(new MappingJackson2HttpMessageConverter());
    }

    @Bean
    ViewResolver viewResolver() {
        final UrlBasedViewResolver result = new UrlBasedViewResolver();
        result.setRedirectHttp10Compatible(false);
        result.setViewClass(JstlView.class);
        result.setPrefix("/WEB-INF/jsp/views/anonymous/");
        result.setSuffix(".jsp");
        return result;
    }
}
