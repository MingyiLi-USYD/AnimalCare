package usyd.mingyi.animalcare.annotation;

import com.auth0.jwt.interfaces.Payload;

import javax.validation.Constraint;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = StatusValidator.class)
public @interface Status {
    String message() default "status参数必须为0或1";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
