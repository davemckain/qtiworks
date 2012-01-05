package uk.ac.ed.ph.qtiengine.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@Configuration
@ComponentScan(basePackages={"uk.ac.ed.ph.qtiengine"})
public class MvcConfiguration {
    
//    @Bean
//    ViewResolver viewResolver() {
//        UrlBasedViewResolver result = new UrlBasedViewResolver();
//        result.setViewClass(JstlView.class);
//        result.setPrefix("/WEB-INF/jsp/views/");
//        result.setSuffix(".jsp");
//        return result;
//    }
    
//    @Bean
//    MessageSource messageSource() {
//        ResourceBundleMessageSource result = new ResourceBundleMessageSource();
//        result.setBasename("messages");
//        return result;
//    }

}
