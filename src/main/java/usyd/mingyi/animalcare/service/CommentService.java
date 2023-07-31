package usyd.mingyi.animalcare.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;
import usyd.mingyi.animalcare.dto.CommentDto;
import usyd.mingyi.animalcare.mapper.CommentMapper;
import usyd.mingyi.animalcare.pojo.Comment;

import java.util.List;


public interface CommentService extends IService<Comment> {
    IPage<CommentDto> getCommentsByPostId(Long currPage, Integer pageSize, Long postId);
    CommentDto saveAndSync(Comment comment);
}
