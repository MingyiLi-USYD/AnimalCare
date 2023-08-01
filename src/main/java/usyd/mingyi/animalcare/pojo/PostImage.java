package usyd.mingyi.animalcare.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@TableName("post_image")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class PostImage {
    @TableId("image_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Long imageId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Long postId;
    @NotBlank(message = "Must have imageUrl")
    String imageUrl;
    @NotBlank(message = "Must have fileName")
    String fileName;
}
