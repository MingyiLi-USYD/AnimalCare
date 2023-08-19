package usyd.mingyi.animalcare.service.serviceImp;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import usyd.mingyi.animalcare.common.CustomException;
import usyd.mingyi.animalcare.dto.PostDto;
import usyd.mingyi.animalcare.mapper.*;
import usyd.mingyi.animalcare.pojo.LovePost;
import usyd.mingyi.animalcare.pojo.Mention;
import usyd.mingyi.animalcare.pojo.Post;
import usyd.mingyi.animalcare.query.QueryBuilderFactory;
import usyd.mingyi.animalcare.service.FriendshipService;
import usyd.mingyi.animalcare.service.PostService;
import usyd.mingyi.animalcare.service.RealTimeService;
import usyd.mingyi.animalcare.socketEntity.ServiceMessage;

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
    @Autowired
    RedisTemplate<String,Object> redisTemplate;


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
                friendId -> {
                    if (friendshipService.isFriend(postDto.getUserId(), friendId)) {
                        realTimeService.remindFriends(new ServiceMessage(
                                postDto.getUserId(), System.currentTimeMillis(), friendId,
                                MENTION
                        ));
                    }
                }
        );

    }


    @Override
    public IPage<PostDto> getAllPosts(Long currPage, Integer pageSize, Integer order, String keywords) {
        IPage<PostDto> page = new Page<>(currPage, pageSize);
        MPJLambdaWrapper<Post> query = QueryBuilderFactory.createPostQueryBuilder()
                .selectAll()
                .associationUser()
                .order(order)
                .like(keywords)
                .build();
        return postMapper.selectJoinPage(page, PostDto.class, query);

    }


    @Override
    @Cacheable(value = "postCache", key = "#postId")
    public Post getPostById(Long postId) {

        MPJLambdaWrapper<Post> query = QueryBuilderFactory.createPostQueryBuilder()
                .associationUser()
                .collectionImage()
                .eqPostId(postId)
                .build();
        PostDto postDto = postMapper.selectJoinOne(PostDto.class, query);
        if (postDto == null) {
            throw new CustomException("No post found");
        }
        return postDto;
    }

    @Transactional(propagation = Propagation.REQUIRED )
    public Post changeLoveOfPostOptimistic(Long postId, Integer delta) {
        int maxRetries = 3;
        int retries = 0;
        while (retries < maxRetries) {
            Post post = postMapper.selectById(postId);
            if (post == null) {
                throw new CustomException("Not found post");
            }
            // Modify the post data
            post.setLove(post.getLove() + delta);

            int affectedRows = postMapper.updateById(post);

            if (affectedRows > 0) {
                return post; // Update succeeded
            }

            // Update failed, retry after a short sleep
            retries++;
            try {
                Thread.sleep(5); // Sleep for 5 milliseconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupted status
            }
        }

        // Max retries exceeded, throw an exception
        throw new CustomException("Failed to update post after multiple retries");
    }

    @Override
    @Transactional
    @CachePut(value = "postCache", key = "#postId")
    public Post loveAndSyncSocket(Long userId, Long postId) {
        //先给post的赞加1
        Post post = this.changeLoveOfPostOptimistic(postId, 1);
        MPJLambdaWrapper<LovePost> query =
                QueryBuilderFactory.createLovePostQueryBuilder()
                        .eqUserId(userId)
                        .eqPostId(postId)
                        .build();
        //现在是来判断 是否点过赞 先查询
        LovePost exist = lovePostMapper.selectOne(query);
        if (exist == null) {
            LovePost lovePost = new LovePost();
            lovePost.setUserId(userId);
            lovePost.setPostId(postId);
            lovePostMapper.insert(lovePost);
            //证明第一次点赞 socket推送
            realTimeService.remindFriends(
                    new ServiceMessage(userId, System.currentTimeMillis(),
                            post.getUserId(), NEW_LIKE)
            );
        } else {//证明之前对这个post点过赞
            //没有必要再次通知用户了
            exist.setIsCanceled(false);
            lovePostMapper.updateById(exist);
        }
        return post;
    }

    @Override
    @Transactional
    @CachePut(value = "postCache", key = "#postId")
    public Post cancelLove(Long userId, Long postId) {
        MPJLambdaWrapper<LovePost> query =
                QueryBuilderFactory.createLovePostQueryBuilder()
                        .eqUserId(userId)
                        .eqPostId(postId)
                        .build();
        LovePost lovePost = lovePostMapper.selectOne(query);
        if (lovePost == null) {
            throw new CustomException("never love this post before");
        } else {
            lovePost.setIsCanceled(true);
            lovePostMapper.updateById(lovePost);
        }
       return this.changeLoveOfPostOptimistic(postId, -1);

    }

    @Override
    public void deletePost(Long postId, Long userId) {
        MPJLambdaWrapper<Post> query = QueryBuilderFactory.createPostQueryBuilder()
                .eqPostId(postId)
                .eqUserId(userId)
                .build();
/*        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getPostId, postId).eq(Post::getUserId, BaseContext.getCurrentId());*/
        int delete = postMapper.delete(query);
        if (delete == 0) {
            throw new CustomException("Fail to delete this post");
        }
    }


    @Override
    public List<PostDto> getPostByUserId(Long userId) {
        MPJLambdaWrapper<Post> query = QueryBuilderFactory.createPostQueryBuilder()
                .selectAll()
                .collectionImage()
                .associationUser()
                .eqUserId(userId)
                .build();
        return postMapper.selectJoinList(PostDto.class, query);
    }

    @Override
    public List<PostDto> getAllPostsUserLove(Long userId) {
        MPJLambdaWrapper<LovePost> build = QueryBuilderFactory
                .createLovePostQueryBuilder()
                .eqUserId(userId)
                .isCanceled(false)
                .build();
/*        MPJLambdaWrapper<LovePost> lovedPost = new MPJLambdaWrapper<>();
        lovedPost.eq(LovePost::getUserId, userId).eq(LovePost::getIsCanceled, false);*/
        List<LovePost> lovePosts = lovePostMapper.selectList(build);
        if (lovePosts.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> list = lovePosts.stream().map(LovePost::getPostId).toList();
        MPJLambdaWrapper<Post> query = QueryBuilderFactory.
                createPostQueryBuilder()
                .associationUser()
                .in(list)
                .eqUserId(userId)
                .build();
  /*      MPJLambdaWrapper<Post> query = new MPJLambdaWrapper<>();
        query.selectAll(Post.class)
                .in(Post::getPostId, list)
                .eq(Post::getUserId, userId);
        QueryUtils.postWithUser(query);*/
        return postMapper.selectJoinList(PostDto.class, query);
    }

    @Override
    public List<String> getAllLovedPostsIdInString(Long userId) {
        return this.getAllLovedPostsId(userId).stream().map(String::valueOf).toList();
    }

    @Override
    public List<Long> getAllLovedPostsId(Long userId) {
        MPJLambdaWrapper<LovePost> query = QueryBuilderFactory.createLovePostQueryBuilder()
                .eqUserId(userId)
                .isCanceled(false)
                .build();
/*        MPJLambdaWrapper<LovePost> lovedPost = new MPJLambdaWrapper<>();
        lovedPost.eq(LovePost::getUserId, userId)
                .eq(LovePost::getIsCanceled, false);*/
        List<LovePost> lovePosts = lovePostMapper.selectList(query);
        return lovePosts.stream().map(LovePost::getPostId).toList();
    }


}
