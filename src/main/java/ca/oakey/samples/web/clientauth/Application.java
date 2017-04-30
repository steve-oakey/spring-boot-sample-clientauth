package ca.oakey.samples.web.clientauth;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /*
     * Create a RestTemplate bean, using the RestTemplateBuilder provided
     * by the auto-configuration.
     */
    @Bean
    RestTemplate restTemplate(RestTemplateBuilder builder) throws Exception {

        /*
         * Sample certs use the same password
         */
        char[] password = "password".toCharArray();

        /*
         * Create an SSLContext that uses client.jks as the client certificate
         * and the truststore.jks as the trust material (trusted CA certificates).
         * In this sample, truststore.jks contains ca.pem which was used to sign
         * both client.pfx and server.jks.
         */
        SSLContext sslContext = SSLContextBuilder
                .create()
                .loadKeyMaterial(loadPfx("classpath:client.pfx", password), password)
                .loadTrustMaterial(ResourceUtils.getFile("classpath:truststore.jks"), password)
                .build();

        /*
         * Create an HttpClient that uses the custom SSLContext
         */
        HttpClient client = HttpClients.custom()
                .setSSLContext(sslContext)
                .build();

        /*
         * Create a RestTemplate that uses a request factory that references
         * our custom HttpClient
         */
        return builder
                .requestFactory(new HttpComponentsClientHttpRequestFactory(client))
                .build();
    }

    private KeyStore loadPfx(String file, char[] password) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        File key = ResourceUtils.getFile(file);
        try (InputStream in = new FileInputStream(key)) {
            keyStore.load(in, password);
        }
        return keyStore;
    }

    /*
     * Web security configuration.
     */
    @Configuration
    static class WebSecurityConfig extends WebSecurityConfigurerAdapter {

        /*
         * Enable x509 client authentication.
         */
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.x509();
        }

        /*
         * Create an in-memory authentication manager. We create 1 user (client which
         * is the CN of the client certificate) which has a role of USER.
         */
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication()
                    .withUser("client")
                    .password("none")
                    .roles("USER");
        }
    }

    @RestController
    static class TestController {

        /*
         * Return the authenicated username and roles.
         */
        @GetMapping("/whoami")
        public Map<String, Object> whoami(Authentication authenication) {
            String name = authenication.getName();
            Collection<? extends GrantedAuthority> authorities = authenication.getAuthorities();
            Map<String, Object> values = new HashMap<>();
            values.put("name", name);
            values.put("authorities", authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));
            return values;
        }

    }
}
