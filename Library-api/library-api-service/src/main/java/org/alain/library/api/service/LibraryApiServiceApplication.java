package org.alain.library.api.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@SpringBootApplication
@ComponentScan(basePackages = {"org.alain.library.api"})
@EntityScan(basePackages = {"org.alain.library.api"})
@EnableJpaRepositories(basePackages = {"org.alain.library.api"})
@EnableSwagger2
public class LibraryApiServiceApplication{


    public static void main(String[] args) {
        SpringApplication.run(LibraryApiServiceApplication.class, args);
    }

//    @Bean
//    public ServletWebServerFactory servletContainer(){
//        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory(){
//            @Override
//            protected void postProcessContext(Context context){
//                SecurityConstraint securityConstraint = new SecurityConstraint();
//                securityConstraint.setUserConstraint("CONFIDENTIAL");
//                SecurityCollection collection = new SecurityCollection();
//                collection.addPattern("/*");
//                securityConstraint.addCollection(collection);
//                context.addConstraint(securityConstraint);
//            }
//        };
//        tomcat.addAdditionalTomcatConnectors(httpToHttpsRedirectConnector());
//        return tomcat;
//    }
//
//    private Connector httpToHttpsRedirectConnector() {
//        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
//        connector.setScheme("http");
//        connector.setPort(8082);
//        connector.setSecure(false);
//        connector.setRedirectPort(8443);
//        return connector;
//    }
}
