package usyd.mingyi.animalcare.config;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import usyd.mingyi.animalcare.common.R;
import usyd.mingyi.animalcare.utils.BaseContext;
import usyd.mingyi.animalcare.utils.JWTUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//登录页面拦截器
@Slf4j
public class LoginHandlerInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {

        //log.info("拦截前的线程ID {}和URI {}",  Thread.currentThread().getId(),request.getRequestURI());
        String token = request.getHeader("auth");
        if(token!=null&&JWTUtils.verify(token)){
             DecodedJWT tokenInfo = JWTUtils.getTokenInfo(token);
             Long userId = tokenInfo.getClaim("userId").asLong();
             BaseContext.setCurrentId(userId);
         }else {
             response.setCharacterEncoding("UTF-8");
             response.setContentType("application/json; charset=utf-8");
             response.setStatus(401);
             String resp = new ObjectMapper().writeValueAsString(R.error("请先登录"));
             response.getWriter().println(resp);
             return false;
         }
        return true;

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        BaseContext.clearCurrentId(); // 清理ThreadLocal中的数据
    }


}
