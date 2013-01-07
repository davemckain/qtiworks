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
package org.qtitools.qti.node.expression;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.exception2.QtiModelException;
import uk.ac.ed.ph.jqtiplus.node.LoadingContext;
import uk.ac.ed.ph.jqtiplus.node.QtiNode;
import uk.ac.ed.ph.jqtiplus.node.expression.Expression;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionType;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingContext;

import java.io.StringReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * Superclass for all expression tests.
 */
public abstract class ExpressionTest {

    private final String xml;

    private Expression expression;

    /**
     * Constructs expression test.
     * 
     * @param xml xml data used for creation tested expression
     */
    public ExpressionTest(String xml) {
        this.xml = xml;
    }

    /**
     * Gets xml data used for creation tested expression.
     * 
     * @return xml data used for creation tested expression
     */
    protected String getXml() {
        return xml;
    }

    /**
     * Creates tested expression from given xml data.
     * 
     * @return tested expression from give xml data
     * @throws Exception 
     */
    protected Expression getExpression() throws Exception {
        if (expression == null) {
            final Element element = readQtiXmlFragment(xml);
            expression = ExpressionType.getInstance(null, element.getLocalName());
            loadQtiModel(element, expression);
        }
        return expression;
    }
    
    public static Element readQtiXmlFragment(String xmlFragment) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(new InputSource(new StringReader(xmlFragment)));
        return document.getDocumentElement();
    }
    
    public static void loadQtiModel(Element element, QtiNode targetNode) {
        targetNode.load(element, new TestLoadingContext());
    }
    
    public static ItemProcessingContext createContextFreeItemProcessingContext() {
        InvocationHandler invocationHandler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                throw new RuntimeException("Unexpected invocation: proxy=" + proxy + ",method=" + method + ",args=" + Arrays.asList(args));
            }
        };
        return (ItemProcessingContext) Proxy.newProxyInstance(ItemProcessingContext.class.getClassLoader(), new Class[] { ItemProcessingContext.class }, invocationHandler);
    }
    
    public static class TestLoadingContext implements LoadingContext {
        
        @Override
        public JqtiExtensionManager getJqtiExtensionManager() {
            return null;
        }

        @Override
        public void modelBuildingError(QtiModelException exception, Element element) {
            throw(exception);
        }
    }
}
