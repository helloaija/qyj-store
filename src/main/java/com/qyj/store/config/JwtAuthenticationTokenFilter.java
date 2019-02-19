package com.qyj.store.config;

import com.qyj.store.common.constant.CommonConstant;
import com.qyj.store.common.util.JwtTokenUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            final String authToken = authHeader.substring("Bearer ".length());
            String username = JwtTokenUtils.parseToken(authToken);

            if (username != null && CommonConstant.USER_LOGIN_MAP.containsKey(username)) {
                QyjUserDetails userDetails = CommonConstant.USER_LOGIN_MAP.get(username);

                // 如果登录用户不为空且登录的token和获取的token一致
                if (userDetails != null && authToken.equals(userDetails.getJwtToken())) {
                    QyjUserDetails qyjUserDetails = new QyjUserDetails();
                    BeanUtils.copyProperties(userDetails, qyjUserDetails);
                    // 重新设置登录过期时间，之所以copy一个新的对象，是因为之key、value一致无效
                    CommonConstant.USER_LOGIN_MAP.put(username, qyjUserDetails, CommonConstant.LOGOUT_TIME, TimeUnit.SECONDS);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        chain.doFilter(request, response);
    }
}
