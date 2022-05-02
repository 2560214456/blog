package com.blog.shiro;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.blog.entity.TUser;
import com.blog.service.ITUserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpSession;

public class CustomerRealm extends AuthorizingRealm {
    @Autowired
    ITUserService service;
    @Autowired
    HttpSession session;
    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }
    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String username = (String) token.getPrincipal();
        TUser user = service.getOne(new LambdaQueryWrapper<TUser>().eq(TUser::getUsername, username));
        if (!ObjectUtils.isEmpty(user)){
            //把用户信息存储在session中
            SecurityUtils.getSubject().getSession().setAttribute("user",user);
            return new SimpleAuthenticationInfo(user.getUsername()
                        ,user.getPassword()
                        ,this.getName()
            );
        }
        return null;
    }
}
