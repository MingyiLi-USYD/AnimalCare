package usyd.mingyi.animalcare.config;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import usyd.mingyi.animalcare.common.R;
import usyd.mingyi.animalcare.utils.BaseContext;
import usyd.mingyi.animalcare.utils.JWTUtils;
import usyd.mingyi.animalcare.utils.ResultData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.util.Map;

//登录页面拦截器
@Slf4j
public class LoginHandlerInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info("拦截前的线程ID {}和URI {}",  Thread.currentThread().getId(),request.getRequestURI());
        String token = request.getHeader("auth");
         if(token!=null&&JWTUtils.verify(token)){
             //log.info("Thread id is: {}",Thread.currentThread().getId());
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

 /*       log.info("被拦截的路径: " + request.getRequestURI());

        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("id") != null) {
            Long id = (Long) request.getSession().getAttribute("id");
            log.info("Thread id is: {}",Thread.currentThread().getId());
            BaseContext.setCurrentId(id);
            return true;
        } else {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            response.setStatus(401);
            PrintWriter out = response.getWriter();
            out.append(ResultData.fail(401, "Please login first").toString());
            return false;
        }*/
        return true;

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
