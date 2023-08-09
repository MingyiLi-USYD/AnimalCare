package usyd.mingyi.animalcare.service.serviceImp;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usyd.mingyi.animalcare.common.CustomException;
import usyd.mingyi.animalcare.dto.CommentDto;
import usyd.mingyi.animalcare.dto.SubcommentDto;
import usyd.mingyi.animalcare.mapper.CommentMapper;
import usyd.mingyi.animalcare.mapper.PostMapper;
import usyd.mingyi.animalcare.mapper.UserMapper;
import usyd.mingyi.animalcare.pojo.Comment;
import usyd.mingyi.animalcare.pojo.Post;
import usyd.mingyi.animalcare.pojo.Subcomment;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.service.CommentService;
import usyd.mingyi.animalcare.service.MentionService;
import usyd.mingyi.animalcare.service.RealTimeService;
import usyd.mingyi.animalcare.service.SubcommentService;
import usyd.mingyi.animalcare.utils.BaseContext;

import java.util.ArrayList;

@Service
@Slf4j
public class CommentServiceImp extends ServiceImpl<CommentMapper, Comment>implements CommentService {
    @Autowired
    CommentMapper commentMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    PostMapper postMapper;

    @Autowired
    SubcommentService subcommentService;

    @Autowired
    MentionService mentionService;

    @Autowired
    RealTimeService realTimeService;

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
    public CommentDto saveAndGet(CommentDto commentDto){
        //插入用户的comment
         commentMapper.insert(commentDto);
         //把用户信息同步到这个commentDto中 用户返回给前端
        User user = userMapper.selectById(commentDto.getUserId());
        commentDto.setSubcommentDtos(new ArrayList<>());
        commentDto.setSubcommentsLength(0);
        commentDto.setCommentUser(user);
        //realTimeService.remindFriends();
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

    @Override
    public void markAsRead(Long commentId) {
        Comment comment = getById(commentId);
        if(comment==null){
            throw new CustomException("No comment found");
        }
        Post post = postMapper.selectById(comment.getPostId());
        if(post!=null&&post.getUserId().equals(BaseContext.getCurrentId())){
             comment.setIsRead(true);
             commentMapper.updateById(comment);
        }
    }

    @Override
    @Transactional
    public void saveSubcommentAndMarkAsRead(SubcommentDto subcommentDto) {
        subcommentService.saveSubcomment(subcommentDto);
        this.markAsRead(subcommentDto.getCommentId());
    }

    @Override
    public void saveCommentAndMarkAsRead(Comment comment,Long mentionId) {
        this.save(comment);
        mentionService.markMentionAsRead(BaseContext.getCurrentId(),mentionId);
    }
}
