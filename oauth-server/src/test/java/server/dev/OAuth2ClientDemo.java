package server.dev;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import server.account.dto.AccountDto;

/**
 *
 */
public class OAuth2ClientDemo {

    String endpoint = "http://localhost:3000";
    String clientId = "application";
    String clientSecret = "pass";
    final String username = "user@gmail.com";
    final String password = "user";
    RestTemplate restTemplate;

    @Before
    public void setUp() {
        restTemplate = new RestTemplate();
    }

    @Test
    public void requestToAccessToken() throws Exception {
        for (int i = 0; i < 5; i++) {
            ResponseEntity<Token> accessToken = getAccessToken();

            if (accessToken.getStatusCode().is2xxSuccessful()) {
                System.out.println(accessToken.getBody());
            } else {
                System.err.println(accessToken);
            }

            TimeUnit.SECONDS.sleep(2L);
        }
        //OAuth2ClientDemo.Token(access_token=66d529f5-9040-4848-9616-d6c706ff8d46, token_type=bearer, refresh_token=552cf3b2-f118-4768-ad27-8021652e1ba6, expires_in=4, scope=read write)
        //OAuth2ClientDemo.Token(access_token=66d529f5-9040-4848-9616-d6c706ff8d46, token_type=bearer, refresh_token=552cf3b2-f118-4768-ad27-8021652e1ba6, expires_in=2, scope=read write)
        //OAuth2ClientDemo.Token(access_token=66d529f5-9040-4848-9616-d6c706ff8d46, token_type=bearer, refresh_token=552cf3b2-f118-4768-ad27-8021652e1ba6, expires_in=0, scope=read write)
        //OAuth2ClientDemo.Token(access_token=21ae5775-c1b5-4e6c-be1a-4f0a9ca1bb67, token_type=bearer, refresh_token=de0d7901-ae41-4a7d-8524-f81f525f6a09, expires_in=4, scope=read write)
        //OAuth2ClientDemo.Token(access_token=21ae5775-c1b5-4e6c-be1a-4f0a9ca1bb67, token_type=bearer, refresh_token=de0d7901-ae41-4a7d-8524-f81f525f6a09, expires_in=2, scope=read write)
    }

    @Test
    public void requestRefreshToken() throws Exception {
        ResponseEntity<Token> accessToken = getAccessToken();
        System.out.println("First access token : " + accessToken.getBody());
        String refreshToken = accessToken.getBody().getRefreshToken();
        for (int i = 0; i < 5; i++) {
            try {
                ResponseEntity<Token> refreshTokenResponse = getRefreshToken(refreshToken);

                if (refreshTokenResponse.getStatusCode().is2xxSuccessful()) {
                    System.out.printf("Request %d >> %s\n", i, refreshTokenResponse.getBody());
                    refreshToken = refreshTokenResponse.getBody().getRefreshToken();
                } else {
                    System.err.printf("Request %d >> %s\n", i, refreshTokenResponse);
                }
            } catch (Unauthorized e) {
                System.err.println("Unauthorized exception occur");
                System.out.println("Receive new token : " + getAccessToken().getBody());
                refreshToken = getAccessToken().getBody().getRefreshToken();
                i--;
            } catch (Exception e) {
                System.err.println("Unknown exception " + e.getClass().getName() + " : " + e.getMessage());
            }

            TimeUnit.SECONDS.sleep(2L);
        }
        //First access token : OAuth2ClientDemo.Token(accessToken=8ce2f5fd-9a11-44c1-af0c-64ac39fbf0c7, tokenType=bearer, refreshToken=2e39c3fb-ceba-4ef7-a662-31c17ba3bb36, expiresIn=4, scope=read write)
        //Request 0 >> OAuth2ClientDemo.Token(accessToken=a6ea8fa8-c20a-41b4-87e6-249807409bc2, tokenType=bearer, refreshToken=2e39c3fb-ceba-4ef7-a662-31c17ba3bb36, expiresIn=4, scope=read write)
        //Request 1 >> OAuth2ClientDemo.Token(accessToken=83d9370f-671e-4aa5-b3d8-1c9acfd9b591, tokenType=bearer, refreshToken=2e39c3fb-ceba-4ef7-a662-31c17ba3bb36, expiresIn=4, scope=read write)
        //Request 2 >> OAuth2ClientDemo.Token(accessToken=32ed4776-7639-4a58-ba5d-937e86380636, tokenType=bearer, refreshToken=2e39c3fb-ceba-4ef7-a662-31c17ba3bb36, expiresIn=4, scope=read write)
        //Unauthorized exception occur
        //Receive new token : OAuth2ClientDemo.Token(accessToken=54407f32-0301-4b23-a412-4f65bba4522c, tokenType=bearer, refreshToken=6ab355c4-48ce-4297-9f0f-f0a20350a748, expiresIn=4, scope=read write)
        //Request 3 >> OAuth2ClientDemo.Token(accessToken=085e74e3-c01b-477b-9dbb-1265d0de897d, tokenType=bearer, refreshToken=6ab355c4-48ce-4297-9f0f-f0a20350a748, expiresIn=4, scope=read write)
        //Request 4 >> OAuth2ClientDemo.Token(accessToken=1dfabd7c-fe22-4c6f-a189-c3bfb4c074ba, tokenType=bearer, refreshToken=6ab355c4-48ce-4297-9f0f-f0a20350a748, expiresIn=4, scope=read write)
    }

