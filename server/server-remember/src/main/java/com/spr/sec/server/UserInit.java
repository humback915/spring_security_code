package com.spr.sec.server;

import com.spr.sec.comp.domain.UserEntity;
import com.spr.sec.comp.service.UserService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserInit implements InitializingBean {

    @Autowired
    private UserService userService;

    @Override
    public void afterPropertiesSet() throws Exception {
        if(!userService.findUser("user1@gmail.com").isPresent()){
            UserEntity userEntity = userService.save(UserEntity.builder()
                            .email("user1@gmail.com")
                            .password("1111")
                            .enabled(true)
                    .build());
            userService.addAuthority(userEntity.getUserId(), "ROLE_USER");
        }
        if(!userService.findUser("user2@gmail.com").isPresent()){
            UserEntity userEntity = userService.save(UserEntity.builder()
                    .email("user2@gmail.com")
                    .password("1111")
                    .enabled(true)
                    .build());
            userService.addAuthority(userEntity.getUserId(), "ROLE_USER");
        }
        if(!userService.findUser("admin@gmail.com").isPresent()){
            UserEntity userEntity = userService.save(UserEntity.builder()
                    .email("admin@gmail.com")
                    .password("1111")
                    .enabled(true)
                    .build());
            userService.addAuthority(userEntity.getUserId(), "ROLE_ADMIN");
        }
    }
}
