package usyd.mingyi.animalcare.mapper;

import org.apache.ibatis.annotations.Mapper;
import usyd.mingyi.animalcare.pojo.User;

import java.util.List;

@Mapper
public interface FriendMapper {
    /** 
    * @Description:  检查是否自己已经发过同样的请求
    * @Param: [fromId, toId] 
    * @return: int 
    * @Author: Mingyi Li
    * @Date: 2022/10/5 
    */ 
    int checkExistRequest(int fromId,int toId);
    /** 
    * @Description:  检查是否被该用户请求添加过
    * @Param: [fromId, toId] 
    * @return: int 
    * @Author: Mingyi Li
    * @Date: 2022/10/5 
    */ 
    int checkRequestReverse(int fromId,int toId);
    /** 
    * @Description:  发送到对方请求列表中
    * @Param: [fromId, toId] 
    * @return: int 
    * @Author: Mingyi Li
    * @Date: 2022/10/5 
    */ 
    int sendFriendRequest(int fromId,int toId);
    /** 
    * @Description:  在请求列表中删除请求
    * @Param: [fromId, toId] 
    * @return: int 
    * @Author: Mingyi Li
    * @Date: 2022/10/5 
    */ 
    int deleteFriendRequest(int fromId,int toId);//
    
    /** 
    * @Description:  互相添加到好友列表
    * @Param: [fromId, toId] 
    * @return: int 
    * @Author: Mingyi Li
    * @Date: 2022/10/5 
    */ 
    int addToFriendList(int fromId,int toId);
    
    /** 
    * @Description: 判断是否已经是好友了
    * @Param: [fromId, toId] 
    * @return: boolean 
    * @Author: Mingyi Li
    * @Date: 2022/10/5 
    */
    
    boolean isFriend(int fromId,int toId);
    /** 
    * @Description:  根据用户id获取用户所有的friends
    * @Param: [id] 
    * @return: java.util.List<usyd.mingyi.animalcare.pojo.User> 
    * @Author: Mingyi Li
    * @Date: 2022/10/5 
    */ 
    List<User> getAllFriends(int id);
    /** 
    * @Description: 根据用户id获取用户所有的requests
    * @Param: [id] 
    * @return: java.util.List<usyd.mingyi.animalcare.pojo.User> 
    * @Author: Mingyi Li
    * @Date: 2022/10/5 
    */

    List<User> getAllRequests(int id);


    /** 
    * @Description:
    * @Param: [fromId, toId] 
    * @return: int 
    * @Author: Mingyi Li
    * @Date: 2022/10/8 
    */ 
    int deleteFromFriendList(int fromId,int toId);
    

}
