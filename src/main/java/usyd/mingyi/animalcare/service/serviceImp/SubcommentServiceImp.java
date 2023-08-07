package usyd.mingyi.animalcare.service.serviceImp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usyd.mingyi.animalcare.dto.SubcommentDto;
import usyd.mingyi.animalcare.mapper.SubcommentMapper;
import usyd.mingyi.animalcare.mapper.UserMapper;
import usyd.mingyi.animalcare.pojo.Subcomment;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.SubcommentService;

import java.util.List;

@Service
public class SubcommentServiceImp extends ServiceImpl<SubcommentMapper, Subcomment> implements SubcommentService {
    @Autowired
    SubcommentMapper subcommentMapper;

    @Autowired
    UserMapper userMapper;

    @Override
    public List<SubcommentDto> getSubcommentDtos(Long commentId, Boolean limit) {
        MPJLambdaWrapper<Subcomment> wrapper = new MPJLambdaWrapper<>();
        wrapper.selectAll(Subcomment.class).selectAssociation(User.class, SubcommentDto::getSubcommentUser)
                .leftJoin(User.class, User::getUserId, Subcomment::getUserId)
                .eq(Subcomment::getCommentId, commentId).last(limit, "LIMIT 3");
        return subcommentMapper.selectJoinList(SubcommentDto.class, wrapper);

    }

    @Override
    public Integer getSubcommentsSize(Long commentId) {
        LambdaQueryWrapper<Subcomment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Subcomment::getCommentId, commentId);
        return subcommentMapper.selectCount(wrapper);

    }

    @Transactional
    public SubcommentDto saveAndGet(SubcommentDto subcommentDto) {
        subcommentMapper.insert(subcommentDto);
        User user = userMapper.selectById(subcommentDto.getUserId());
        subcommentDto.setSubcommentUser(user);
        return subcommentDto;

    }

    @Override
    public void saveSubcomment(SubcommentDto subcommentDto) {
        this.save(subcommentDto);
    }

}
