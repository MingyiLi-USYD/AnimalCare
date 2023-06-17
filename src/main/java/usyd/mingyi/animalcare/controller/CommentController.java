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
import usyd.mingyi.animalcare.pojo.Comment;
import usyd.mingyi.animalcare.service.CommentService;
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

    @PostMapping("/comment/{postId}")
    public R<String> addComment(@PathVariable("postId") long postId, @RequestBody Map map) {


        String commentContent = (String) map.get("commentContent");
        Long id = BaseContext.getCurrentId();

        Comment comment = new Comment();
        comment.setCommentContent(commentContent);
        comment.setPostId(postId);
        comment.setCommentTime(System.currentTimeMillis());
        comment.setUserId(id);
        System.out.println(comment);
        commentService.save(comment);

        return R.success(" Comment post successfully");
    }


    @GetMapping("/comments/{postId}")
    @ResponseBody
    public R<IPage<CommentDto>> getCommentsByPostId(@RequestParam("currPage") long currPage, @RequestParam("pageSize") long pageSize,@PathVariable("postId") long postId) {
        IPage<CommentDto> commentsByPostId = commentService.getCommentsByPostId(currPage,pageSize,postId);
        return R.success(commentsByPostId);
    }


}
