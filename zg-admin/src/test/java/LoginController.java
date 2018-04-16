//import com.google.code.kaptcha.impl.DefaultKaptcha;
//import com.google.common.collect.ImmutableMap;
//import com.google.common.collect.Maps;
//import net.jcmob.common.enums.RoleCodeEnum;
//import net.jcmob.common.util.PasswordHelper;
//import net.jcmob.joyplus.entity.User;
//import net.jcmob.joyplus.manage.filter.JcaptchaValidateFilter;
//import net.jcmob.joyplus.manage.jwt.JwtToken;
//import net.jcmob.joyplus.manage.jwt.LoginUser;
//import net.jcmob.joyplus.manage.jwt.SubjectUtil;
//import net.jcmob.joyplus.manage.jwt.TokenUtils;
//import net.jcmob.joyplus.manage.log.OperationLog;
//import net.jcmob.joyplus.service.RoleService;
//import net.jcmob.joyplus.service.UserService;
//import org.apache.commons.codec.binary.Base64;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.shiro.SecurityUtils;
//import org.apache.shiro.authz.annotation.RequiresAuthentication;
//import org.apache.shiro.subject.Subject;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.web.bind.annotation.*;
//
//import javax.imageio.ImageIO;
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.util.*;
//import java.util.concurrent.TimeUnit;
//
//@RestController
//@RequestMapping("/auth")
//public class LoginController extends BaseController {
//
//    @Autowired
//    private TokenUtils tokenUtils;
//
//    @Autowired
//    private SubjectUtil subjectUtil;
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private RoleService roleService;
//
//    @Autowired
//    private DefaultKaptcha captchaProducer;
//
//    @Autowired
//    private RedisTemplate<String,Object> redisTemplate;
//
//    @GetMapping(value="/captcha")
//    public Object captcha(){
//
//        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
//            String capText = captchaProducer.createText();
//            String uuid = UUID.randomUUID().toString();
//            redisTemplate.boundValueOps(uuid).set(capText,60, TimeUnit.SECONDS);
//            BufferedImage bi = captchaProducer.createImage(capText);
//            ImageIO.write(bi, "jpg", baos);
//            String imgBase64 = Base64.encodeBase64String(baos.toByteArray());
//            return success(ImmutableMap.of(uuid,"data:image/jpeg;base64,"+imgBase64));
//        } catch (IOException e) {
//            throw new RuntimeException(e.getMessage(),e);
//        }
//    }
//
//    @PostMapping(value = "/login")
//    @OperationLog(description = "用户登录")
//    public Object login(@RequestBody LoginUser loginUser, HttpServletRequest request,HttpServletResponse response){
//        String message = (String)request.getAttribute(JcaptchaValidateFilter.failureKeyAttribute);
//        //验证码失败
//        if(StringUtils.isNotBlank(message)){
//            return error(message);
//        }
//
//        // 根据用户名查询用户
//        User user = userService.selectOne(User.builder().username(loginUser.getUsername()).build());
//        if(user == null){
//            return error("用户不存在！");
//        }
//
//        // 验证用户状态
//        if(user.getStatus() != 1){
//            return error("用户已禁用！");
//        }
//
//        String pwd = PasswordHelper.builder().build().encrypt(loginUser.getPassword(), loginUser.getUsername(), user.getSalt());
//
//        // 验证密码
//        if(!StringUtils.equalsIgnoreCase(pwd,user.getPassword())){
//            return error("密码错误！");
//        }
//
//        Boolean superAdmin=false;
//        Boolean operation=false;
//        List<String> userCode = roleService.getUserCode(user.getId());
//        if (userCode == null) {
//            return error("该用户尚未分配任何角色，请联系超级管理员分配角色！");
//        }
//        for(String code : userCode){
//            if(StringUtils.equalsIgnoreCase(code, RoleCodeEnum.SUPER_ADMIN.getValue()) || StringUtils.equalsIgnoreCase(code, RoleCodeEnum.MANAGER.getValue())){
//                superAdmin=true;
//            }
//            if(StringUtils.equalsIgnoreCase(code, RoleCodeEnum.OPERATION.getValue())){
//                operation=true;
//            }
//        }
//        // 验证用户名密码成功后生成token
//        String token = tokenUtils.generateToken(loginUser.getUsername(),user.getId(),superAdmin,operation);
//        JwtToken jwtToken = JwtToken.builder().token(token).principal(loginUser.getUsername()).build();
//
//        Subject subject = SecurityUtils.getSubject();
//
//        subject.login(jwtToken);
//
//        // 认证成功后把token写出到Cookie
//        if (subject.isAuthenticated()){
//            Cookie cookie = new Cookie("token",token);
//            cookie.setHttpOnly(true);
//            cookie.setMaxAge(3600 * 5);
//            cookie.setPath("/");
//            response.addCookie(cookie);
//        }
//
//        return success();
//    }
//
//    @GetMapping(value = "/islogin")
//    public Object islogin(@CookieValue("token") String token){
//        if(StringUtils.isBlank(token)){
//            return error("令牌为空！");
//        }
//        Long userId = subjectUtil.getSubjectIdFromToken(token);
//
//        if(userId == null){
//            return error("用户不存在！");
//        }
//
//        if(userId > 0){
//            return success();
//        }
//        return error("用户不正确！");
//    }
//
//
//    @GetMapping(value = "/logout")
//    @OperationLog(description = "用户退出")
//    public Object logout(HttpServletRequest request,HttpServletResponse response) throws IOException {
//        Optional<Cookie> cookie = Arrays.stream(request.getCookies())
//                .filter(ck -> "token".equals(ck.getName()))
//                .limit(1)
//                .map(ck -> {
//                    ck.setMaxAge(0);
//                    ck.setHttpOnly(true);
//                    ck.setPath("/");
//                    return ck;
//                })
//                .findFirst();
//        response.addCookie(cookie.get());
//        response.flushBuffer();
//        return success();
//    }
//
//
//    /**
//     * 刷新token
//     * @param token token
//     * @return 新token
//     */
//    @GetMapping("/token/refresh")
//    @RequiresAuthentication
//    public Object refreshToken(@CookieValue("token") String token) {
//        String newToken = this.tokenUtils.refreshToken(token);
//        HashMap<Object, Object> ret = Maps.newHashMap();
//        ret.put("token", newToken);
//        return success(ret);
//    }
//
//}
