package server;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Oauth2 client IT given account resource server
 *
 * @GitHub : https://github.com/zacscoding
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OAuth2ClientIT {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Test
    public void runOauthClient() throws Exception {
        /**
         * 1) /accounts/me with anonymous user
         */
        mockMvc.perform(get("/accounts/me"))
               .andDo(print())
               .andExpect(status().isUnauthorized());

        /**
         * 2) getting access token
         */
        final String username = "user@gmail.com";
        final String password = "user";
        final String clientId = "application";
        final String clientSecret = "pass";

        ResultActions perform = mockMvc.perform(post("/oauth/token")
                                                        .with(httpBasic(clientId, clientSecret))
                                                        .param("username", username)
                                                        .param("password", password)
                                                        .param("grant_type", "password"));

        String response = perform.andReturn().getResponse().getContentAsString();
        System.out.println("/oauth/token response : " + response);
        String accessToken = new Jackson2JsonParser().parseMap(response).get("access_token").toString();
        String bearerToken = "Bearer " + accessToken;

        /**
         * 3) request with auth token
         */
        mockMvc.perform(get("/accounts/me")
                                .header(HttpHeaders.AUTHORIZATION, bearerToken))
               .andDo(print())
               .andExpect(jsonPath("id").exists())
               .andExpect(jsonPath("email").value(username))
               .andExpect(jsonPath("age").exists());

        /**
         * 4) refresh token
         */
    }
}
