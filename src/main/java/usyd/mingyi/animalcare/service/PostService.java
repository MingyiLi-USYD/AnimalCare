package usyd.mingyi.animalcare.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import usyd.mingyi.animalcare.dto.PostDto;
import usyd.mingyi.animalcare.pojo.Comment;
import usyd.mingyi.animalcare.pojo.Post;

import java.util.List;

public interface PostService extends IService<Post> {
    void addPost(Post post,String userName, MultipartFile[] images);
    IPage<PostDto> getAllPosts(long currPage, long pageSize);
    Post queryPostById(long postId);
    boolean checkLoved(long userId,long postId);
    void love(long userId, long postId);
    void cancelLove(long userId, long postId);
    int deletePost(long postId, long userId);
    int addComment(Comment comment);
    List<Post> getPostByUserId(long userId);
    List<Comment> getCommentsByPostId(long postId);
    List<Post> getPostsByKeywords(String keywords);


}
