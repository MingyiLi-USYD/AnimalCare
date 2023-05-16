package usyd.mingyi.animalcare.api;

import usyd.mingyi.animalcare.pojo.Comment;
import usyd.mingyi.animalcare.pojo.Post;
import usyd.mingyi.animalcare.pojo.User;

import java.util.List;

public interface InterfaceOne {

    List<Post> getPostsByUserId(int userId);

    List<Comment> getCommentsByPostId(int postId);

    int commentPost(int postId); //返回数据库被修改的行数 判断是否修改成功

    int updateInfo(User user); //修改用户个人信息

    int deletePost(int postId); //根据postId 删除对应的post

}
