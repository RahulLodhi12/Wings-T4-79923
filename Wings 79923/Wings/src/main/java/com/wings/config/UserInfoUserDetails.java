package com.wings.config;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;

import com.wings.models.UserInfo;


public class UserInfoUserDetails implements UserDetails {

	private static final long serialVersionUID = 4122757742427151539L;
	private UserInfo userInfo;
	
	//set via constructor
	public UserInfoUserDetails(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	
	public UserInfo getUserInfo() {
		return userInfo;
	}
	
	private String name;
	private String password;
	private List<GrantedAuthority> authorities;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
//		return null;
		return List.of(new SimpleGrantedAuthority(userInfo.getRoles()));
	}

	@Override
	public String getPassword() {
		return userInfo.getPassword();
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return userInfo.getUsername();
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

    @Override
    public boolean isEnabled() {
        return true;
    }
}
