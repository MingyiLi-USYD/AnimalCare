package usyd.mingyi.animalcare.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StatusValidator implements ConstraintValidator<Status, Byte> {

    @Override
    public boolean isValid(Byte value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // 允许参数为空
        }
        return value == 0 || value == 1;
    }
}