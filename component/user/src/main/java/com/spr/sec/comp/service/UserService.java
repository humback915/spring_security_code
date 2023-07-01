package com.spr.sec.comp.service;

import com.spr.sec.comp.domain.UserAuthority;
import com.spr.sec.comp.domain.UserEntity;
import com.spr.sec.comp.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

    public Optional<UserEntity> findUser(String email){
        return userRepository.findUserByEmail(email);
    }

    public UserEntity save(UserEntity userEntity){
        return userRepository.save(userEntity);
    }

    public void addAuthority(Long userId, String authority){
        userRepository.findById(userId).ifPresent(user->{
            UserAuthority newRole = new UserAuthority(user.getUserId(), authority);

            if(user.getAuthorities()==null){
                HashSet<UserAuthority> authorities = new HashSet<>();

                authorities.add(newRole);
                user.setAuthorities(authorities);

                save(user);
            }else if(!user.getAuthorities().contains(newRole)){
                HashSet<UserAuthority> authorities = new HashSet<>();

                authorities.addAll(user.getAuthorities());
                authorities.add(newRole);
                user.setAuthorities(authorities);

                save(user);
            }
        });
    }

    public void removeAutority(Long userId, String authority){
        userRepository.findById(userId).ifPresent(user->{

            if(user.getAuthorities()==null){ return ; }
            UserAuthority targetRole = new UserAuthority(user.getUserId(), authority);

            if(user.getAuthorities().contains(targetRole)){
                user.setAuthorities(
                        user.getAuthorities().stream().filter(auth -> !auth.equals(targetRole))
                                .collect(Collectors.toSet())
                );

                save(user);
            }
        });
    }
}
