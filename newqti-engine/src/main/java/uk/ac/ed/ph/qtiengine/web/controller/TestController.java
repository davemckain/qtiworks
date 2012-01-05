package uk.ac.ed.ph.qtiengine.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * MVC Controller for the global "Home" Action.
 *
 * @author  David McKain
 * @version $Revision: 1013 $
 */
@Controller
public class TestController {
    
    @RequestMapping("/test")
    @ResponseBody
    public String test() {
        return "This is a test!";
    }
    
}
