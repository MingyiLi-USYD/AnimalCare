package usyd.mingyi.animalcare.annotation;

import usyd.mingyi.animalcare.pojo.PostImage;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ImageValidator implements ConstraintValidator<Image, PostImage[]> {

    @Override
    public boolean isValid(PostImage[] value, ConstraintValidatorContext context) {

        if(value!=null&&value.length>0){
            return true;
        }
        return false;
    }
}
