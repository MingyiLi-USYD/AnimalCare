package usyd.mingyi.animalcare.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import usyd.mingyi.animalcare.common.CustomException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

@Component
public class ImageUtil {


    private static Properties properties = PropertiesUtil.readProperties("application.yml");

    private static String fileDiskLocation = properties.getProperty("file-disk-location");

    //private static String projectPrefix = properties.getProperty("project-prefix");
    public static String saveBatchToLocal(String userName, MultipartFile[] images,@Autowired ObjectMapper mapper){
        List<String> strings = new ArrayList<>();

        for (MultipartFile image : images) {

            saveImage(userName,image,strings);
        }
        try {

            return mapper.writeValueAsString(strings);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    public static void saveImage(String userName, MultipartFile image, List<String> imageList) {
        try {
            String originalName = image.getOriginalFilename();
            String suffix = originalName.substring(originalName.lastIndexOf("."));
            String tempFileName = UUID.randomUUID() + suffix; //文件名
            String path = fileDiskLocation + userName; //文件路径
            File newFile = new File(path+ File.separator + tempFileName);
            if(!newFile.getParentFile().exists()){
                newFile.getParentFile().mkdirs();
            }
            image.transferTo(newFile);
            imageList.add(userName + "/" + tempFileName);
        }catch (Exception e){
            e.printStackTrace();
            throw new CustomException("Images uploading error");
        }
    }

    public static String savePetImage(MultipartFile image,String userName){
        try {
            String originalName = image.getOriginalFilename();
            String suffix = originalName.substring(originalName.lastIndexOf("."));
            String tempFileName = UUID.randomUUID() + suffix; //文件名
            String path = fileDiskLocation + userName; //文件路径
            File newFile = new File(path+ File.separator + tempFileName);
            if(!newFile.getParentFile().exists()){
                newFile.getParentFile().mkdirs();
            }
            image.transferTo(newFile);
            return userName + "/" + tempFileName;
        }catch (Exception e){
            e.printStackTrace();
            throw new CustomException("Images uploading error");
        }
    }

}