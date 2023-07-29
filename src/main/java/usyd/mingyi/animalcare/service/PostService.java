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
    IPage<PostDto> getAllPosts(Long currPage, Integer pageSize,Integer order);
    Post getPostById(Long postId,Long currentUserId);
    void love(Long userId, Long postId);
    void cancelLove(Long userId, Long postId);
    void deletePost(Long postId, Long userId);
    List<PostDto> getPostByUserId(Long userId);
    List<Post> getPostsByKeywords(String keywords);


}
