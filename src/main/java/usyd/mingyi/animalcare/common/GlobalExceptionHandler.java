package usyd.mingyi.animalcare.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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

    @ExceptionHandler(CustomException.class)
    public R<String> customExceptionHandler(CustomException exception){
        log.info("存在SQL异常");
        return R.error(exception.getMessage());
    }
}
