//import net.jcmob.common.exception.LoginException;
//import net.jcmob.joyplus.dto.RoleDto;
//import net.jcmob.joyplus.entity.Resources;
//import net.jcmob.joyplus.entity.User;
//import net.jcmob.joyplus.service.ResourcesService;
//import net.jcmob.joyplus.service.RoleService;
//import net.jcmob.joyplus.service.UserService;
//import org.apache.shiro.authc.AuthenticationException;
//import org.apache.shiro.authc.AuthenticationInfo;
//import org.apache.shiro.authc.AuthenticationToken;
//import org.apache.shiro.authc.SimpleAuthenticationInfo;
//import org.apache.shiro.authz.AuthorizationInfo;
//import org.apache.shiro.authz.SimpleAuthorizationInfo;
//import org.apache.shiro.realm.AuthorizingRealm;
//import org.apache.shiro.subject.PrincipalCollection;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.List;
//
//
//public class JwtRealm extends AuthorizingRealm {
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private RoleService roleService;
//
//    @Autowired
//    private ResourcesService permissionService;
//
//
//    @Autowired
//    private TokenUtils tokenUtils;
//
//    @Override
//    public boolean supports(AuthenticationToken token) {
//        //表示此Realm只支持JwtToken类型
//        return token instanceof JwtToken;
//    }
//
//    /**
//     * 权限验证
//     * @param principals
//     * @return
//     */
//    @Override
//    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
//
//        String principal = (String) principals.getPrimaryPrincipal();
//
//        // 从数据库查询用户角色权限
//        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
//
//        if(principal!=null){
//
//            // 根据username查询角色
//            List<RoleDto> roles = roleService.getRoleByUserName(principal);
//            roles.forEach(role -> {
//                if (role.getCode()!=null){
//                    simpleAuthorizationInfo.addRole(role.getCode());
//                }
//            });
//
//            // 根据username查询权限
//            List<Resources> resources = permissionService.getResourcesByUserName(principal);
//            resources.forEach(res -> {
//                if (res.getPermission()!=null){
//                    simpleAuthorizationInfo.addStringPermission(res.getPermission());
//                }
//            });
//
//            // 初始可修改密码的权限
//            simpleAuthorizationInfo.addStringPermission("user:updatepwd");
//
//        }
//        return simpleAuthorizationInfo;
//    }
//
//    /**
//     * 登录验证
//     * @param authenticationToken
//     * @return
//     * @throws org.apache.shiro.authc.AuthenticationException
//     */
//    @Override
//    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
//        JwtToken jwtToken = (JwtToken) authenticationToken;
//        String token = jwtToken.getToken();
//
//        try {
//            String username = tokenUtils.getUsernameFromToken(token);
//
//            User user = userService.selectOne(User.builder().username(username).build());
//
//            // 校验用户
//            if(user == null || user.getId() <= 0){
//                throw new LoginException("用户不存在！");
//            }
//
//            // 验证用户状态
//            if(user.getStatus() != 1){
//                throw new LoginException("用户被禁用！");
//            }
//
//            return new SimpleAuthenticationInfo(
//                    username ,
//                    token,
//                    getName()
//            );
//        } catch (Exception e) {
//            throw new AuthenticationException(e);
//        }
//    }
//}
