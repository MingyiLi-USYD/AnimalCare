package usyd.mingyi.animalcare.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import usyd.mingyi.animalcare.pojo.Comment;

@Mapper
public interface CommentMapper extends MPJBaseMapper<Comment> {
}
