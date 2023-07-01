package com.spr.sec.server;


import com.spr.sec.comp.service.UserService;
import com.spr.sec.web.mng.AdminManager;
import com.spr.sec.web.mng.UserManager;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import javax.servlet.http.HttpSessionEvent;
import java.time.LocalDateTime;

@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true) // @PreAuthorize 적용
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    RememberMeAuthenticationFilter rememberMeAuthenticationFilter;
    TokenBasedRememberMeServices tokenBasedRememberMeServices;
    PersistentRememberMeToken persistentRememberMeToken;
    private final UserService userService;

    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {


        /**
         * DB에서 가져오기
         */
        auth.userDetailsService(userService);
    }
    @Bean
    PasswordEncoder passwordEncoder(){
        //return new BCryptPasswordEncoder();
        return NoOpPasswordEncoder.getInstance();
    }

    /**
     * 권한 계층
     * A > B : A는 B의 권한도 적용할 수 있다.
     * @return
     */
    @Bean
    RoleHierarchy roleHierarchy(){
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
        return roleHierarchy;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        /**
         * formLogin Test
         */
        http
            .authorizeRequests((requests) -> {
                    requests
                    .antMatchers("/").permitAll()
                    .anyRequest().authenticated()
            ;
            //.antMatchers("/**").permitAll() 모든 페이지 허용
            })
            //.formLogin() // login 페이지 지정이 없을 시 DefaultLoginPageGenerationFilter 동작하여 Default 페이지 출력
            .formLogin(
                    login-> login.loginPage("/login")
                            .permitAll()
                            .defaultSuccessUrl("/", false) // alwaysUse 는 항상 false true일 경우 다시 root 페이지로 이동하게 됨.
                            .failureUrl("/login-error") // 로그인 실패시
                            //.authenticationDetailsSource(customAuthDetails) // detailSource 값 커스텀 지정.
            )
            .logout(logout->logout.logoutSuccessUrl("/"))
            .exceptionHandling(exception -> exception.accessDeniedPage("/access-denied"))
            .rememberMe();
        ;
    }

    /**
     * Web 리소스가 필터에 안걸리도록 해당 경로에 대한 무시 설정
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        //super.configure(web);
        web.ignoring()
                .requestMatchers(
                        PathRequest.toStaticResources().atCommonLocations(), // static리소스 경로 지정
                        PathRequest.toH2Console() // web에서 h2Console 접근 가능하도록 Path 열기
                )
                ;
    }

    /**
     * ServletListenerRegistrationBean
     * Servlet Linstner을 Bean으로 등록. 보통 web.xml있을 시에는 web.xml에 Listner등록.
     *  셰션 생성, 만료, 변경
     */
    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher(){
        return new ServletListenerRegistrationBean<HttpSessionEventPublisher>(
                new HttpSessionEventPublisher(){

                    @Override
                    public void sessionCreated(HttpSessionEvent event){
                        super.sessionCreated(event);
                        System.out.printf("[%s] 세션 생성 %s \n", LocalDateTime.now(), event.getSession().getId());
                    }
                    @Override
                    public void sessionDestroyed(HttpSessionEvent event){
                        super.sessionDestroyed(event);
                        System.out.printf("[%s] 세션 만료 %s \n", LocalDateTime.now(), event.getSession().getId());
                    }
                    @Override
                    public void sessionIdChanged(HttpSessionEvent event, String oldSessionId) {
                        super.sessionIdChanged(event, oldSessionId);
                        System.out.printf("[%s] 세션 변경 %s \n", LocalDateTime.now(), event.getSession().getId());
                    }
        });
    };


}
