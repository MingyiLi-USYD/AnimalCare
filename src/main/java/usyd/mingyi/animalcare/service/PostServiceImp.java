package usyd.mingyi.animalcare.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import usyd.mingyi.animalcare.common.CustomException;
import usyd.mingyi.animalcare.common.R;
import usyd.mingyi.animalcare.dto.PostDto;
import usyd.mingyi.animalcare.mapper.ImageMapper;
import usyd.mingyi.animalcare.mapper.PostMapper;
import usyd.mingyi.animalcare.mapper.UserMapper;
import usyd.mingyi.animalcare.pojo.Comment;
import usyd.mingyi.animalcare.pojo.Post;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.utils.BaseContext;
import usyd.mingyi.animalcare.utils.ImageUtil;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;

@Service
public class PostServiceImp extends ServiceImpl<PostMapper,Post> implements PostService  {
    @Autowired
    PostMapper postMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    ImageMapper imageMapper;
    @Autowired
    ObjectMapper mapper;
    @Override
    public void addPost(Post post, String userName, MultipartFile[] images) {
        String imagesString= ImageUtil.saveBatchToLocal(userName, images, mapper);
        post.setImages(imagesString);
        postMapper.insert(post);

    }


    @Override
    public  IPage<PostDto> getAllPosts(long currPage, long pageSize) {
        IPage<PostDto> postDtoPage = new Page<>(currPage,pageSize);
        MPJLambdaWrapper<Post> wrapper = new MPJLambdaWrapper<>();
        wrapper.selectAll(Post.class)
                .selectAs(User::getAvatar,PostDto::getUserAvatar)
                .selectAs(User::getNickName,PostDto::getNickName)
               .leftJoin(User.class,User::getId,Post::getUserId)
                .orderByAsc(Post::getPostId);
        return postMapper.selectJoinPage(postDtoPage, PostDto.class, wrapper);

    }



    @Override
    public Post queryPostById(long postId) {

        MPJLambdaWrapper<Post> wrapper = new MPJLambdaWrapper<>();
         wrapper.selectAll(Post.class).selectAs(User::getAvatar,PostDto::getUserAvatar)
                 .selectAs(User::getNickName,PostDto::getNickName)
                 .leftJoin(User.class,User::getId,Post::getUserId).eq(PostDto::getPostId,postId);
        return postMapper.selectJoinOne(PostDto.class,wrapper);
    }

    @Override
    public boolean checkLoved(long userId, long postId) {
        return postMapper.checkLoved(userId, postId);
    }

    @Override
    @Transactional
    public void love(long userId, long postId) {
        LambdaUpdateWrapper<Post> wrapper = new LambdaUpdateWrapper<>();
        wrapper.setSql("love=love+1").eq(Post::getPostId,postId);
        postMapper.update(null,wrapper);
        User user = userMapper.selectById(userId);
        String loveList = user.getLoveList();

        try {
            HashSet<Long> hashSet = mapper.readValue(loveList==null?"[]":loveList, new TypeReference<HashSet<Long>>(){});
            hashSet.add(postId);
            loveList = mapper.writeValueAsString(hashSet);
            user.setLoveList(loveList);

            userMapper.update(null,new LambdaUpdateWrapper<User>()
                    .set(User::getLoveList,loveList).eq(User::getId,userId));
        } catch (JsonProcessingException e) {
            throw new CustomException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void cancelLove(long userId, long postId) {
        LambdaUpdateWrapper<Post> wrapper = new LambdaUpdateWrapper<>();
        wrapper.setSql("love=love-1").eq(Post::getPostId,postId);
        postMapper.update(null,wrapper);
        User user = userMapper.selectById(userId);
        String loveList = user.getLoveList();

        try {
            HashSet<Long> hashSet = mapper.readValue(loveList==null?"[]":loveList, new TypeReference<HashSet<Long>>(){});
            hashSet.remove(postId);
            loveList = mapper.writeValueAsString(hashSet);
            user.setLoveList(loveList);
            userMapper.update(null,new LambdaUpdateWrapper<User>()
                    .set(User::getLoveList,loveList).eq(User::getId,userId));
        } catch (JsonProcessingException e) {
            throw new CustomException(e.getMessage());
        }
    }

    @Override
    public int deletePost(long postId, long userId) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getPostId,postId).eq(Post::getUserId, BaseContext.getCurrentId());
        return postMapper.delete(wrapper);
    }

    @Override
    public int addComment(Comment comment) {
        return postMapper.addComment(comment);
    }

    @Override
    public List<Post> getPostByUserId(long userId) {
        return postMapper.getPostsByUserId(userId);
    }

    @Override
    public List<Comment> getCommentsByPostId(long postId) {
        return postMapper.getCommentsByPostId(postId);
    }

    @Override
    public List<Post> getPostsByKeywords(String keywords) {
        return postMapper.getPostsByKeywords(keywords);
    }

}
