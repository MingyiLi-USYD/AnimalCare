package usyd.mingyi.animalcare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import usyd.mingyi.animalcare.pojo.Subcomment;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubcommentDto extends Subcomment {
    private String userAvatar;
    private String nickName;
}
