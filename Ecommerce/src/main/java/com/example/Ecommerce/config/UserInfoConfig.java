package com.example.Ecommerce.config;

import com.example.Ecommerce.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


//custom implementation of userDetails interface
//customization

@Data
@NoArgsConstructor
@AllArgsConstructor


public class UserInfoConfig implements UserDetails {

    //unique identifier for serializable class
    @Serial
    private static final long serialVersionUID = 1L;
    private String email;
    private String password;
    private List<GrantedAuthority>authorities;

    public UserInfoConfig(User user) {
        this.email = user.getEmail();
        this.password = user.getPassword();

        //Converts the user's roles to a list of GrantedAuthority objects using a stream
        this.authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                .collect(Collectors.toList());

    }


    @Override
    //Returns the list of authorities granted to the user
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    //account is never expired
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //account is never locked
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //credentials never expired
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //user is always enabled
    @Override
    public boolean isEnabled() {
        return true;
    }
}
