package usyd.mingyi.animalcare.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {
    @Value("${project.file-disk-location}")
    private String basePath;


    @GetMapping("/download")
    public void downloadFile(@RequestParam("name")String fileName, HttpServletResponse response){

         FileInputStream fileInputStream = null;
        ServletOutputStream outputStream = null;
        try {
             fileInputStream = new FileInputStream(basePath + fileName);
             outputStream = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = fileInputStream.read(buffer))!=-1){
                outputStream.write(buffer,0,len);
                outputStream.flush();
            }
        } catch (Exception e) {
            System.out.println(e);
            //throw new RuntimeException(e);
        }
        finally {
            try {
                outputStream.close();
                fileInputStream.close();
            } catch (Exception e) {
                //throw new RuntimeException(e);
            }

        }

    }

}
