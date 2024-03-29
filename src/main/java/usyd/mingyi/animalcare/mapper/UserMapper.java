package usyd.mingyi.animalcare.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import usyd.mingyi.animalcare.dto.UserDto;
import usyd.mingyi.animalcare.pojo.User;

import java.util.List;

@Mapper
@Repository
public interface UserMapper extends MPJBaseMapper<User> {
    /** 
    * @Description: 根据账号密码查询到具体用户 
    * @Param: [username, password] 
    * @return: usyd.mingyi.animalcare.pojo.User 
    * @Author: Mingyi Li
    * @Date: 2022/10/5 
    */ 
    User queryUser(String username,String password);
    /** 
    * @Description: 注册添加用户 
    * @Param: [user] 
    * @return: int 
    * @Author: Mingyi Li
    * @Date: 2022/10/5 
    */ 
    int addUser(User user);
    /** 
    * @Description: 根据用户名获取密码以便后面的校验 
    * @Param: [username] 
    * @return: java.lang.String 
    * @Author: Mingyi Li
    * @Date: 2022/10/5 
    */ 
    String queryPassword(String username);
    /** 
    * @Description: 更新用户的个人信息 
    * @Param: [user] 
    * @return: int 
    * @Author: Mingyi Li
    * @Date: 2022/10/5 
    */ 
    int updateUser(User user);
    /** 
    * @Description: 根据用户名查找到对应用户 
    * @Param: [username] 
    * @return: usyd.mingyi.animalcare.pojo.User 
    * @Author: Mingyi Li
    * @Date: 2022/10/5 
    */ 
    User queryUserByUsername(String username);

    /** 
    * @Description: 查看对应id的主页
    * @Param: [userId] 
    * @return: usyd.mingyi.animalcare.pojo.User 
    * @Author: Mingyi Li
    * @Date: 2022/10/5 
    */ 
    UserDto getProfile(long targetUserId,long currentUserId);

    int updatePassword(String username,String password);

}
