package server.backup;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;

/**
 *
 * @GitHub : https://github.com/zacscoding
 */
@Ignore
public class OAuthClientTests {

    String grantType = "password";
    String accessTokenUri = "http://localhost:18989/oauth/token";
    String clientId = "application";
    String clientSecret = "pass";
    String username = "user@gmail.com";
    String password = "user";

    @Test
    public void runTests() throws Exception {
        OAuth2RestTemplate restTemplate = createRestTemplate();
        String response = restTemplate.getForObject(
                "http://localhost:18989/api/resources", String.class
        );
        System.out.println("resources : " + response);
    }

    private OAuth2RestTemplate createRestTemplate() {
        ResourceOwnerPasswordResourceDetails resourceDetails = new ResourceOwnerPasswordResourceDetails();

        resourceDetails.setGrantType(grantType);
        resourceDetails.setAccessTokenUri(accessTokenUri);
        resourceDetails.setClientId(clientId);
        resourceDetails.setClientSecret(clientSecret);

        List<String> scopes = new ArrayList<>();
        scopes.add("read");
        scopes.add("write");
        resourceDetails.setScope(scopes);

        resourceDetails.setUsername(username);
        resourceDetails.setPassword(password);

        return new OAuth2RestTemplate(resourceDetails);
    }

}
