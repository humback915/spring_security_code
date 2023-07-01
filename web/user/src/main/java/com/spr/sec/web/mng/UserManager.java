package com.spr.sec.web.mng;

import com.spr.sec.web.domain.User;
import com.spr.sec.web.token.UserAuthenticationToken;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserManager implements AuthenticationProvider, InitializingBean {

    private HashMap<String, User> userDB = new HashMap<>();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if(authentication instanceof  UsernamePasswordAuthenticationToken){
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
            if(userDB.containsKey(token.getName())){
                return getUserAuthenticationToken(token.getName());
            }
            return null;
        }
        UserAuthenticationToken token = (UserAuthenticationToken) authentication;
        if(userDB.containsKey(token.getCredentials())){
            return getUserAuthenticationToken(token.getCredentials());
        }
        return null;
    }

    private UserAuthenticationToken getUserAuthenticationToken(String id) {
        User user = userDB.get(id);
        return UserAuthenticationToken.builder()
                .principal(user)
                .details(user.getName())
                .authenticated(true)
                .build();
    }

    /**
     * UsernamePasswordAuthenticationFilter에서 받을 Token으로 User객체의 사용자 지정 Token으로 발행
     * UsernamePasswordAuthenticationToken -> UserAuthenticationToken
     * @param authentication
     * @return
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication == UserAuthenticationToken.class ||
                authentication == UsernamePasswordAuthenticationToken.class;
    }

    public List<User> myUserList(Long adminId){
        return userDB.values().stream().filter(s->s.getAdminId().equals(adminId))
                .collect(Collectors.toList());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Set.of(
                new User("jay", "제이", Set.of(new SimpleGrantedAuthority("ROLE_USER")), 1L),
                new User("hope", "홉", Set.of(new SimpleGrantedAuthority("ROLE_USER")), 1L),
                new User("jung", "정", Set.of(new SimpleGrantedAuthority("ROLE_USER")), 1L)
        ).forEach(s->
                userDB.put(s.getId(),s)
        );
    }
}
