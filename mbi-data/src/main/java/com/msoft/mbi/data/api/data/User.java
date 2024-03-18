package com.msoft.mbi.data.api.data;

import com.msoft.mbi.model.BIUserEntity;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class User implements UserDetails {

    private String userName;
    private String password;
    private boolean isEnabled;
    private List<GrantedAuthority> authorities;

    public User(BIUserEntity biUser) {
        this.userName = biUser.getEmail(); // biUser.getFirstName() + "-" + biUser.getLastName();
        this.password = biUser.getPassword();
        this.isEnabled = biUser.getIsActive();
        this.authorities = Arrays.stream(
                biUser.getBiUserGroupByUserGroup().getRoleCode().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
