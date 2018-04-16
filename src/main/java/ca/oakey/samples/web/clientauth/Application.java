package ca.oakey.samples.web.clientauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
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
            http.x509()
                    .and()
                    .headers()
                    /*
                     * Disable HSTS headers so localhost
                     * doesn't continue to re-direct to HTTPS
                     */
                    .httpStrictTransportSecurity()
                    .disable();
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
         * Return the authenticated username and roles.
         */
        @GetMapping("/whoami")
        public User whoami(Authentication authentication) {
            return User.fromAuthentication(authentication);
        }

    }
}
