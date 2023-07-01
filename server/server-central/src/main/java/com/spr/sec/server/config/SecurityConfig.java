package com.spr.sec.server.config;


import com.spr.sec.comp.service.UserService;
import com.spr.sec.web.mng.AdminManager;
import com.spr.sec.web.mng.UserManager;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
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
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Order(2)
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true) // @PreAuthorize 적용
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    //CsrfFilter csrfFilter;
    //UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter;

    private final CustomAuthDetails customAuthDetails;

    private final UserManager userManager;

    private final AdminManager adminManager;

    private final UserService userService;

    public SecurityConfig(CustomAuthDetails customAuthDetails, UserManager userManager, AdminManager adminManager, UserService userService) {
        this.customAuthDetails = customAuthDetails;
        this.userManager = userManager;
        this.adminManager = adminManager;
        this.userService = userService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {


        /**
         * DB에서 가져오기
         * insert into USER values(1, 'ho@google.com', TRUE, '1111');
         * insert into USER_AUTHORITY  values(1,'ROLE_USER');
         * insert into USER_AUTHORITY  values(1,'ROLE_ADMIN');
         * select * from USER ;
         * select * from USER_AUTHORITY;
         */
        auth.userDetailsService(userService);

        /**
         * inMemeory로 user와 password 생성

        auth.inMemoryAuthentication()
                .withUser(User.builder()
                    .username("user")
                    .password(passwordEncoder().encode("1111"))
                        .roles("USER")
                ).withUser(User.builder()
                        .username("v")
                        .password(passwordEncoder().encode("1"))
                        .roles("ADMIN")
                );
         */
        /**
         * AuthenticationManagerBuilder 의 authenticationProvider()
         * 는 List로 쌓아놓고 사용
         */
        auth.authenticationProvider(userManager);
        auth.authenticationProvider(adminManager);
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
     *
     */


}
