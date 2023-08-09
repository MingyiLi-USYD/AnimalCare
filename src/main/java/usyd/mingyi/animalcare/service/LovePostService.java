package usyd.mingyi.animalcare.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.yulichang.base.MPJBaseMapper;
import usyd.mingyi.animalcare.dto.LovePostDto;
import usyd.mingyi.animalcare.pojo.LovePost;

public interface LovePostService extends IService<LovePost> {
    Page<LovePostDto> getLovePostsToMe(Long userId, Long current, Integer pageSize);
    void markLovePostRead(Long userId, Long lovePostId);
}
