package usyd.mingyi.animalcare.service.serviceImp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.query.MPJQueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usyd.mingyi.animalcare.common.CustomException;
import usyd.mingyi.animalcare.dto.MentionDto;
import usyd.mingyi.animalcare.dto.PostDto;
import usyd.mingyi.animalcare.mapper.MentionMapper;
import usyd.mingyi.animalcare.pojo.Mention;
import usyd.mingyi.animalcare.pojo.Post;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.MentionService;

@Service
public class MentionServiceImp extends ServiceImpl<MentionMapper, Mention> implements MentionService {
    @Autowired
    MentionMapper mentionMapper;
    @Override
    public Page<MentionDto> getAllMentionList(Long userId, Long current, Integer pageSize) {
        Page<MentionDto> page = new Page<>(current, pageSize);
        MPJLambdaWrapper<Mention> query = new MPJLambdaWrapper<>();
        query.selectAll(Mention.class)
                .selectAssociation(Post.class,MentionDto::getRelevantPost)
                .leftJoin(Post.class, Post::getPostId, Mention::getPostId)
                .selectAssociation(User.class, MentionDto::getUserInfo)
                .leftJoin(User.class, User::getUserId, Post::getUserId)
                .eq(Mention::getUserId, userId)
                .eq(Mention::getIsRead, false);
        return mentionMapper.selectJoinPage(page, MentionDto.class, query);
    }

    @Override
    public void markMentionAsRead(Long userId, Long mentionId) {
        Mention mention = this.getById(mentionId);
        if(mention==null){
            throw new CustomException("No mention found");
        }
        if(!mention.getUserId().equals(userId)){
            throw new CustomException("No right to access");
        }
        if(!mention.getIsRead()){
            mention.setIsRead(true);
            this.updateById(mention);
        }

    }

    @Override
    public Integer countMentionsReceived(Long userId) {
        LambdaQueryWrapper<Mention> query = new LambdaQueryWrapper<>();
       return mentionMapper
               .selectCount(query.eq(Mention::getUserId,userId));
    }
}
