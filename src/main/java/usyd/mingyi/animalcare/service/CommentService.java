package usyd.mingyi.animalcare.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;
import usyd.mingyi.animalcare.dto.CommentDto;
import usyd.mingyi.animalcare.dto.SubcommentDto;
import usyd.mingyi.animalcare.mapper.CommentMapper;
import usyd.mingyi.animalcare.pojo.Comment;
import usyd.mingyi.animalcare.pojo.Subcomment;

import java.util.List;


public interface CommentService extends IService<Comment> {
    IPage<CommentDto> getCommentsByPostId(Long currPage, Integer pageSize, Long postId);
    CommentDto saveAndGet(CommentDto comment);

    Page<CommentDto> getAllComments(Long userId,Long current,Integer pageSize);

    void markAsRead(Long commentId);

    void saveSubcommentAndMarkAsRead(SubcommentDto subcomment);
    void saveCommentAndMarkAsRead(Comment comment,Long mentionId);

}
