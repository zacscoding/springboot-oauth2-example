package server.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import server.account.annotation.AuthPrincipal;
import server.config.properties.ApplicationProperties;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.GrantType;
import springfox.documentation.service.OAuth;
import springfox.documentation.service.ResourceOwnerPasswordCredentialsGrant;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@RequiredArgsConstructor
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    private final ApplicationProperties properties;

    @Bean
    public Docket api() {
        final ApiInfo apiInfo = new ApiInfoBuilder()
                .title("SpringBoot OAuth2 Example")
                .description("Support OAuth2 Server based on Database")
                .build();

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo)
                .ignoredParameterTypes(AuthPrincipal.class)
                .select()
                .apis(RequestHandlerSelectors.basePackage("server"))
                .paths(PathSelectors.any())
                .build()
                .securityContexts(Collections.singletonList(securityContext()))
                .securitySchemes(Arrays.asList(securitySchema()));
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                              .securityReferences(defaultAuth())
                              .build();
    }

    private List<SecurityReference> defaultAuth() {
        final AuthorizationScope[] authorizationScopes = new AuthorizationScope[] {
                new AuthorizationScope("read", "read all"),
                new AuthorizationScope("write", "write all")
        };

        return Collections.singletonList(new SecurityReference("oauth2", authorizationScopes));
    }

    private OAuth securitySchema() {
        final List<AuthorizationScope> authorizationScopeList = new ArrayList<>(2);

        authorizationScopeList.add(new AuthorizationScope("read", "read all"));
        authorizationScopeList.add(new AuthorizationScope("write", "access all"));

        final List<GrantType> grantTypes = new ArrayList<>(1);
        grantTypes.add(new ResourceOwnerPasswordCredentialsGrant(properties.getSwagger().getAccessTokenUri()));

        return new OAuth("oauth2", authorizationScopeList, grantTypes);
    }
}
