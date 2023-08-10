package usyd.mingyi.animalcare.service.serviceImp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usyd.mingyi.animalcare.common.CustomException;
import usyd.mingyi.animalcare.dto.PostDto;
import usyd.mingyi.animalcare.mapper.*;
import usyd.mingyi.animalcare.pojo.LovePost;
import usyd.mingyi.animalcare.pojo.Mention;
import usyd.mingyi.animalcare.pojo.Post;
import usyd.mingyi.animalcare.service.FriendshipService;
import usyd.mingyi.animalcare.service.PostService;
import usyd.mingyi.animalcare.service.RealTimeService;
import usyd.mingyi.animalcare.socketEntity.ServiceMessage;
import usyd.mingyi.animalcare.utils.BaseContext;
import usyd.mingyi.animalcare.utils.QueryUtils;

import java.util.ArrayList;
import java.util.List;

import static usyd.mingyi.animalcare.socketEntity.ServiceMessageType.MENTION;
import static usyd.mingyi.animalcare.socketEntity.ServiceMessageType.NEW_LIKE;

@Service
public class PostServiceImp extends ServiceImpl<PostMapper, Post> implements PostService {
    @Autowired
    PostMapper postMapper;
    @Autowired
    UserMapper userMapper;

    @Autowired
    MentionMapper mentionMapper;

    @Autowired
    PostImageMapper postImageMapper;
    @Autowired
    LovePostMapper lovePostMapper;

    @Autowired
    RealTimeService realTimeService;

    @Autowired
    FriendshipService friendshipService;


    @Override
    @Transactional
    public void addPost(PostDto postDto) {

        postMapper.insert(postDto);
        postDto.getImages().forEach(postImage -> {
            postImage.setPostId(postDto.getPostId());
            postImageMapper.insert(postImage);
        });
        //然后通知需要分享的人和关注的人
        List<Long> referFriends = postDto.getReferFriends();
        referFriends.forEach(id -> {
            mentionMapper.insert(new Mention(postDto.getPostId(), id));
        });
    }

    @Override
    @Transactional
    public void addPostAndSyncSocket(PostDto postDto) {
        this.addPost(postDto);
        postDto.getReferFriends().forEach(
                friendId->{
                    if (friendshipService.isFriend(postDto.getUserId(),friendId)) {
                        realTimeService.remindFriends(new ServiceMessage(
                                postDto.getUserId(),System.currentTimeMillis(),friendId,
                                MENTION
                        ));
                    }
                }
        );

    }


    @Override
    public IPage<PostDto> getAllPosts(Long currPage, Integer pageSize, Integer order, String keyword) {
        IPage<PostDto> page = new Page<>(currPage, pageSize);
        MPJLambdaWrapper<Post> query = new MPJLambdaWrapper<>();
        query.selectAll(Post.class).eq(Post::getVisible, true)
                .orderByDesc(order == 1, Post::getPostTime)
                .orderByDesc(order == 2, Post::getLove)
                .like(keyword != null && !keyword.isEmpty(), Post::getPostContent, keyword);
        QueryUtils.postWithUser(query);
        return postMapper.selectJoinPage(page, PostDto.class, query);

    }


    @Override
    @Cacheable(value = "postCache", key = "#postId")
    public Post getPostById(Long postId) {

        MPJLambdaWrapper<Post> query = new MPJLambdaWrapper<>();
        query.selectAll(Post.class)
                .eq(Post::getPostId, postId);
        QueryUtils.postWithPostImages(query);
        QueryUtils.postWithUser(query);
        PostDto postDto = postMapper.selectJoinOne(PostDto.class, query);
        if (postDto == null) {
            throw new CustomException("No post found");
        }
        return postDto;
    }


    @Override
    @Transactional
    public void love(Long userId, Long postId) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new CustomException("Not found post");
        }
        post.setLove(post.getLove() + 1);
        postMapper.updateById(post);
        MPJLambdaWrapper<LovePost> query = new MPJLambdaWrapper<>();
        query.selectAll(LovePost.class)
                .eq(LovePost::getPostId, postId)
                .eq(LovePost::getUserId, userId);
        LovePost exist = lovePostMapper.selectOne(query);
        if (exist == null) {
            LovePost lovePost = new LovePost();
            lovePost.setUserId(userId);
            lovePost.setPostId(postId);
            lovePostMapper.insert(lovePost);
        } else {//证明之前对这个post点过赞
            //没有必要再次通知用户了
            exist.setIsCanceled(false);
            lovePostMapper.updateById(exist);
        }

    }

    @Override
    @Transactional
    public void loveAndSyncServer(Long userId, Long postId) {
        this.love(userId, postId);
        Post post = postMapper.selectById(postId);
        realTimeService.remindFriends(
                new ServiceMessage(userId, System.currentTimeMillis(),
                        post.getUserId(), NEW_LIKE)
        );
    }

    @Override
    @Transactional
    public void cancelLove(Long userId, Long postId) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new CustomException("Not found post");
        }
        LambdaQueryWrapper<LovePost> query = new LambdaQueryWrapper<>();
        query.eq(LovePost::getUserId, userId).eq(LovePost::getPostId, postId);
        LovePost lovePost = lovePostMapper.selectOne(query);
        if (lovePost == null) {
            throw new CustomException("never love this post before");
        } else {
            lovePost.setIsCanceled(true);
            lovePostMapper.updateById(lovePost);
        }
        post.setLove(post.getLove() - 1);
        postMapper.updateById(post);

    }

    @Override
    public void deletePost(Long postId, Long userId) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getPostId, postId).eq(Post::getUserId, BaseContext.getCurrentId());
        int delete = postMapper.delete(wrapper);
        if (delete == 0) {
            throw new CustomException("Fail to delete this post");
        }
    }


    @Override
    public List<PostDto> getPostByUserId(Long userId) {
        MPJLambdaWrapper<Post> query = new MPJLambdaWrapper<>();
        query.selectAll(Post.class)
                .eq(Post::getUserId, userId);
        QueryUtils.postWithUser(query);
        QueryUtils.postWithPostImages(query);
        return postMapper.selectJoinList(PostDto.class, query);
    }

    @Override
    public List<PostDto> getAllPostsUserLove(Long userId) {
        MPJLambdaWrapper<LovePost> lovedPost = new MPJLambdaWrapper<>();
        lovedPost.eq(LovePost::getUserId, userId).eq(LovePost::getIsCanceled, false);
        List<LovePost> lovePosts = lovePostMapper.selectList(lovedPost);
        if (lovePosts.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> list = lovePosts.stream().map(LovePost::getPostId).toList();
        MPJLambdaWrapper<Post> query = new MPJLambdaWrapper<>();
        query.selectAll(Post.class)
                .in(Post::getPostId, list)
                .eq(Post::getUserId, userId);
        QueryUtils.postWithUser(query);
        return postMapper.selectJoinList(PostDto.class, query);
    }

    @Override
    public List<String> getAllLovedPostsIdInString(Long userId) {
        return getAllLovedPostsId(userId).stream().map(String::valueOf).toList();
    }

    @Override
    public List<Long> getAllLovedPostsId(Long userId) {
        MPJLambdaWrapper<LovePost> lovedPost = new MPJLambdaWrapper<>();
        lovedPost.eq(LovePost::getUserId, userId)
                .eq(LovePost::getIsCanceled, false);
        List<LovePost> lovePosts = lovePostMapper.selectList(lovedPost);
        return lovePosts.stream().map(LovePost::getPostId).toList();
    }


}
