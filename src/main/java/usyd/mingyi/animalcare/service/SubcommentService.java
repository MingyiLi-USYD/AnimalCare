package usyd.mingyi.animalcare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import usyd.mingyi.animalcare.dto.SubcommentDto;
import usyd.mingyi.animalcare.pojo.Subcomment;

import java.util.List;

public interface SubcommentService extends IService<Subcomment> {
    List<SubcommentDto> getSubcommentDtos(Long commentId,Boolean limit);
    Integer getSubcommentsSize(Long commentId);
    SubcommentDto saveAndSync(Subcomment subcomment);
}
