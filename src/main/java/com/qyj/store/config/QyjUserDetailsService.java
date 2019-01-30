package com.qyj.store.config;

import com.qyj.store.dao.SysUserMapper;
import com.qyj.store.entity.SysUserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class QyjUserDetailsService implements UserDetailsService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public QyjUserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        SysUserModel queryUser = new SysUserModel();
        queryUser.setUserName(userName);
        SysUserModel sysUserModel = sysUserMapper.getSysUserByUsername(userName);

        if (sysUserModel == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }

        QyjUserDetails userDetails = new QyjUserDetails();
        userDetails.setUsername(userName);
        userDetails.setPassword(sysUserModel.getPassword());
        userDetails.setAuthorities(null);

        return userDetails;
    }
}
