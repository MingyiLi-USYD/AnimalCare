package usyd.mingyi.animalcare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import usyd.mingyi.animalcare.pojo.Comment;
import usyd.mingyi.animalcare.pojo.Post;

import java.util.List;

public interface PostService extends IService<Post> {
    int addPost(Post post);
    int addImage(long imagePostId,String imageUrl);
    List<Post> getAllPosts(long currPage, long pageSize);
    List<Post> getAllPostsOrderByLove(long currPage, long pageSize);
    Post queryPostById(long postId);
    boolean checkLoved(long userId,long postId);
    int love(long userId, long postId);
    int cancelLove(long userId, long postId);
    int deletePost(long postId, long userId);
    int addComment(Comment comment);
    List<Post> getPostByUserId(long userId);
    List<Comment> getCommentsByPostId(long postId);
    List<Post> getPostsByKeywords(String keywords);


}
