package uk.ac.ed.ph.qtiengine;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dave.RPTemplateTest;

public class TestServlet extends HttpServlet {
    
    private static final long serialVersionUID = -3862728852466145739L;
    
    private static final Logger logger = LoggerFactory.getLogger(TestServlet.class);
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
        logger.info("Logging here!");
        response.setContentType("text/plain");
        response.getOutputStream().println("Hello " + RPTemplateTest.bob);
        response.getOutputStream().flush();
    }

}
