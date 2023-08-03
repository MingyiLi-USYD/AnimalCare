package usyd.mingyi.animalcare.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import usyd.mingyi.animalcare.annotation.HasPermission;
import usyd.mingyi.animalcare.common.CustomException;
import usyd.mingyi.animalcare.utils.BaseContext;

import java.lang.reflect.Method;
import java.util.Arrays;

@Component
@Aspect
public class AuthAspect {

    @Around("@annotation(usyd.mingyi.animalcare.annotation.HasPermission)")
    public Object auth(ProceedingJoinPoint pjp) throws Throwable {
        //根据这个去获取用户role信息
        Long currentId = BaseContext.getCurrentId();
        //临时模拟
        String mockRole = "Admin";
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        HasPermission annotation = method.getAnnotation(HasPermission.class);
        String[] value = annotation.value();
        if(Arrays.asList(value).contains(mockRole)){
            return pjp.proceed();
        }else {
            throw new CustomException("没有权限");
        }

    }
}
