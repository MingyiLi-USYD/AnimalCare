package usyd.mingyi.animalcare.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerInfo implements Serializable {
    private Long totalMemory;       // 总内存大小
    private Long freeMemory;        // 空闲内存大小
    private Double memoryUsage;     // 内存使用率
    private Integer cpuCores;
    private Double cpuUsage;        // CPU 使用率
    private Double cpuFrequency;
    private Double temperature;    // CPU 温度
    private Double voltage;           //电压
    private Double totalDiskSpace;    // 总磁盘空间大小
    private Double freeDiskSpace;     // 空闲磁盘空间大小
    private Double diskUsage;       // 磁盘使用率
    // 其他系统信息的可视化成员变量...
}
