package server.backup;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @GitHub : https://github.com/zacscoding
 */
@Ignore
public class OAuthClientTests2 {

    private RestTemplate restTemplate = new RestTemplate();

    @Test
    public void getAccessToken() throws Exception {
        String url = "http://localhost:18989/oauth/token";
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                                                          .scheme("http")
                                                          .host("localhost")
                                                          .port(18989)
                                                          .path("/oauth/token")
                                                          .build();

        final String username = "user@gmail.com";
        final String password = "user";
        final String clientId = "application";
        final String clientSecret = "pass";
        Map<String, String> param = new HashMap<>();
        param.put("username", username);
        param.put("password", password);
        param.put("grant_type", "password");

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(param, createHeaders(clientId, clientSecret));
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST,
                                                                entity,
                                                                String.class);
        System.out.println(response.getBody());
    }

    HttpHeaders createHeaders(String username, String password) {
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(Charset.forName("UTF-8")));
            String authHeader = "Basic " + new String(encodedAuth);
            set("Authorization", authHeader);
        }};
    }
}
