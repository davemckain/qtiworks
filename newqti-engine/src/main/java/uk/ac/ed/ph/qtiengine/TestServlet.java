package uk.ac.ed.ph.qtiengine;

import uk.ac.ed.ph.snuggletex.SnuggleSession;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dave.RPTemplateTest;

public class TestServlet extends HttpServlet {

    private static final long serialVersionUID = -3862728852466145739L;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        response.getOutputStream().println("Hello " + RPTemplateTest.bob);
        response.getOutputStream().flush();
    }

}
