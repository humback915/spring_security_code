package com.spr.sec.web.token;

import com.spr.sec.web.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.security.auth.Subject;
import java.util.Collection;
import java.util.HashSet;

/**
 * User객체가 사이트 접속시 받게 되는 Token
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAuthenticationToken implements Authentication {

    private User principal;
    private String credentials;
    private String details;
    private boolean authenticated;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return principal == null ? new HashSet<>() : principal.getRole();
    }

    @Override
    public String getName() {
        return principal == null ? "" : principal.getName();
    }
}
