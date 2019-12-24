package server.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@Component
@ConfigurationProperties(prefix = "app")
public class ApplicationProperties {

    private String adminUsername;
    private String adminPassword;

    private String userUsername;
    private String userPassword;

    private String clientId;
    private String clientSecret;

    private SwaggerProperties swagger;

    @Getter
    @Setter
    public static class SwaggerProperties {

        private String accessTokenUri;
    }
}
