package usyd.mingyi.animalcare.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import usyd.mingyi.animalcare.dto.MentionDto;
import usyd.mingyi.animalcare.pojo.Mention;

public interface MentionService extends IService<Mention> {
    Page<MentionDto> getAllMentionList(Long userId, Long current, Integer pageSize);
    void markMentionAsRead(Long userId,Long mentionId);

    Integer countMentionsReceived(Long userId);
}
