package usyd.mingyi.animalcare.annotation;

import javax.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(ElementType.PARAMETER)
@Constraint(validatedBy = DateValidator.class )
@Documented
@Retention(RUNTIME)
public @interface Date {

}
