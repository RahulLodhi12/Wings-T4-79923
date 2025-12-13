package com.wings.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.wings.models.UserInfo;
import com.wings.repository.UserInfoRepository;

//@Component
//public class UserInfoUserDetailsService implements UserDetailsService{
//
//	@Autowired
//	private UserInfoRepository repository;
//	
//	@Override
//	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//		// TODO Auto-generated method stub
////		return null;
//		
//		Optional<UserInfo> userInfo = repository.findByUsername(username);
//
//        if(userInfo.isPresent()){
//            return new UserInfoUserDetails(userInfo.get()); //We can't directly return object of UserDetails, since UserDetails is an interface.
//        }
//        else{
//            throw new UsernameNotFoundException("User Not Found..");
//        }
//        /*
//        ✅ If user is found:
//            - It returns a new instance of MyUserDetails, a custom class that implements UserDetails.
//            - This object holds user’s username, password, roles, etc., and Spring uses it for authentication.
//
//        ❌ If user is not found, an exception is thrown → Spring Security rejects login and shows an error.
//        */
//	}
//
//}

@Component
public class UserInfoUserDetailsService implements UserDetailsService {

	@Autowired
	UserInfoRepository repo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
//		return null;
		
		Optional<UserInfo> user = repo.findByUsername(username);
		
		if(user.isEmpty()) {
			throw new UsernameNotFoundException("user not found");
		}
		
		return new UserInfoUserDetails(user.get());
		
	}
	
}
