package usyd.mingyi.animalcare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import usyd.mingyi.animalcare.pojo.Comment;
import usyd.mingyi.animalcare.pojo.Subcomment;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto extends Comment {
    private String userAvatar;
    private String nickName;
    private String userName;
    private List<SubcommentDto> subcommentDtos;
    private Integer subcommentsLength;
}
