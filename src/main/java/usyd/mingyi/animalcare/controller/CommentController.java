package usyd.mingyi.animalcare.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
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
    public R<Comment> addComment(@PathVariable("postId") long postId, @RequestBody Comment comment) {
        Long id = BaseContext.getCurrentId();
        comment.setPostId(postId);
        comment.setCommentTime(System.currentTimeMillis());
        comment.setUserId(id);
        //commentService.save(comment);
        CommentDto commentDto = commentService.saveAndSync(comment);
        System.out.println(commentDto);
        return R.success( commentDto);
    }


    @GetMapping("/comments/{postId}")
    @ResponseBody
    public R<IPage<CommentDto>> getCommentsByPostId(@RequestParam("currPage") long currPage, @RequestParam("pageSize") long pageSize,@PathVariable("postId") long postId) {
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
    @GetMapping("/test/subcomment/{commentId}")
    public R<List<SubcommentDto>> test(@PathVariable("commentId") long commentId){
        List<SubcommentDto> subcommentDtos = subcommentService.getSubcommentDtos(commentId,false);
        System.out.println(subcommentService.getSubcommentsSize(commentId));
        return R.success(subcommentDtos);
    }
    @PostMapping("/comment/subcomment/{commentId}")
    @ResponseBody
    public R<Subcomment> addSubcomment(@PathVariable("commentId") long commentId,@RequestBody Subcomment subcomment){
        System.out.println(subcomment);
        Long id = BaseContext.getCurrentId();
        subcomment.setCommentId(commentId);
        subcomment.setUserId(id);
        subcomment.setSubcommentTime(System.currentTimeMillis());
       // subcommentService.save(subcomment);
        return R.success(subcommentService.saveAndSync(subcomment));
    }

    @GetMapping("/comment/subcomments/{commentId}")
    @ResponseBody
    public R<List<SubcommentDto>> getSubcommentsByCommentId(@PathVariable("commentId") long commentId){
        List<SubcommentDto> subcommentDtos = subcommentService.getSubcommentDtos(commentId,false);
        return R.success(subcommentDtos);
    }

}
