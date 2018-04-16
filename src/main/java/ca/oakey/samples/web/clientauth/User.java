package ca.oakey.samples.web.clientauth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class User {
    private String name;
    private String[] authorities;

    @SuppressWarnings("unused")
    private User() {

    }

    private User(String name, String[] authorities) {
        this.name = name;
        this.authorities = authorities;
    }

    public static User fromAuthentication(Authentication authentication) {
        String name = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return new User(name, authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toArray(String[]::new));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getAuthorities() {
        return authorities;
    }

    public void setAuthorities(String[] authorities) {
        this.authorities = authorities;
    }
}
