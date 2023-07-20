package usyd.mingyi.animalcare.controller;

import com.sun.management.OperatingSystemMXBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import oshi.SystemInfo;
import oshi.hardware.*;
import usyd.mingyi.animalcare.common.R;
import usyd.mingyi.animalcare.pojo.ServerInfo;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.List;


@RestController
@Slf4j
@RequestMapping("/system")
public class ViewDataController {

    @GetMapping("/data")
    public R<String> getSystemData() {
        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        ServerInfo serverInfo = new ServerInfo();
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();
        Sensors sensors = hardware.getSensors();

        // 获取内存信息
        GlobalMemory memory = hardware.getMemory();
        serverInfo.setTotalMemory(memory.getTotal()/ (1024 * 1024 ));
        serverInfo.setFreeMemory(memory.getAvailable()/ (1024 * 1024 ));
        serverInfo.setMemoryUsage((memory.getTotal() - memory.getAvailable()) / (double) memory.getTotal());

        // 获取CPU信息
        CentralProcessor processor = hardware.getProcessor();
        serverInfo.setCpuUsage(osBean.getCpuLoad());
        serverInfo.setTemperature(sensors.getCpuTemperature());
        serverInfo.setVoltage(sensors.getCpuVoltage());
        serverInfo.setCpuCores(processor.getPhysicalProcessorCount());
        serverInfo.setCpuFrequency(calculateAverageFrequency(processor.getCurrentFreq(),processor.getPhysicalProcessorCount()));
        // 获取磁盘信息
        getDiskSpaceInfo(serverInfo);

        System.out.println(serverInfo);

        // 获取其他系统信息...
        return null;
    }

    public static double calculateAverageFrequency(long[] frequencies, int coreCount) {
        long totalFrequency = 0;
        for (long freq : frequencies) {
            totalFrequency += freq;
        }
        double averageFrequency = (double) totalFrequency / coreCount;
        averageFrequency=averageFrequency/1E9;
        DecimalFormat df = new DecimalFormat("#.##");
        averageFrequency = Double.parseDouble(df.format(averageFrequency));

        return averageFrequency;
    }


    public void getDiskSpaceInfo(ServerInfo serverInfo) {
        File[] roots = File.listRoots();
        long totalSpaceSum = 0;
        long freeSpaceSum = 0;
        for (File root : roots) {
            long totalSpace = root.getTotalSpace();
            long freeSpace = root.getFreeSpace();

            totalSpaceSum += totalSpace;
            freeSpaceSum += freeSpace;
        }

        if (roots.length > 0) {
            double totalSpaceAverage = (double) totalSpaceSum / roots.length;
            double freeSpaceAverage = (double) freeSpaceSum / roots.length;
            double diskUsage = (totalSpaceAverage - freeSpaceAverage) / totalSpaceAverage * 100;

            // 将获取到的信息设置到ServerInfo对象中
            serverInfo.setTotalDiskSpace(totalSpaceAverage / (1024 * 1024 * 1024));  // 转换为GB单位
            serverInfo.setFreeDiskSpace(freeSpaceAverage / (1024 * 1024 * 1024));  // 转换为GB单位
            serverInfo.setDiskUsage(diskUsage);
        }
    }
}
