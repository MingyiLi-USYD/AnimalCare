package usyd.mingyi.animalcare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import usyd.mingyi.animalcare.pojo.Comment;
import usyd.mingyi.animalcare.pojo.Post;
import usyd.mingyi.animalcare.pojo.Subcomment;
import usyd.mingyi.animalcare.pojo.User;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto extends Comment {
    private User commentUser;
    private List<SubcommentDto> subcommentDtos;
    private Integer subcommentsLength;
    private Post relevantPost;
}
