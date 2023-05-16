package usyd.mingyi.animalcare.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ToString
@ConfigurationProperties(prefix = "project")
public class ProjectProperties {
    public String fileDiskLocation;
    public String projectPrefix;
}
