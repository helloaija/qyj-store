package com.qyj.store.config;

import com.alibaba.fastjson.JSON;
import com.qyj.common.page.ResultBean;
import com.qyj.common.utils.StringUtils;
import com.qyj.store.common.constant.CommonConstant;
import com.qyj.store.common.util.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;


@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private QyjAuthenticationProvider provider;

    @Autowired
    private OncePerRequestFilter jwtAuthenticationTokenFilter;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 加入自定义的安全认证
        auth.authenticationProvider(provider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 添加转码
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding("UTF-8");
        encodingFilter.setForceEncoding(true);
        http.addFilterBefore(encodingFilter, CsrfFilter.class);

        http.csrf().disable().cors();

        http.authorizeRequests().antMatchers("/admin/**").authenticated().anyRequest().permitAll();

        http.httpBasic().authenticationEntryPoint((request, response, authentication) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            // 用来解决匿名用户访问无权限资源时的异常
            response.getWriter().write(JSON.toJSONString(new ResultBean("0001", "用户未登录")));
        });

        http.formLogin().loginProcessingUrl("/login").successHandler((request, response, authentication) -> {
            // 登录成功
            QyjUserDetails userDetails = (QyjUserDetails) authentication.getDetails();
            String jwtToken = JwtTokenUtils.generateToken(userDetails.getUsername());
            // 设置当前登录的token
            userDetails.setJwtToken(jwtToken);
            CommonConstant.USER_LOGIN_MAP.put(userDetails.getUsername(), userDetails, CommonConstant.LOGOUT_TIME, TimeUnit.SECONDS);
            ResultBean resultBean = new ResultBean("0000", "登录成功", jwtToken);
            response.getWriter().write(JSON.toJSONString(resultBean));
        }).failureHandler((request, response, authentication) -> {
            // response.setCharacterEncoding("UTF-8");
            // 登录失败
            response.getWriter().write(JSON.toJSONString(new ResultBean("0001", authentication.getMessage())));
        }).permitAll();

        http.exceptionHandling().accessDeniedHandler((request, response, authentication) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            // 用来解决认证过的用户访问无权限资源时的异常
            response.getWriter().write(JSON.toJSONString(new ResultBean("0001", "无权限访问")));
        });

        http.logout().logoutSuccessHandler((request, response, authentication) -> {
            // 退出成功
            String userName = (String) authentication.getPrincipal();
            if (!StringUtils.isEmpty(userName)) {
                CommonConstant.USER_LOGIN_MAP.remove(userName);
            }

            response.getWriter().write(JSON.toJSONString(new ResultBean("0000", "退出成功")));
        });

        // 禁用session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // 访问过滤
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }

}