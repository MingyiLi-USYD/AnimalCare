package usyd.mingyi.animalcare.service.serviceImp;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usyd.mingyi.animalcare.dto.CommentDto;
import usyd.mingyi.animalcare.dto.PostDto;
import usyd.mingyi.animalcare.mapper.CommentMapper;
import usyd.mingyi.animalcare.pojo.Comment;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.CommentService;

import java.util.List;

@Service
@Slf4j
public class CommentServiceImp extends ServiceImpl<CommentMapper, Comment>implements CommentService {
    @Autowired
    CommentMapper commentMapper;

    @Override
    public IPage<CommentDto> getCommentsByPostId(long currPage, long pageSize,long postId) {
        //
        //
        log.info("进来了");
        IPage<CommentDto> commentDtoIPage = new Page<>(currPage,pageSize);
        MPJLambdaWrapper<Comment> wrapper = new MPJLambdaWrapper<>();
        wrapper.selectAll(Comment.class)
                .selectAs(User::getNickname,CommentDto::getNickName)
                .selectAs(User::getAvatar,CommentDto::getUserAvatar)
                .leftJoin(User.class,User::getId,Comment::getUserId)
                .eq(Comment::getPostId,postId);

        //List<CommentDto> commentDtoList = commentMapper.selectJoinList(CommentDto.class, wrapper);

        return commentMapper.selectJoinPage(commentDtoIPage, CommentDto.class, wrapper);

    }
}
