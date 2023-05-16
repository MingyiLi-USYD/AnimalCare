package usyd.mingyi.animalcare.mapper;

import org.apache.ibatis.annotations.Mapper;
import usyd.mingyi.animalcare.pojo.Comment;
import usyd.mingyi.animalcare.pojo.Post;

import java.util.List;

@Mapper
public interface PostMapper {
    /** 
    * @Description: 发布一个朋友圈 
    * @Param: [post] 
    * @return: int 
    * @Author: Mingyi Li
    * @Date: 2022/10/5 
    */ 
    int addPost(Post post);
    /** 
    * @Description: 保存朋友圈下的图片信息 
    * @Param: [imagePostId, imageUrl] 
    * @return: int 
    * @Author: Mingyi Li
    * @Date: 2022/10/5 
    */ 
    int addImage(int imagePostId,String imageUrl);
    /** 
    * @Description: 获取所有的朋友圈默认基于发布时间排序
    * @Param: [currPage, pageSize] 
    * @return: java.util.List<usyd.mingyi.animalcare.pojo.Post> 
    * @Author: Mingyi Li
    * @Date: 2022/10/5 
    */ 
    List<Post> getAllPosts(int currPage, int pageSize);
    /** 
    * @Description: 获取所有的朋友圈基于点赞数排序 
    * @Param: [currPage, pageSize] 
    * @return: java.util.List<usyd.mingyi.animalcare.pojo.Post> 
    * @Author: Mingyi Li
    * @Date: 2022/10/5 
    */ 
    List<Post> getAllPostsOrderByLove(int currPage, int pageSize);
    /** 
    * @Description: 根据朋友圈的id找到具体的朋友圈信息 
    * @Param: [postId] 
    * @return: usyd.mingyi.animalcare.pojo.Post 
    * @Author: Mingyi Li
    * @Date: 2022/10/5 
    */ 
    Post queryPostById(int postId);
    /** 
    * @Description: 检查朋友圈是否已经被登录用户点过赞 
    * @Param: [userId, postId] 
    * @return: boolean 
    * @Author: Mingyi Li
    * @Date: 2022/10/5 
    */ 
    boolean checkLoved(int userId,int postId);
    /** 
    * @Description: 给朋友圈点赞
    * @Param: [userId, postId] 
    * @return: int 
    * @Author: Mingyi Li
    * @Date: 2022/10/5 
    */ 
    int love(int userId, int postId);
    /** 
    * @Description: 取消朋友圈点赞 
    * @Param: [userId, postId] 
    * @return: int 
    * @Author: Mingyi Li
    * @Date: 2022/10/5 
    */ 
    int cancelLove(int userId, int postId);
    /** 
    * @Description: 给朋友圈点赞加1 
    * @Param: [postId] 
    * @return: int 
    * @Author: Mingyi Li
    * @Date: 2022/10/5 
    */ 
    int lovePlus(int postId);
    /** 
    * @Description: 给朋友圈点赞数减1 
    * @Param: [postId] 
    * @return: int 
    * @Author: Mingyi Li
    * @Date: 2022/10/5 
    */ 
    int loveMinus(int postId);
    /** 
    * @Description: 删除对应的朋友圈 
    * @Param: [postId, userId] 
    * @return: int 
    * @Author: Mingyi Li
    * @Date: 2022/10/5 
    */ 
    int deletePost(int postId, int userId);
    /** 
    * @Description: 对朋友圈发表评论 
    * @Param: [comment] 
    * @return: int 
    * @Author: Mingyi Li
    * @Date: 2022/10/5 
    */ 
    int addComment(Comment comment);
    /** 
    * @Description:  根据用户id获取到所有的朋友圈
    * @Param: [userId] 
    * @return: java.util.List<usyd.mingyi.animalcare.pojo.Post> 
    * @Author: Mingyi Li
    * @Date: 2022/10/5 
    */ 
    List<Post> getPostsByUserId(int userId);
    /** 
    * @Description: 根据朋友圈id获取到其所有评论 
    * @Param: [postId] 
    * @return: java.util.List<usyd.mingyi.animalcare.pojo.Comment> 
    * @Author: Mingyi Li
    * @Date: 2022/10/5 
    */ 
    List<Comment> getCommentsByPostId(int postId);
    /** 
    * @Description: 根据关键词搜索朋友圈 
    * @Param: [keywords] 
    * @return: java.util.List<usyd.mingyi.animalcare.pojo.Post> 
    * @Author: Mingyi Li
    * @Date: 2022/10/5 
    */ 
    List<Post> getPostsByKeywords(String keywords);

}
