package usyd.mingyi.animalcare.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ConstraintViolationException;
import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> sqlExceptionHandler(SQLIntegrityConstraintViolationException exception){
        log.info("存在SQL异常");
        if(exception.getMessage().contains("Duplicate entry")){
            String[] split = exception.getMessage().split(" ");
            return R.error(split[2]+"已经存在");
        }
        return R.error("未知错误");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<String> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException exception){
        log.info("存在参数异常");
        return R.error("存在参数异常");
    }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public R<String> HttpMessageNotReadableExceptionHandler(HttpMessageNotReadableException exception){
            log.info("日期格式不对");
        return R.error("日期格式不对");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public R<String> constraintViolationExceptionHandler(ConstraintViolationException exception){
        log.info("参数值有问题");
        return R.error(exception.getMessage());
    }
    @ExceptionHandler(CustomException.class)
    public R<String> customExceptionHandler(CustomException exception){
        log.info("存在SQL异常");
        return R.error(exception.getMessage());
    }
}
