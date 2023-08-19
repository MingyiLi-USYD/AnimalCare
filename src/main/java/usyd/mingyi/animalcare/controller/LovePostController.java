package usyd.mingyi.animalcare.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import usyd.mingyi.animalcare.common.R;
import usyd.mingyi.animalcare.dto.LovePostDto;
import usyd.mingyi.animalcare.service.LovePostService;
import usyd.mingyi.animalcare.service.PostService;
import usyd.mingyi.animalcare.utils.BaseContext;

@RestController
@Slf4j
public class LovePostController {
    @Autowired
    LovePostService lovePostService;
    @Autowired
    PostService postService;
    @GetMapping("/lovePosts")
    public R<Page<LovePostDto>> LovePostsToMe(@RequestParam("current") Long current,
                                                 @RequestParam("pageSize") Integer pageSize) {
        return R.success(lovePostService.getLovePostsToMe(BaseContext.getCurrentId(), current, pageSize));
    }

    @GetMapping("/lovePost/read/{id}")
    public R<String> markAsRead(@PathVariable("id")Long lovePostId) {
        lovePostService.markLovePostRead(BaseContext.getCurrentId(),lovePostId);
        return R.success("Success");
    }

    @GetMapping("/love/{postId}")
    public R<String> love(@PathVariable("postId") Long postId) {
        long id = BaseContext.getCurrentId();
        postService.loveAndSyncSocket(id, postId);
        return R.success("success");
    }

    @DeleteMapping("/love/{postId}")
    public R<String> cancelLove(@PathVariable("postId") Long postId) {
        long id = BaseContext.getCurrentId();
        postService.cancelLove(id, postId);
        return R.success("success");
    }

}
