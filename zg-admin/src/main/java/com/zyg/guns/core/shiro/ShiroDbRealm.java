package com.zyg.guns.core.shiro;

import com.zyg.guns.core.shiro.factory.IShiro;
import com.zyg.guns.core.shiro.factory.ShiroFactroy;
import com.zyg.guns.core.util.ToolUtil;
import com.zyg.guns.core.utils.StringUtil;
import com.zyg.guns.modular.system.model.User;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ShiroDbRealm extends AuthorizingRealm {

    @Autowired
    private TokenUtil tokenUtil;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public boolean supports(AuthenticationToken token) {
        //表示此Realm只支持JwtToken类型
        return token instanceof JwtToken;
    }
    /**
     * 登录认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken)
            throws AuthenticationException {
        JwtToken jwtToken = (JwtToken) authcToken;
        // 获取token
        String token = jwtToken.getToken();
        if(StringUtil.isEmpty(token)){
            throw new AuthenticationException();
        }
        String username = tokenUtil.getUsernameFromToken(token);
//        IShiro shiroFactory = ShiroFactroy.me();
//        User user = shiroFactory.user(username);
        // 从token中获取用户名

//        String credentials = user.getPassword();

        // 密码加盐处理
//        String source = user.getSalt();
//        ByteSource credentialsSalt = new Md5Hash(source);

//        ShiroUser shiroUser = shiroFactory.shiroUser(user);
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(
                username,
                token,
                getName());
//        IShiro shiroFactory = ShiroFactroy.me();
//        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
//        User user = shiroFactory.user(token.getUsername());
//        ShiroUser shiroUser = shiroFactory.shiroUser(user);
//        SimpleAuthenticationInfo info = shiroFactory.info(shiroUser, user, super.getName());
        return info;
    }

    /**
     * 权限认证
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        IShiro shiroFactory = ShiroFactroy.me();
        String username = (String) principals.getPrimaryPrincipal();
        User user = shiroFactory.user(username);
        ShiroUser shiroUser = shiroFactory.shiroUser(user);
        List<Integer> roleList = shiroUser.getRoleList();

        Set<String> permissionSet = new HashSet<>();
        Set<String> roleNameSet = new HashSet<>();

        for (Integer roleId : roleList) {
            List<String> permissions = shiroFactory.findPermissionsByRoleId(roleId);
            if (permissions != null) {
                for (String permission : permissions) {
                    if (ToolUtil.isNotEmpty(permission)) {
                        permissionSet.add(permission);
                    }
                }
            }
            String roleName = shiroFactory.findRoleNameByRoleId(roleId);
            roleNameSet.add(roleName);
        }

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.addStringPermissions(permissionSet);
        info.addRoles(roleNameSet);
        return info;
    }

    /**
     * 设置认证加密方式
     */
    @Override
    public void setCredentialsMatcher(CredentialsMatcher credentialsMatcher) {
        SimpleCredentialsMatcher simpleCredentialsMatcher = new SimpleCredentialsMatcher();
        super.setCredentialsMatcher(simpleCredentialsMatcher);
    }
}
