package usyd.mingyi.animalcare.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import usyd.mingyi.animalcare.utils.BaseContext;

import java.time.LocalDateTime;

@Component
public class MybatisPlusMetaObjectHandler implements MetaObjectHandler {
    public final static String CREATE_TIME = "createTime";
    public final static String CREATE_USER = "createUser";
    public final static String UPDATE_TIME = "updateTime";
    public final static String UPDATE_USER = "updateUser";
    @Override
    public void insertFill(MetaObject metaObject) {
        metaObject.setValue(CREATE_TIME, LocalDateTime.now());
        metaObject.setValue(UPDATE_TIME, LocalDateTime.now());
        metaObject.setValue(CREATE_USER, BaseContext.getCurrentId());
        metaObject.setValue(UPDATE_USER, BaseContext.getCurrentId());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue(UPDATE_TIME, LocalDateTime.now());
        metaObject.setValue(UPDATE_USER, BaseContext.getCurrentId());
    }
}
