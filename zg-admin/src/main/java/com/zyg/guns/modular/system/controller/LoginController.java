package com.zyg.guns.modular.system.controller;

import com.google.code.kaptcha.Constants;
import com.zyg.guns.core.base.controller.BaseController;
import com.zyg.guns.core.common.exception.InvalidKaptchaException;
import com.zyg.guns.core.log.LogManager;
import com.zyg.guns.core.log.factory.LogTaskFactory;
import com.zyg.guns.core.node.MenuNode;
import com.zyg.guns.core.shiro.JwtToken;
import com.zyg.guns.core.shiro.ShiroKit;
import com.zyg.guns.core.shiro.ShiroUser;
import com.zyg.guns.core.shiro.TokenUtil;
import com.zyg.guns.core.util.ApiMenuFilter;
import com.zyg.guns.core.util.KaptchaUtil;
import com.zyg.guns.core.util.ToolUtil;
import com.zyg.guns.modular.system.model.User;
import com.zyg.guns.modular.system.service.IMenuService;
import com.zyg.guns.modular.system.service.IUserService;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.zyg.guns.core.support.HttpKit.getIp;

/**
 * 登录控制器
 *
 * @author fengshuonan
 * @Date 2017年1月10日 下午8:25:24
 */
@Controller
public class LoginController extends BaseController {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private TokenUtil tokenUtil;
    @Autowired
    private IMenuService menuService;

    @Autowired
    private IUserService userService;

    /**
     * 跳转到主页
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model) {
        //获取菜单列表
        List<Integer> roleList = ShiroKit.getUser().getRoleList();
        if (roleList == null || roleList.size() == 0) {
            ShiroKit.getSubject().logout();
            model.addAttribute("tips", "该用户没有角色，无法登陆");
            return "/login.html";
        }
        List<MenuNode> menus = menuService.getMenusByRoleIds(roleList);
        List<MenuNode> titles = MenuNode.buildTitle(menus);
        titles = ApiMenuFilter.build(titles);

        model.addAttribute("titles", titles);

        //获取用户头像
        Integer id = ShiroKit.getUser().getId();
        User user = userService.selectById(id);
        String avatar = user.getAvatar();
        model.addAttribute("avatar", avatar);

        return "/index.html";
    }

    /**
     * 跳转到登录页面
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        if (ShiroKit.isAuthenticated() || ShiroKit.getUser() != null) {
            return REDIRECT + "/";
        } else {
            return "/login.html";
        }
    }

    /**
     * 点击登录执行的动作
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginVali(HttpServletRequest request, HttpServletResponse response,Device device) throws IOException{

        String username = super.getPara("username").trim();
        String password = super.getPara("password").trim();
        String remember = super.getPara("remember");

        //验证验证码是否正确
        if (KaptchaUtil.getKaptchaOnOff()) {
            String kaptcha = super.getPara("kaptcha").trim();
            String code = (String) super.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
            if (ToolUtil.isEmpty(kaptcha) || !kaptcha.equalsIgnoreCase(code)) {
                throw new InvalidKaptchaException();
            }
        }
        // 验证用户名密码成功后生成token
        String token = tokenUtil.generateToken(username, device);
        log.debug(token);
        JwtToken jwtToken = JwtToken.builder().token(token).principal(username).build();

        Subject currentUser = ShiroKit.getSubject();
//        UsernamePasswordToken jwtToken = new UsernamePasswordToken(username, password.toCharArray());

//        if ("on".equals(remember)) {
//            authtoken.setRememberMe(true);
//        } else {
//            authtoken.setRememberMe(false);
//        }

        currentUser.login(jwtToken);

        ShiroUser shiroUser = ShiroKit.getUser();
        super.getSession().setAttribute("shiroUser", shiroUser);
        super.getSession().setAttribute("username", shiroUser.getAccount());
        super.getSession().setAttribute("token", token);

        if(currentUser.isAuthenticated()){

            // 将token写出到cookie
            Cookie cookie =new Cookie("token",token);
            cookie.setHttpOnly(true);
            cookie.setMaxAge(3600 * 5);
            cookie.setPath("/");
            response.addCookie(cookie);
//            response.flushBuffer();
        }

        LogManager.me().executeLog(LogTaskFactory.loginLog(shiroUser.getId(), getIp()));

        ShiroKit.getSession().setAttribute("sessionFlag", true);

        return REDIRECT + "/";
    }

    /**
     * 退出登录
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logOut(HttpServletRequest request,HttpServletResponse response) throws IOException {
        LogManager.me().executeLog(LogTaskFactory.exitLog(ShiroKit.getUser().getId(), getIp()));

        Optional<Cookie> cookie = Arrays.stream(request.getCookies())
                .filter(ck -> "token".equals(ck.getName()))
                .limit(1)
                .map(ck -> {
                    ck.setMaxAge(0);
                    ck.setHttpOnly(true);
                    ck.setPath("/");
                    return ck;
                })
                .findFirst();
        response.addCookie(cookie.get());
//        response.flushBuffer();
        ShiroKit.getSubject().logout();
        return REDIRECT + "/login";
    }
}
