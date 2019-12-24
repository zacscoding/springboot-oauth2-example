# Oauth2 server & client with spring boot

- <a href="#start">Getting started</a>
- <a href="#structure">Project structure</a>
- <a href="#server">OAuth2 Resource server</a>
- <a href="#client">OAuth2 Client</a>

---  

<div id="start"></div>  

> ## Getting started    

> ### Git clone  

```
$ git clone https://github.com/zacscoding/springboot-oauth2-example.git
$ cd springboot-oauth2-example
```

> ### Start OAuth2 server  

```
$ ./gradlew oauth-server:bootRun
```  

And then connect to http://localhost:3000/swagger-ui.html in browser.

Call `/accounts/me` without access token, then receive unauthorized response.  

Sign in by using `Authorize` tab in swagger-ui and login with below info.  

<table>
  <tr>
    <th>username</th>
    <th>admin@email.com</th>
  </tr>
  <tr>
    <th>password</th>
    <th>admin</th>
  </tr>
  <tr>
    <th>client_id</th>
    <th>application</th>
  </tr>
  <tr>
    <th>client_secret</th>
    <th>pass</th>
  </tr>    
</table>  

Then will receive success response after signed in.  

Can see full flow (get and refresh token) in <a href="test-oauth2-server">tests code</a>

---  

<div id="structure"></div>  

> ## Project structure  

```aidl
springboot-oauth2-example$ tree ./ -L 2
├── oauth-server        <-- oauth2 resource server
│   ├── build.gradle
│   └── src
```

---

<div id="server"></div>  

> ## Resource and auth server  

Oauth-server is a resource and auth server like facebook, google.  

Token info is stored at database given DataSource i.e JdbcTokenStore.

Can see full example of configuration <a href="oauth-server/src/main/java/server/config">config package</a>.

> ### Configurer server  

> SecurityConfiguration extended WebSecurityConfigurerAdapter

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;
import server.account.service.AccountService;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // ignore swagger resources
        web.ignoring().antMatchers(
                "/v2/api-docs", "/swagger-resources/**",
                "/swagger-ui.html", "/webjars/**", "/swagger/**");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(accountService)
            .passwordEncoder(passwordEncoder);
    }
}
```  

> ResourceServerConfiguration extended ResourceServerConfigurerAdapter  

```java
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("examples");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.headers().frameOptions().disable();
        http.anonymous()
            .and()
            .authorizeRequests()
            .mvcMatchers(HttpMethod.GET, "/api/hello")
            .permitAll()
            .anyRequest()
            .authenticated()
            .and()
            .exceptionHandling()
            .accessDeniedHandler(new OAuth2AccessDeniedHandler());
    }
}
```  

> AuthServerConfiguration extended AuthorizationServerConfigurerAdapter  

```java
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import lombok.RequiredArgsConstructor;
import server.account.service.AccountService;

@RequiredArgsConstructor
@Configuration
@EnableAuthorizationServer
public class AuthServerConfiguration extends AuthorizationServerConfigurerAdapter {

    // required
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AccountService accountService;
    private final DataSource dataSource;

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.passwordEncoder(passwordEncoder);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.jdbc(dataSource);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)
                 .userDetailsService(accountService)
                 .tokenStore(tokenStore())
                 .approvalStore(approvalStore());
    }

    @Bean
    public TokenStore tokenStore() {
        return new JdbcTokenStore(dataSource);
    }

    @Bean
    public ApprovalStore approvalStore() {
        return new JdbcApprovalStore(dataSource);
    }
}
```  

> Schema of oauth2  

See <a href="oauth-server/src/main/resources/schema-oauth2-h2.sql">schema-oauth2-h2.sql</a>.

<div id="test-oauth2-server"></div>

> ### Test OAuth2  

> Tests with spring test  

See <a href="oauth-server/src/test/java/server/OAuth2ClientIT.java">OAuth2ClientIT</a>.

```java
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
         * Try to access "/accounts/me" without access token.
         * Then receive unauthorized response
         */
        mockMvc.perform(get("/accounts/me"))
               .andDo(print())
               .andExpect(status().isUnauthorized());

        /**
         * 2) getting access token
         *
         * Try to get access token with BasicAuth.
         *
         * * Header
         *  - Key : "Authentication", Value: Base64Enc("application:pass")
         *
         * * Body with form-data
         *  - username=user@gmail.com
         *  - password=user
         *  - grant_type=password
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

        Map<String, Object> results = new Jackson2JsonParser().parseMap(response);
        String accessToken = results.get("access_token").toString();
        String refreshToken = results.get("refresh_token").toString();
        String bearerToken = "Bearer " + accessToken;

        /**
         * 3) request with auth token
         * Try to get resource with access token (Bearer)
         * * Header
         *  - Key : "Authentication", Value: Bearer <access-token>
         */
        mockMvc.perform(get("/accounts/me")
                                .header(HttpHeaders.AUTHORIZATION, bearerToken))
               .andDo(print())
               .andExpect(jsonPath("id").exists())
               .andExpect(jsonPath("email").value(username))
               .andExpect(jsonPath("age").exists());

        /**
         * 4) refresh token
         * Try to get a new access token with refresh token
         *
         * * Header
         *  - Key : "Authentication", Value: Base64Enc("application:pass")
         *
         * * Body with form-data
         * - grant_type=refresh_token
         * - refresh_token=<refresh-token>
         */
        perform = mockMvc.perform(post("/oauth/token")
                                          .with(httpBasic(clientId, clientSecret))
                                          .param("grant_type", "refresh_token")
                                          .param("refresh_token", refreshToken));

        response = perform.andReturn().getResponse().getContentAsString();
        results = new Jackson2JsonParser().parseMap(response);
        String updatedAccessToken = results.get("access_token").toString();
        String updatedBearerToken = "Bearer " + updatedAccessToken;

        assertThat(accessToken).isNotEqualTo(updatedAccessToken);

        // failed to request if access with prev token.
        mockMvc.perform(get("/accounts/me")
                                .header(HttpHeaders.AUTHORIZATION, bearerToken))
               .andExpect(status().isUnauthorized());

        // success to request if access with new token
        mockMvc.perform(get("/accounts/me")
                                .header(HttpHeaders.AUTHORIZATION, updatedBearerToken))
               .andDo(print())
               .andExpect(jsonPath("id").exists())
               .andExpect(jsonPath("email").value(username))
               .andExpect(jsonPath("age").exists());

    }
}
```

---  

<div id="client"></div>  

> ## Oauth2 client  
