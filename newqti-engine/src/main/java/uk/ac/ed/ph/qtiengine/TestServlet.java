package uk.ac.ed.ph.qtiengine;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestServlet extends HttpServlet {

    private static final long serialVersionUID = -3862728852466145739L;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        response.getOutputStream().println("Hello");
        response.getOutputStream().flush();
    }

}
