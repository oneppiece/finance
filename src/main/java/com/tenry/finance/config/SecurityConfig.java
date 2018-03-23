package com.tenry.finance.config;

import com.tenry.finance.account.AccountAuthenticationProvider;
import com.tenry.finance.account.AccountPasswordEncoder;
import com.tenry.finance.account.HandlerLoginFail;
import com.tenry.finance.account.HandlerLoginSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.session.HttpSessionEventPublisher;

/**
 * security 配置
 * Created by Administrator on 15:00.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private AccountAuthenticationProvider accountAuthenticationProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AccountPasswordEncoder accountPasswordEncoder;

    @Autowired
    private HandlerLoginSuccess handlerLoginSuccess;
    @Autowired
    private HandlerLoginFail handlerLoginFail;


    /**
     * 忽略的静态文件
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/static/**.**");
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .authenticationProvider(accountAuthenticationProvider)
                .userDetailsService(userDetailsService)
                .passwordEncoder(accountPasswordEncoder)
        ;
    }

    /**
     * 登陆,注销 的配置
     */

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/login", "/static/**/**.**", "/static/**.**", "/static/**/**/**.**").permitAll()                   //首页任意访问
                .anyRequest().authenticated()                                //其他所有资源都需要认证
                .and()
                .formLogin()                                                   //登录入口
                .loginPage("/login")                                           //登录页面入口
                .loginProcessingUrl("/doLogin")                                //登录表单中字段
                .passwordParameter("password")
                .usernameParameter("username")
                .successHandler(handlerLoginSuccess)                           //处理登陆成功
                .failureHandler(handlerLoginFail)                              //处理登陆成功
                .permitAll()                                                   //登录页面用户任意访问
                .and()
                .authorizeRequests()
                .anyRequest()
                .access("@rbacService.hasPermission(request,authentication)")
                .and()
                .sessionManagement()                                           //Session 超时，最大在线数量
                .maximumSessions(1)
                .maxSessionsPreventsLogin(true)
                .sessionRegistry(getSessionRegistry())
                .expiredUrl("/timeout")
                .and()
                .and().userDetailsService(userDetailsService)
                .exceptionHandling()
                .and()
                .logout()                                                      //注销
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
                .and().csrf().disable();

    }

    @Bean
    public SessionRegistry getSessionRegistry() {
        SessionRegistry sessionRegistry = new SessionRegistryImpl();
        return sessionRegistry;
    }

    @Bean
    public ServletListenerRegistrationBean httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean(new HttpSessionEventPublisher());
    }
}