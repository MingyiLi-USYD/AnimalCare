package usyd.mingyi.animalcare.service.serviceImp;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usyd.mingyi.animalcare.dto.LovePostDto;
import usyd.mingyi.animalcare.mapper.LovePostMapper;
import usyd.mingyi.animalcare.pojo.LovePost;
import usyd.mingyi.animalcare.pojo.Post;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.LovePostService;

@Service
public class LovePostServiceImp extends ServiceImpl<LovePostMapper, LovePost> implements LovePostService {
    @Autowired
    LovePostMapper lovePostMapper;
    @Override
    public Page<LovePostDto> getLovePostsToMe(Long userId, Long current, Integer pageSize) {
        Page<LovePostDto> page = new Page<>(current, pageSize);
        MPJLambdaWrapper<LovePost> query = new MPJLambdaWrapper<>();
        query.selectAll(LovePost.class)
                .selectAssociation(User.class, LovePostDto::getUserInfo)
                .leftJoin(User.class, User::getUserId, LovePost::getUserId)
                .selectAssociation(Post.class, LovePostDto::getRelevantPost)
                .leftJoin(Post.class, Post::getPostId, LovePost::getPostId)
                .eq(LovePost::getIsRead, false)   //post 首先必须是未读的
                .eq(Post::getUserId, userId)          //post 必须是我的才行 不能查到别人的
                .ne(LovePost::getUserId, userId);     //虽然可以自己给自己点赞 但是我自己的查看的时候得过滤掉
        return lovePostMapper.selectJoinPage(page, LovePostDto.class, query);
    }
}
