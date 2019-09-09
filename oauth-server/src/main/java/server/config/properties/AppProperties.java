package server.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @GitHub : https://github.com/zacscoding
 */
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {

    private String adminUsername;
    private String adminPassword;

    private String userUsername;
    private String userPassword;

    private String clientId;
    private String clientSecret;
}
