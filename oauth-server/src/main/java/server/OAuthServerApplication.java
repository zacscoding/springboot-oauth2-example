package server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import server.account.Account;
import server.account.annotation.CurrentUser;
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

    @GetMapping("/api/hello")
    public String getResource(@CurrentUser Account account) {
        StringBuilder result = new StringBuilder("Hello ");
        if (account == null) {
            result.append("anonymous");
        } else {
            result.append(account.getUsername());
        }
        return result.append(" !!").toString();
    }
}
