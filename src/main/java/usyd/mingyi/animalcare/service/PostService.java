package usyd.mingyi.animalcare.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import usyd.mingyi.animalcare.dto.PostDto;
import usyd.mingyi.animalcare.pojo.Comment;
import usyd.mingyi.animalcare.pojo.Post;

import java.util.List;

public interface PostService extends IService<Post> {
    void addPost(Post post);
    IPage<PostDto> getAllPosts(long currPage, long pageSize,int order);
    Post queryPostById(long postId,long currentUserId);
    void love(long userId, long postId);
    void cancelLove(long userId, long postId);
    int deletePost(long postId, long userId);
    List<Post> getPostByUserId(long userId);
    List<Post> getPostsByKeywords(String keywords);


}
