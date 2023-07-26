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
import usyd.mingyi.animalcare.service.PostService;
import usyd.mingyi.animalcare.utils.BaseContext;
import usyd.mingyi.animalcare.utils.ImageUtil;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImp extends ServiceImpl<PostMapper,Post> implements PostService {
    @Autowired
    PostMapper postMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    ImageMapper imageMapper;
    @Autowired
    ObjectMapper mapper;
    @Override
    public void addPost(Post post) {
        postMapper.insert(post);
        //然后通知需要分享的人和关注的人
    }


    @Override
    public  IPage<PostDto> getAllPosts(long currPage, long pageSize,int order) {

        IPage<PostDto> postDtoPage = new Page<>(currPage,pageSize);
        MPJLambdaWrapper<Post> wrapper = new MPJLambdaWrapper<>();
        wrapper.selectAll(Post.class)
                //.selectAs(" COUNT(comment.comment_id)",PostDto::getCommentList)
                .selectAs(User::getAvatar,PostDto::getUserAvatar)
                .selectAs(User::getNickname,PostDto::getNickName)
                .leftJoin(User.class,User::getId,Post::getUserId)
                .eq(Post::isVisible,true);
        if(order==1){
            wrapper.orderByDesc(Post::getPostTime);
        }else {
            wrapper.orderByDesc(Post::getLove);
        }
        IPage<PostDto> postDtoIPage = postMapper.selectJoinPage(postDtoPage, PostDto.class, wrapper);
 /*       List<PostDto> records = postDtoIPage.getRecords();
        List<PostDto> newRecords = records.stream().map(item -> {
            item.setCommentCount(5L);
            return item;
        }).collect(Collectors.toList());
        postDtoIPage.setRecords(newRecords);*/
        return postDtoIPage;


    }



    @Override
    public Post queryPostById(long postId,long currentUserId) {

        MPJLambdaWrapper<Post> wrapper = new MPJLambdaWrapper<>();
        wrapper.selectAll(Post.class).selectAs(User::getAvatar,PostDto::getUserAvatar)
                .selectAs(User::getNickname,PostDto::getNickName)
                .leftJoin(User.class,User::getId,Post::getUserId).eq(PostDto::getPostId,postId);
        PostDto postDto = postMapper.selectJoinOne(PostDto.class, wrapper);
        if(postDto.isVisible()){
            return postDto;
        }else if(postDto.getUserId()==currentUserId){
            return postDto;
        }else {
            throw new CustomException("No right to access");
        }
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
            //线程不安全
            HashSet<String> hashSet = mapper.readValue(loveList==null?"[]":loveList, new TypeReference<HashSet<String>>(){});
            if(hashSet.contains(String.valueOf(postId))){
                throw new CustomException("already loved");
            }
            hashSet.add(String.valueOf(postId));
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
            HashSet<String> hashSet = mapper.readValue(loveList==null?"[]":loveList, new TypeReference<HashSet<String>>(){});
            if(!hashSet.contains(String.valueOf(postId))){
                throw new CustomException("did not love before");
            }
            hashSet.remove(String.valueOf(postId));
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
    public List<Post> getPostByUserId(long userId) {
        MPJLambdaWrapper<Post> wrapper = new MPJLambdaWrapper<>();
        wrapper.selectAll(Post.class).eq(Post::getUserId,userId);
        return postMapper.selectList(wrapper);
    }



    @Override
    public List<Post> getPostsByKeywords(String keywords) {
        return postMapper.getPostsByKeywords(keywords);
    }

}
