package usyd.mingyi.animalcare.controller;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import usyd.mingyi.animalcare.common.R;
import usyd.mingyi.animalcare.dto.CommentDto;
import usyd.mingyi.animalcare.dto.SubcommentDto;
import usyd.mingyi.animalcare.pojo.Comment;
import usyd.mingyi.animalcare.service.CommentService;
import usyd.mingyi.animalcare.service.SubcommentService;
import usyd.mingyi.animalcare.utils.BaseContext;
import usyd.mingyi.animalcare.utils.ResultData;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class CommentController {
    @Autowired
    CommentService commentService;
    @Autowired
    SubcommentService subcommentService;

    @PostMapping("/comment/{postId}")
    public R<String> addComment(@PathVariable("postId") long postId, @RequestBody Comment comment) {
        Long id = BaseContext.getCurrentId();
        comment.setPostId(postId);
        comment.setCommentTime(System.currentTimeMillis());
        comment.setUserId(id);
        log.info(comment.toString());
        commentService.save(comment);
        return R.success(" Comment post successfully");
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
    @GetMapping("/comment/subcomments/{commentId}")
    @ResponseBody
    public R<List<SubcommentDto>> getSubcommentsByCommentId(@PathVariable("commentId") long commentId){
        List<SubcommentDto> subcommentDtos = subcommentService.getSubcommentDtos(commentId,false);
        return R.success(subcommentDtos);
    }

}
