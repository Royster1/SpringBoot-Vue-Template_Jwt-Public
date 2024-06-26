package com.example.config;

import com.example.entity.RestBean;
import com.example.entity.vo.response.AuthorizeVO;
import com.example.utils.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// SpringSecurity基本配置
@Configuration
public class SecurityConfiguration {

    @Resource
    JwtUtils utils;

    // security安全过滤链
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // 拦截,登录登出,csrf关闭,状态管理
                .authorizeRequests(conf -> conf
                        // 这个url允许所有的请求放行
                        .requestMatchers(new AntPathRequestMatcher("/api/auth/**")).permitAll()
                        // 其他的需要验证登录才能访问
                        .anyRequest().authenticated()
                )
                .formLogin(conf -> conf
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler(this::onAuthenticationSuccess)
                        .failureHandler(this::onAuthenticationFailure)
                )
                .logout(conf -> conf
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(this::onLogoutSuccess)
                )
                .csrf(AbstractHttpConfigurer::disable)
                // 我们现在的是无状态的
                // 跟有状态的前后端分离最大的区别是 session是不用维护用户信息的, 用户信息都是在jwt里面, 所以将这个改为无状态,让security不去处理session
                .sessionManagement(conf -> conf
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    // 登录成功
    // 登录成功需要给用户发jwt令牌,不然访问不了
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // 告诉前端发送的数据是json格式,字符编码格式是utf-8
        response.setContentType("application/json;charset=utf-8");
        User user = (User) authentication.getPrincipal(); // 读取用户信息
        // 用户登录成功获取token
        String token = utils.createJwt(user,1, "小明");
        // 当然我们不仅需要获取token, 我们还需要获取到令牌的过期时间和用户信息(封装好)
        AuthorizeVO vo = new AuthorizeVO();
        vo.setExpire(utils.expireTime());
        vo.setRole("");
        vo.setToken(token);
        vo.setUsername("小明");
        response.getWriter().write(RestBean.success(vo).asJsonString());
    }

    // 登录失败
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        // 告诉前端发送的数据是json格式,字符编码格式是utf-8
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(RestBean.failure(401, exception.getMessage()).asJsonString());
    }

    // 退出登录成功
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

    }
}
