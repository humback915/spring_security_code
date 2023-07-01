package com.spr.sec.web.mng;

import com.spr.sec.web.domain.Admin;
import com.spr.sec.web.token.AdminAuthenticationToken;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Set;

@Component
public class AdminManager implements AuthenticationProvider, InitializingBean {

    private HashMap<Long, Admin> adminDB = new HashMap<>();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if(authentication instanceof  UsernamePasswordAuthenticationToken){
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
            if(adminDB.containsKey(token.getName())){
                return getAdminAuthenticationToken(token.getName());
            }
            return null;
        }
        AdminAuthenticationToken token = (AdminAuthenticationToken) authentication;
        if(adminDB.containsKey(token.getCredentials())){
            return getAdminAuthenticationToken(token.getCredentials());
        }
        return null;
    }

    private AdminAuthenticationToken getAdminAuthenticationToken(String id) {
        Admin admin = adminDB.get(id);
        return AdminAuthenticationToken.builder()
                .principal(admin)
                .details(admin.getName())
                .authenticated(true)
                .build();
    }

    /**
     * UsernamePasswordAuthenticationToken 받을 Token으로 Admin객체의 사용자 지정 Token으로 발행
     * UsernamePasswordAuthenticationToken -> AdminAuthenticationToken
     * @param authentication
     * @return
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication == AdminAuthenticationToken.class ||
                authentication == UsernamePasswordAuthenticationToken.class;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        Set.of(
                new Admin(1L, "v", Set.of(new SimpleGrantedAuthority("ROLE_ADMIN"))),
                new Admin(2L, "rm", Set.of(new SimpleGrantedAuthority("ROLE_ADMIN"))),
                new Admin(3L, "sugar", Set.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
        ).forEach(s->
                adminDB.put(s.getId(),s)
        );
    }
}
