# Oauth2 server & client with spring boot

- <a href="#start">getting started</a>
- <a href="#structure">source structure</a>
- <a href="#server">oauth2 resource server</a>
- <a href="#client">oauth2 client</a>

---  

<div id="start"></div>  

> ## Getting started    

will update

---  

<div id="structure"></div>  

> ## Source structure  

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

Configuration can see <a href="oauth-server/src/main/java/server/config">config package</a>.  

Also, u can see IT from <a href="oauth-server/src/test/java/server/OAuth2ClientIT.java">OAuth2ClientIT</a>.  

This test is consist of getting access token, refresh token, fail if no auth.  

<div id="client"></div>  

> ## Oauth2 client  
