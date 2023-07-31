package usyd.mingyi.animalcare.service.serviceImp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yulichang.query.MPJLambdaQueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usyd.mingyi.animalcare.common.CustomException;
import usyd.mingyi.animalcare.dto.PostDto;
import usyd.mingyi.animalcare.mapper.LovePostMapper;
import usyd.mingyi.animalcare.mapper.PostImageMapper;
import usyd.mingyi.animalcare.mapper.PostMapper;
import usyd.mingyi.animalcare.mapper.UserMapper;
import usyd.mingyi.animalcare.pojo.LovePost;
import usyd.mingyi.animalcare.pojo.Post;
import usyd.mingyi.animalcare.pojo.PostImage;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.PostService;
import usyd.mingyi.animalcare.utils.BaseContext;
import usyd.mingyi.animalcare.utils.JWTUtils;
import usyd.mingyi.animalcare.utils.QueryUtils;


import java.util.HashSet;
import java.util.List;

@Service
public class PostServiceImp extends ServiceImpl<PostMapper,Post> implements PostService {
    @Autowired
    PostMapper postMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    PostImageMapper imageMapper;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    LovePostMapper lovePostMapper;
    @Override
    public void addPost(Post post) {
        postMapper.insert(post);
        //然后通知需要分享的人和关注的人
    }


    @Override
    public  IPage<PostDto> getAllPosts(Long currPage, Integer pageSize,Integer order) {
        IPage<PostDto> page = new Page<>(currPage,pageSize);
        MPJLambdaWrapper<Post> query = new MPJLambdaWrapper<>();
        query.selectAll(Post.class).eq(Post::getVisible,true);
       // QueryUtils.postWithPostImages(query);
        QueryUtils.postWithUser(query);
        return postMapper.selectJoinPage(page, PostDto.class, query);

    }

    @Override
    public Post getPostById(Long postId, Long currentUserId) {
        MPJLambdaWrapper<Post> query = new MPJLambdaWrapper<>();
        query.selectAll(Post.class)
                .eq(Post::getPostId,postId);
        QueryUtils.postWithPostImages(query);
        QueryUtils.postWithUser(query);
       return postMapper.selectJoinOne(PostDto.class, query);

    }


    @Override
    @Transactional
    public void love(Long userId, Long postId) {
        LovePost lovePost = new LovePost();
        lovePost.setUserId(userId);
        lovePost.setPostId(postId);
        lovePostMapper.insert(lovePost);
    }

    @Override
    @Transactional
    public void cancelLove(Long userId, Long postId) {
        LambdaQueryWrapper<LovePost> query = new LambdaQueryWrapper<>();
        query.eq(LovePost::getUserId,userId).eq(LovePost::getPostId,postId);

        int delete = lovePostMapper.delete(query);
        if(delete==0){
            throw new CustomException("never love this post before");
        }
    }

    @Override
    public void deletePost(Long postId, Long userId) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getPostId,postId).eq(Post::getUserId, BaseContext.getCurrentId());
        int delete = postMapper.delete(wrapper);
        if(delete==0){
            throw new CustomException("Fail to delete this post");
        }
    }


    @Override
    public List<PostDto> getPostByUserId(Long userId) {
        MPJLambdaWrapper<Post> query = new MPJLambdaWrapper<>();
        query.selectAll(Post.class)
                .eq(Post::getUserId,userId);
        QueryUtils.postWithUser(query);
        QueryUtils.postWithPostImages(query);
        return postMapper.selectJoinList(PostDto.class, query);

    }



    @Override
    public List<Post> getPostsByKeywords(String keywords) {
        return postMapper.getPostsByKeywords(keywords);
    }

    @Override
    public List<PostDto> getAllLovedPost(Long userId) {
        MPJLambdaWrapper<LovePost> lovedPost = new MPJLambdaWrapper<>();
        lovedPost.eq(LovePost::getUserId,userId);

        List<LovePost> lovePosts = lovePostMapper.selectList(lovedPost);
        List<Long> list = lovePosts.stream().map(LovePost::getPostId).toList();
        MPJLambdaWrapper<Post> query = new MPJLambdaWrapper<>();
        query.selectAll(Post.class)
                .in(Post::getPostId,list)
                .eq(Post::getUserId,userId);
        QueryUtils.postWithUser(query);
        QueryUtils.postWithPostImages(query);
        return postMapper.selectJoinList(PostDto.class, query);
    }
    @Override
    public List<Long> getAllLovedPostsId(Long userId) {
        MPJLambdaWrapper<LovePost> lovedPost = new MPJLambdaWrapper<>();
        lovedPost.eq(LovePost::getUserId,userId);
        List<LovePost> lovePosts = lovePostMapper.selectList(lovedPost);

        return lovePosts.stream().map(LovePost::getPostId).toList();

    }
}
