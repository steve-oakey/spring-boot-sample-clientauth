package ca.oakey.samples.web.clientauth;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private RestTemplate restTempate;

    @Test
    public void whoami() throws Exception {
        URI uri = URI.create("https://localhost:" + port + "/whoami");
        Map<String, Object> whoami = restTempate.getForObject(uri, Map.class);
        assertThat(whoami).containsEntry("name", "client");
    }
}