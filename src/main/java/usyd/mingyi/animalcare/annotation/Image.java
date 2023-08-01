package usyd.mingyi.animalcare.annotation;


import javax.validation.Constraint;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target(FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = ImageValidator.class)
public @interface Image {
    String message() default "Please upload post with images";
}
