package usyd.mingyi.animalcare.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usyd.mingyi.animalcare.mapper.PostMapper;
import usyd.mingyi.animalcare.pojo.Comment;
import usyd.mingyi.animalcare.pojo.Image;
import usyd.mingyi.animalcare.pojo.Post;
import usyd.mingyi.animalcare.pojo.User;

import java.util.List;

@Service
public class PostServiceImp extends ServiceImpl<PostMapper,Post> implements PostService  {
    @Autowired
    PostMapper postMapper;

    @Override
    public int addPost(Post post) {
        return postMapper.addPost(post);
    }

    @Override
    public int addImage(long imagePostId, String imageUrl) {
        return postMapper.addImage(imagePostId, imageUrl);
    }

    @Override
    public List<Post> getAllPosts(long currPage, long pageSize) {
        Page<Post> postPage = new Page<>(currPage,pageSize);
        MPJLambdaWrapper<Post> wrapper = new MPJLambdaWrapper<>();
        //.selectCollection(Image.class,Post::getImageUrlList, map->map.result(Image::getUrl))
        wrapper.selectAll(Post.class).selectCollection(Image.class,Post::getImageUrlList, map->map.result(Image::getUrl))
                .leftJoin(Image.class,Image::getPostId,Post::getPostId);
                //.leftJoin("user As u on po.post_user_id=u.id");

        List<Post> users = postMapper.selectJoinList(Post.class, wrapper);
        System.out.println(users);
        return postMapper.getAllPosts(currPage, pageSize);
    }

    @Override
    public List<Post> getAllPostsOrderByLove(long currPage, long pageSize) {
        return postMapper.getAllPostsOrderByLove(currPage, pageSize);
    }

    @Override
    public Post queryPostById(long postId) {
        return postMapper.queryPostById(postId);
    }

    @Override
    public boolean checkLoved(long userId, long postId) {
        return postMapper.checkLoved(userId, postId);
    }

    @Override
    @Transactional
    public int love(long userId, long postId) {

        if (!checkLoved(userId, postId)) {
            postMapper.lovePlus(postId);
            return postMapper.love(userId, postId);
        } else {
            return 0;
        }
    }

    @Override
    @Transactional
    public int cancelLove(long userId, long postId) {
        if (checkLoved(userId, postId)) {
            postMapper.loveMinus(postId);
            return postMapper.cancelLove(userId, postId);
        } else {
            return 0;
        }
    }

    @Override
    public int deletePost(long postId, long userId) {
        return postMapper.deletePost(postId, userId);
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
