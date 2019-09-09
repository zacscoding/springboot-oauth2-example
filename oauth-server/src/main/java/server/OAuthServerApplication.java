package server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import server.config.properties.AppProperties;

/**
 *
 * @GitHub : https://github.com/zacscoding
 */
@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
@RestController
public class OAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OAuthServerApplication.class, args);
    }

    /**
     * mock resource api
     */
    @GetMapping("/api/resources")
    public String getResource() {
        return "MOCK-RESOURCE\n";
    }

    @GetMapping("/noauth/resources")
    public String getResourceWithAuth() {
        return "MOCK-NOAUTH-RESOURCE\n";
    }
}
