package server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import server.account.annotation.AuthPrincipal;
import server.account.entity.AccountEntity;
import server.config.properties.ApplicationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties.class)
@RestController
public class OAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OAuthServerApplication.class, args);
    }

    @GetMapping("/api/hello")
    public String getResource(@AuthPrincipal AccountEntity account) {
        StringBuilder result = new StringBuilder("Hello ");

        if (account == null) {
            result.append("anonymous");
        } else {
            result.append(account.getEmail());
        }

        return result.append(" !!").toString();
    }
}
