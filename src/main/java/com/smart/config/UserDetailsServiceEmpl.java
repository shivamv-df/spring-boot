package com.smart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smart.dao.UserrRepositry;
import com.smart.entites.User;

public class UserDetailsServiceEmpl  implements UserDetailsService{
    @Autowired 
	private UserrRepositry userrRepositry;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	
	User user=	userrRepositry.getUserByUserName(username);
		
		if (user==null) {
			throw new UsernameNotFoundException("could not found user");
		}
		CustamUserDetails custamUserDetails=new CustamUserDetails(user);
		return custamUserDetails;
	}

}
