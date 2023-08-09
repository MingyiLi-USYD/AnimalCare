package usyd.mingyi.animalcare.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import usyd.mingyi.animalcare.common.R;
import usyd.mingyi.animalcare.dto.CommentDto;
import usyd.mingyi.animalcare.dto.SubcommentDto;
import usyd.mingyi.animalcare.pojo.Comment;
import usyd.mingyi.animalcare.pojo.Subcomment;
import usyd.mingyi.animalcare.service.CommentService;
import usyd.mingyi.animalcare.service.SubcommentService;
import usyd.mingyi.animalcare.utils.BaseContext;

import java.util.List;

@RestController
@Slf4j
public class CommentController {
    @Autowired
    CommentService commentService;
    @Autowired
    SubcommentService subcommentService;

    @PostMapping("/comment/{postId}")
    public R<Comment> addComment(@PathVariable("postId") Long postId, @RequestBody CommentDto comment) {
        Long id = BaseContext.getCurrentId();
        comment.setPostId(postId);
        comment.setCommentTime(System.currentTimeMillis());
        comment.setUserId(id);
        CommentDto commentDto = commentService.saveAndGet(comment);
        return R.success( commentDto);
    }


    @GetMapping("/comments/{postId}")
    @ResponseBody
    public R<IPage<CommentDto>> getCommentsByPostId(@RequestParam("currPage") Long currPage, @RequestParam("pageSize") Integer pageSize,@PathVariable("postId") Long postId) {
        IPage<CommentDto> commentsByPostId = commentService.getCommentsByPostId(currPage,pageSize,postId);
        return R.success(commentsByPostId);
    }

    @GetMapping("/comment/love/{commentId}")
    @ResponseBody
    public R<String> loveComment(@PathVariable("commentId") long commentId) {
        Comment byId = commentService.getById(commentId);
        System.out.println(byId);
        return null;
    }
    @PostMapping("/comment/subcomment/{commentId}")
    @ResponseBody
    public R<Subcomment> addSubcomment(@PathVariable("commentId") Long commentId,@RequestBody SubcommentDto subcommentDto){
        Long id = BaseContext.getCurrentId();
        subcommentDto.setCommentId(commentId);
        subcommentDto.setUserId(id);
        subcommentDto.setSubcommentTime(System.currentTimeMillis());
        return R.success(subcommentService.saveAndGet(subcommentDto));
    }

    @GetMapping("/comment/subcomments/{commentId}")
    @ResponseBody
    public R<List<SubcommentDto>> getSubcommentsByCommentId(@PathVariable("commentId") long commentId){
        List<SubcommentDto> subcommentDtos = subcommentService.getSubcommentDtos(commentId,false);
        return R.success(subcommentDtos);
    }


    @GetMapping("/comments")
    @ResponseBody
    public R<Page<CommentDto>> getAllCommentsToMyPost(@RequestParam("current") Long current,
                                                       @RequestParam("pageSize") Integer pageSize){

        return R.success(commentService.getAllComments(BaseContext.getCurrentId(),current,pageSize));
    }
    @GetMapping("/comment/read/{id}")
    @ResponseBody
    public R<String> markCommentAsRead(@PathVariable("id") Long commentId){
        commentService.markAsRead(commentId);
       return R.success("Success");
    }

    @PostMapping("/comment/reply")
    @ResponseBody
    public R<String> replyComment(@RequestBody SubcommentDto subcommentDto){
        Long id = BaseContext.getCurrentId();
        subcommentDto.setUserId(id);
        subcommentDto.setSubcommentTime(System.currentTimeMillis());
        commentService.saveSubcommentAndMarkAsRead(subcommentDto);
        return R.success("Success");
    }

    @PostMapping("/mention/reply")
    @ResponseBody
    public R<String> replyMention(@RequestBody CommentDto comment,@RequestParam("mentionId") Long mentionId){
        Long id = BaseContext.getCurrentId();
        comment.setCommentTime(System.currentTimeMillis());
        comment.setUserId(id);
        commentService.saveCommentAndMarkAsRead(comment,mentionId);
        return R.success("Success");
    }

}
