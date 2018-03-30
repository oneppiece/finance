package com.tenry.finance.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Objects;

/**
 * 认证
 */
@Component
public class AccountAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * 自定义用户认证，封装用户信息
     *
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String userName = authentication.getName();
        String password = (String) authentication.getPrincipal();

        SysUserParam userDetails = (SysUserParam) userDetailsService.loadUserByUsername(userName);

        if (Objects.isNull(userDetails)) {
            throw new BadCredentialsException("用户名不存在！");
        }
        //加密
        AccountPasswordEncoder passwordEncoder = new AccountPasswordEncoder();
        String encodePwd = passwordEncoder.encode(password);

        if (!encodePwd.equals("123456")) {
            throw new BadCredentialsException("密码不正确!");
        }

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();


        return new UsernamePasswordAuthenticationToken(userDetails, password, authorities);
    }

    /**
     * @param authentication
     * @return true: 代表支持此配置的执行
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
