//import com.alibaba.druid.support.json.JSONUtils;
//import net.jcmob.joyplus.manage.jwt.JwtToken;
//import net.jcmob.joyplus.manage.jwt.TokenHelp;
//import org.apache.shiro.authc.AuthenticationException;
//import org.apache.shiro.authc.AuthenticationToken;
//import org.apache.shiro.subject.Subject;
//import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
//
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//
//public class JwtAuthenticationFilter extends AuthenticatingFilter {
//
//
//    @Override
//    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
//        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        String token = TokenHelp.getToken(httpRequest);
//        return JwtToken.builder()
//                .token(token)
//                .build();
//    }
//
//    @Override
//    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
//        return false;
//    }
//
//    @Override
//    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
//        return executeLogin(request, response);
//    }
//
//    @Override
//    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request,
//                                     ServletResponse response) throws Exception {
//        return true;
//    }
//
//    @Override
//    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException ae, ServletRequest request,
//                                     ServletResponse response) {
//        HttpServletResponse servletResponse = (HttpServletResponse) response;
//        try {
//            Map<String,Object> map = new HashMap<>();
//            map.put("msg",ae.getCause().getMessage());
//            map.put("code", 401);
//            map.put("timestamp", System.currentTimeMillis());
//            servletResponse.setCharacterEncoding("UTF-8");
//            servletResponse.setContentType("application/json;charset=UTF-8");
//            servletResponse.setHeader("Access-Control-Allow-Origin","*");
//            servletResponse.getOutputStream().write(JSONUtils.toJSONString(map).getBytes());
//        } catch (IOException e) {
//        }
//        return false;
//    }
//
//}
