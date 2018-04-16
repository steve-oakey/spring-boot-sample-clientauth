package ca.oakey.samples.web.clientauth;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.security.KeyStore;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private RestTemplate restTempate;

    @Test
    public void whoami() {
        URI uri = URI.create("https://localhost:" + port + "/whoami");
        User whoami = restTempate.getForObject(uri, User.class);
        assertThat(whoami.getName()).isEqualTo("client");
        assertThat(whoami.getAuthorities()).contains("ROLE_USER");
    }

    @TestConfiguration
    static class ClientAuthRestTemplateConfig {
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
                    .requestFactory(() -> new HttpComponentsClientHttpRequestFactory(client))
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
    }
}