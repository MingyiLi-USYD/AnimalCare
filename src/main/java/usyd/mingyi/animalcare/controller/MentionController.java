package usyd.mingyi.animalcare.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import usyd.mingyi.animalcare.common.R;
import usyd.mingyi.animalcare.dto.MentionDto;
import usyd.mingyi.animalcare.service.MentionService;
import usyd.mingyi.animalcare.service.PostService;
import usyd.mingyi.animalcare.utils.BaseContext;

@RestController
@Slf4j
public class MentionController {
    @Autowired
    MentionService mentionService;

    @Autowired
    PostService postService;

    @GetMapping("/mentions")
    public R<Page<MentionDto>> getAllMentionedPosts(@RequestParam("current") Long current,
                                                    @RequestParam("pageSize") Integer pageSize) {
        return R.success(mentionService.getAllMentionList(BaseContext.getCurrentId(), current, pageSize));
    }

    @GetMapping("/mention/read/{mentionId}")
    public R<String> markMentionAsRead(@PathVariable("mentionId") Long mentionId) {
        mentionService.markMentionAsRead(BaseContext.getCurrentId(),mentionId);
        return R.success("Success");
    }
/*    @GetMapping("/mention/count")
    public R<String> countMention() {
        mentionService.countMentions(BaseContext.getCurrentId());
        return R.success("Success");
    }*/


}
