package uk.ac.ed.ph.qtiengine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {
    
    @Bean
    public String myString() {
        return "dave";
    }

}
