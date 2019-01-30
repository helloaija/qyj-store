package com.qyj.store.config;

import com.qyj.store.common.util.EncryptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
public class QyjAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private QyjUserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String userName = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        QyjUserDetails userDetails = userDetailsService.loadUserByUsername(userName);

        if (userDetails == null) {
            throw new UsernameNotFoundException("用户名不存在！");
        }

        String passwordMD5 = EncryptionUtils.getMD5(password, "utf-8", 1);
        if (!userDetails.getPassword().equals(passwordMD5)) {
            throw new BadCredentialsException("密码不正确！");
        }

        UsernamePasswordAuthenticationToken usernamePasswordauthentication = new UsernamePasswordAuthenticationToken(userName, password, userDetails.getAuthorities());
        usernamePasswordauthentication.setDetails(userDetails);
        return usernamePasswordauthentication;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
