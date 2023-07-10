package usyd.mingyi.animalcare.service.serviceImp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usyd.mingyi.animalcare.dto.SubcommentDto;
import usyd.mingyi.animalcare.mapper.SubcommentMapper;
import usyd.mingyi.animalcare.pojo.Subcomment;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.SubcommentService;

import java.util.List;

@Service
public class SubcommentServiceImp extends ServiceImpl<SubcommentMapper, Subcomment> implements SubcommentService {
    @Autowired
    SubcommentMapper subcommentMapper;
    @Override
    public List<SubcommentDto> getSubcommentDtos(Long commentId,Boolean limit) {
        MPJLambdaWrapper<Subcomment> wrapper = new MPJLambdaWrapper<>();
        wrapper.selectAll(Subcomment.class).selectAs(User::getNickname, SubcommentDto::getNickName)
                .selectAs(User::getAvatar,SubcommentDto::getUserAvatar)
                .leftJoin(User.class,User::getId, Subcomment::getUserId)
                .eq(Subcomment::getCommentId,commentId).last(limit,"LIMIT 3");
        return subcommentMapper.selectJoinList(SubcommentDto.class, wrapper);
    }

    @Override
    public Integer getSubcommentsSize(Long commentId) {
        LambdaQueryWrapper<Subcomment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Subcomment::getCommentId,commentId);
       return subcommentMapper.selectCount(wrapper);

    }

    public SubcommentDto saveAndSync(Subcomment subcomment){
        subcommentMapper.insert(subcomment);
        MPJLambdaWrapper<Subcomment> wrapper = new MPJLambdaWrapper<>();
        wrapper.selectAll(Subcomment.class).selectAs(User::getNickname, SubcommentDto::getNickName)
                .selectAs(User::getAvatar,SubcommentDto::getUserAvatar)
                .leftJoin(User.class,User::getId, Subcomment::getUserId)
                .eq(Subcomment::getSubcommentId,subcomment.getSubcommentId());
       return subcommentMapper.selectJoinOne(SubcommentDto.class,wrapper);
    }


}
