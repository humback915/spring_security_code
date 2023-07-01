package com.spr.sec.server.config;


import com.spr.sec.web.mng.AdminManager;
import com.spr.sec.web.mng.UserManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;

@Order(1)
@Configuration
public class MobSecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserManager userManager;

    private final AdminManager adminManager;

    public MobSecurityConfig(UserManager userManager, AdminManager adminManager) {
        this.userManager = userManager;
        this.adminManager = adminManager;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        /**
         * AuthenticationManagerBuilder 의 authenticationProvider()
         * 는 List로 쌓아놓고 사용
         */
        auth.authenticationProvider(userManager);
        auth.authenticationProvider(adminManager);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /**
         * BasicAuthenticationFilter Test
         */
        http
                .antMatcher("/rest/**")
                .csrf().disable()
                .authorizeRequests(request->request.anyRequest().authenticated()) //permitAll()
                .httpBasic()
                ;
    }
}
