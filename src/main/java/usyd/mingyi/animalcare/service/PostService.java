package usyd.mingyi.animalcare.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import usyd.mingyi.animalcare.dto.LovePostDto;
import usyd.mingyi.animalcare.dto.PostDto;
import usyd.mingyi.animalcare.pojo.Comment;
import usyd.mingyi.animalcare.pojo.Post;

import java.util.List;

public interface PostService extends IService<Post> {
    void addPost(PostDto postDto);
    void addPostAndSyncSocket(PostDto postDto);


    IPage<PostDto> getAllPosts(Long currPage, Integer pageSize, Integer order,String keyword);

    Post getPostById(Long postId);

    void love(Long userId, Long postId);
    void loveAndSyncServer(Long userId, Long postId);

    void cancelLove(Long userId, Long postId);

    void deletePost(Long postId, Long userId);

    List<PostDto> getPostByUserId(Long userId);

    List<PostDto> getAllPostsUserLove(Long userId);

    List<String> getAllLovedPostsIdInString(Long userId);

    List<Long> getAllLovedPostsId(Long userId);




}
