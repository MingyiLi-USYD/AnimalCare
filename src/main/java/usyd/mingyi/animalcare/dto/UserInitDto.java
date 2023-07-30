package usyd.mingyi.animalcare.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInitDto extends UserDto {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private List<Long> loveIdList;
}