    @Test
    public void requestResourceWithoutRefreshToken() throws Exception {
        ResponseEntity<Token> tokenResponseEntity = getAccessToken();

        if (!tokenResponseEntity.getStatusCode().is2xxSuccessful()) {
            System.err.println("failed to get access token. " + tokenResponseEntity);
            return;
        }

        Token token = tokenResponseEntity.getBody();

        for (int i = 0; i < 5; i++) {
            try {
                ResponseEntity<AccountDto> response = getAccountMe(token.getAccessToken());
                if (response.getStatusCode().is2xxSuccessful()) {
                    System.out.printf("Request %d >> %s\n", i, response.getBody());
                } else {
                    System.err.printf("Request %d >> %s\n", i, response);
                }
            } catch (Unauthorized e) {
                System.err.println("Unauthorized exception occur");
            } catch (Exception e) {
                System.err.println("Unknown exception " + e.getClass().getName() + " : " + e.getMessage());
            }
            TimeUnit.SECONDS.sleep(2L);
        }
        System.out.println(">> Complete");
        //AccountDto(id=2, email=user@gmail.com, password=null, age=20, roles=[USER])
        //AccountDto(id=2, email=user@gmail.com, password=null, age=20, roles=[USER])
        //AccountDto(id=2, email=user@gmail.com, password=null, age=20, roles=[USER])
        //Unauthorized exception occur
        //Unauthorized exception occur
        //>> Complete
    }

    @Test
    public void requestResourceWithRefreshToken() throws Exception {
        ResponseEntity<Token> tokenResponseEntity = getAccessToken();

        if (!tokenResponseEntity.getStatusCode().is2xxSuccessful()) {
            System.err.println("failed to get access token. " + tokenResponseEntity);
            return;
        }

        Token token = tokenResponseEntity.getBody();

        for (int i = 0; i < 10; i++) {
            try {
                ResponseEntity<AccountDto> response = getAccountMe(token.getAccessToken());
                if (response.getStatusCode().is2xxSuccessful()) {
                    System.out.printf("Request %d >> %s\n", i, response.getBody());
                } else {
                    System.err.printf("Request %d >> %s\n", i, response);
                }
            } catch (Unauthorized e) {
                System.err.println("Unauthorized exception occur");

                try {
                    ResponseEntity<Token> refreshTokenResponse = getRefreshToken(token.getRefreshToken());

                    if (!refreshTokenResponse.getStatusCode().is2xxSuccessful()) {
                        System.err.println("failed to refresh token. " + tokenResponseEntity);
                        return;
                    }

                    token = refreshTokenResponse.getBody();
                } catch (Unauthorized e2) {
                    ResponseEntity<Token> tokenResponseEntity2 = getAccessToken();

                    if (!tokenResponseEntity2.getStatusCode().is2xxSuccessful()) {
                        System.err.println("failed to get access token. " + tokenResponseEntity2);
                        return;
                    }

                    token = tokenResponseEntity2.getBody();
                }
                i--;
                continue;
            } catch (Exception e) {
                System.err.println("Unknown exception " + e.getClass().getName() + " : " + e.getMessage());
            }
            TimeUnit.SECONDS.sleep(1L);
        }
        System.out.println(">> Complete");
    }

    private ResponseEntity<AccountDto> getAccountMe(String accessToken) {
        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(endpoint)
                                                          .pathSegment("accounts", "me")
                                                          .build();

        final HttpEntity<Void> entity = new HttpEntity<>(createBearerTokenHeader(accessToken));

        return restTemplate.exchange(uriComponents.toString(), HttpMethod.GET, entity, AccountDto.class);
    }

    private ResponseEntity<Token> getAccessToken() {
        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(endpoint)
                                                          .pathSegment("oauth", "token")
                                                          .build();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", username);
        map.add("password", password);
        map.add("grant_type", "password");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, createGetAccessTokenHeader());

        return restTemplate.postForEntity(uriComponents.toString(), request, Token.class);
    }

    private ResponseEntity<Token> getRefreshToken(String refreshToken) {
        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(endpoint)
                                                          .pathSegment("oauth", "token")
                                                          .build();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        map.add("grant_type", "refresh_token");
        map.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, createGetAccessTokenHeader());

        return restTemplate.postForEntity(uriComponents.toString(), request, Token.class);
    }

    private HttpHeaders createBearerTokenHeader(String accessToken) {
        final HttpHeaders httpHeaders = new HttpHeaders() {};

        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        return httpHeaders;
    }

    private HttpHeaders createGetAccessTokenHeader() {
        final HttpHeaders httpHeaders = new HttpHeaders() {};

        final String auth = clientId + ":" + clientSecret;
        final byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        final String authHeader = "Basic " + new String(encodedAuth);

        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.set(HttpHeaders.AUTHORIZATION, authHeader);

        return httpHeaders;
    }

    @Getter
    @Setter
    @ToString
    public static class Token {
        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("token_type")
        private String tokenType;

        @JsonProperty("refresh_token")
        private String refreshToken;

        @JsonProperty("expires_in")
        private Long expiresIn;

        private String scope;
    }
}
