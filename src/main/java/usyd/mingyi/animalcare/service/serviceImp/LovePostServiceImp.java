package usyd.mingyi.animalcare.service.serviceImp;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usyd.mingyi.animalcare.common.CustomException;
import usyd.mingyi.animalcare.dto.LovePostDto;
import usyd.mingyi.animalcare.mapper.LovePostMapper;
import usyd.mingyi.animalcare.mapper.PostMapper;
import usyd.mingyi.animalcare.pojo.LovePost;
import usyd.mingyi.animalcare.pojo.Post;
import usyd.mingyi.animalcare.query.QueryBuilderFactory;
import usyd.mingyi.animalcare.service.LovePostService;

@Service
public class LovePostServiceImp extends ServiceImpl<LovePostMapper, LovePost> implements LovePostService {
    @Autowired
    LovePostMapper lovePostMapper;

    @Autowired
    PostMapper postMapper;

    @Override
    public Page<LovePostDto> getLovePostsToMe(Long userId, Long current, Integer pageSize) {
        Page<LovePostDto> page = new Page<>(current, pageSize);
        MPJLambdaWrapper<LovePost> query = QueryBuilderFactory.createLovePostQueryBuilder()
                .associationUser()
                .associationPost()
                .isRead(false)
                .neUserId(userId)
                .filterPostByUserId(userId)
                .orderByTimeDes()
                .build();
        return lovePostMapper.selectJoinPage(page, LovePostDto.class, query);
    }


    @Override
    public void markLovePostRead(Long userId, Long lovePostId) {
        //下面看起来有点繁琐 不如一个修改SQL来得方便根据返回的改动行来判断是否修改成功
        //但是下面操作可以细化错误原因 并且都是走索引效率不会有什么差别
        LovePost lovePost = lovePostMapper.selectById(lovePostId);
        if (lovePost == null) {
            throw new CustomException("Never found this lovePost");
        }
        if (lovePost.getIsRead()) {
            throw new CustomException("Already read");
        }
        Post post = postMapper.selectById(lovePost.getPostId());
        if (post == null || !post.getUserId().equals(userId)) {//虽然post基本上不会为空因为存在级联关系 但是还是需要判断以防高并发
            throw new CustomException("No right to access");
        }
        lovePost.setIsRead(true);
        lovePostMapper.updateById(lovePost);
    }

    public Integer countLovePostsReceived(Long userId){
        MPJLambdaWrapper<LovePost> query = QueryBuilderFactory.createLovePostQueryBuilder()
                .associationUser()
                .associationPost()
                .isRead(false)
                .neUserId(userId)
                .filterPostByUserId(userId)
                .build();
        return lovePostMapper.selectCount(query);
    }
}
