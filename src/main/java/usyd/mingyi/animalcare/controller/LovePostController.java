package usyd.mingyi.animalcare.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import usyd.mingyi.animalcare.common.R;
import usyd.mingyi.animalcare.dto.LovePostDto;
import usyd.mingyi.animalcare.service.LovePostService;
import usyd.mingyi.animalcare.utils.BaseContext;

@RestController
@Slf4j
public class LovePostController {
    @Autowired
    LovePostService lovePostService;
    @GetMapping("/lovePosts")
    public R<Page<LovePostDto>> getAllLovedPosts(@RequestParam("current") Long current,
                                                 @RequestParam("pageSize") Integer pageSize) {
        return R.success(lovePostService.getLovePostsToMe(BaseContext.getCurrentId(), current, pageSize));
    }

    @GetMapping("/lovePost/read/{id}")
    public R<String> markAsRead(@PathVariable("id")Long lovePostId) {
        lovePostService.markLovePostRead(BaseContext.getCurrentId(),lovePostId);
        return R.success("Success");
    }

}
