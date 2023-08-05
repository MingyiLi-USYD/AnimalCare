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
import usyd.mingyi.animalcare.mapper.UserMapper;
import usyd.mingyi.animalcare.pojo.Comment;
import usyd.mingyi.animalcare.pojo.Post;
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
    UserMapper userMapper;

    @Autowired
    SubcommentService subcommentService;

    @Override
    public IPage<CommentDto> getCommentsByPostId(Long currPage, Integer pageSize,Long postId) {
        IPage<CommentDto> commentDtoIPage = new Page<>(currPage,pageSize);
        MPJLambdaWrapper<Comment> wrapper = new MPJLambdaWrapper<>();
        wrapper.selectAll(Comment.class)
                .selectAssociation(User.class,CommentDto::getCommentUser)
                .leftJoin(User.class,User::getUserId,Comment::getUserId)
                .eq(Comment::getPostId,postId);
        IPage<CommentDto> res = commentMapper.selectJoinPage(commentDtoIPage, CommentDto.class, wrapper);
        res.getRecords().forEach(commentDto -> {
            commentDto.setSubcommentDtos(subcommentService.getSubcommentDtos(commentDto.getCommentId(),true));
            commentDto.setSubcommentsLength(subcommentService.getSubcommentsSize(commentDto.getCommentId()));
        });

         return res;

    }
    public CommentDto saveAndSync(CommentDto commentDto){
        //插入用户的comment
         commentMapper.insert(commentDto);
         //把用户信息同步到这个commentDto中 用户返回给前端
        User user = userMapper.selectById(commentDto.getUserId());
        commentDto.setSubcommentDtos(new ArrayList<>());
        commentDto.setSubcommentsLength(0);
        commentDto.setCommentUser(user);
         return commentDto;
    }

    @Override
    public Page<CommentDto> getAllComments(Long userId,Long current,Integer pageSize) {
        Page<CommentDto> page = new Page<>(current, pageSize);
        MPJLambdaWrapper<Comment> query = new MPJLambdaWrapper<>();
         query.selectAll(Comment.class)
                 .selectAssociation(Post.class,CommentDto::getRelevantPost)
                 .leftJoin(Post.class,on->on.eq(Post::getPostId,Comment::getPostId)
                         .eq(Post::getUserId,userId))
                 .selectAssociation(User.class,CommentDto::getCommentUser)
                 .leftJoin(User.class,User::getUserId,Comment::getUserId)
                 .eq(Post::getUserId,userId).ne(Comment::getUserId,userId);

        return commentMapper.selectJoinPage(page, CommentDto.class, query);

    }


}
