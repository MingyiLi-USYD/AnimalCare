package usyd.mingyi.animalcare.annotation;

import usyd.mingyi.animalcare.common.CustomException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateValidator implements ConstraintValidator<Date,String> {
    @Override
    public void initialize(Date constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                java.util.Date parse = sdf.parse(value);
            } catch (ParseException e) {
                throw new CustomException("Date convert error");
            }
           return  true;

    }
}
