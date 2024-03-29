package com.example.community.config;

import com.example.community.util.CommunityConstant;
import com.example.community.util.CommunityUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {

    @Override
    public void configure(WebSecurity web) throws Exception {
//        忽略静态资源的访问
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        授权
        http.authorizeRequests()
                .antMatchers(
                        "/user/setting",
                        "/user/upload",
                        "/user/updatePassword",
                        "/discuss/add",
                        "/comment/add/**",
                        "/letter/**",
                        "/notice/**",
                        "/like",
                        "/follow",
                        "/unfollow"
                )
                .hasAnyAuthority(
                        AUTHORITY_USER,
                        AUTHORITY_ADMIN,
                        AUTHORITY_MODERATOR
                )
//                只有版主有置顶和加精的权限
                .antMatchers(
                        "/discuss/top",
                        "/discuss/wonderful"
                )
                .hasAnyAuthority(
                        AUTHORITY_MODERATOR
                )
//                只有管理员有删帖的权限
                .antMatchers(
                        "/discuss/delete",
                        "/data/**"
                )
                .hasAnyAuthority(
                        AUTHORITY_ADMIN
                )
                // 除了上面那些以外的任何请求，都允许
                .anyRequest().permitAll()
                // 这里可以关闭csrf验证，默认是开启的，如果启用则需要在模板页面和模板js文件引用CSRF令牌，否则权限认证不通过。
                .and().csrf().disable();

//        权限不够时的处理
        http.exceptionHandling()
//                没有登录时的处理
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if ("XMLHttpRequest".equals(xRequestedWith)){
//                            异步请求
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter printWriter = response.getWriter();
                            printWriter.write(CommunityUtil.getJSONString(403, "你没有登录！"));
                        }else {
                            response.sendRedirect(request.getContextPath() + "/login");
                        }
                    }
                })
//                权限不足时的处理
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                        String xRequestedWith = request.getHeader("x-requested-with");
                        if ("XMLHttpRequest".equals(xRequestedWith)){
//                            异步请求
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter printWriter = response.getWriter();
                            printWriter.write(CommunityUtil.getJSONString(403, "你没有访问此功能的权限！"));
                        }else {
                            response.sendRedirect(request.getContextPath() + "/denied");
                        }
                    }
                });

//        Security底层默认拦截/logout请求，进行退出处理
//        覆盖它的逻辑，才能执行我们自己的退出代码
//        将Security默认拦截/logout的url修改为一个不存在的路径，即让Security拦截一个不存在的路径，不拦截/logout
        http.logout().logoutUrl("/securitylogout");
    }
}
