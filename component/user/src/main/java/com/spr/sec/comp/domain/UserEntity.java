package com.spr.sec.comp.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="user")
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="user_id", foreignKey =@ForeignKey(name="user_id"))
    private Set<UserAuthority> authorities;
    /**
     * UserDetails의 인터페이스 메소드를 Override해야하지만, 변수 authorities로 해결
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }
     */

    private String email;
    @Override
    public String getUsername() {
        return email;
    }
    private String password;
    /**
     *  @Override
    public String getPassword() {
        return null;
    }
     */
    private boolean enabled;
    /**
     * @Override
    public boolean isEnabled() {
        return false;
    }*/
    @Override
    public boolean isAccountNonExpired() {
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return enabled;
    }

}
