package usyd.mingyi.animalcare.service.serviceImp;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usyd.mingyi.animalcare.dto.CommentDto;
import usyd.mingyi.animalcare.mapper.CommentMapper;
import usyd.mingyi.animalcare.pojo.Comment;
import usyd.mingyi.animalcare.pojo.Subcomment;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.CommentService;
import usyd.mingyi.animalcare.service.SubcommentService;

import java.util.ArrayList;

@Service
@Slf4j
public class CommentServiceImp extends ServiceImpl<CommentMapper, Comment>implements CommentService {
    @Autowired
    CommentMapper commentMapper;

    @Autowired
    SubcommentService subcommentService;

    @Override
    public IPage<CommentDto> getCommentsByPostId(long currPage, long pageSize,long postId) {
        log.info("进来了");
        IPage<CommentDto> commentDtoIPage = new Page<>(currPage,pageSize);
        MPJLambdaWrapper<Comment> wrapper = new MPJLambdaWrapper<>();
        wrapper.selectAll(Comment.class)
                .selectAs(User::getNickname,CommentDto::getNickName)
                .selectAs(User::getAvatar,CommentDto::getUserAvatar)
                //.selectCollection(Subcomment.class,CommentDto::getSubcomments)
                .leftJoin(User.class,User::getId,Comment::getUserId)
                //.leftJoin(Subcomment.class,Subcomment::getSubcommentId, Comment::getId)
                .eq(Comment::getPostId,postId);

        IPage<CommentDto> res = commentMapper.selectJoinPage(commentDtoIPage, CommentDto.class, wrapper);
         res.getRecords().forEach(item->{
             item.setSubcommentDtos(subcommentService.getSubcommentDtos(item.getId(),true));
             item.setSubcommentsLength(subcommentService.getSubcommentsSize(item.getId()));
         });
         return res;

    }
    public CommentDto saveAndSync(Comment comment){
         commentMapper.insert(comment);

        MPJLambdaWrapper<Comment> wrapper = new MPJLambdaWrapper<>();
        wrapper.selectAll(Comment.class)
                .selectAs(User::getNickname,CommentDto::getNickName)
                .selectAs(User::getAvatar,CommentDto::getUserAvatar)
                .leftJoin(User.class,User::getId,Comment::getUserId)
                .eq(Comment::getId,comment.getId());
        CommentDto commentDto = commentMapper.selectJoinOne(CommentDto.class, wrapper);
        commentDto.setSubcommentDtos(new ArrayList<>());
        commentDto.setSubcommentsLength(0);
        return commentDto;
    }
}
