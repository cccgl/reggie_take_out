package com.heima.reggie.controller;

import com.heima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * @Description:
 * @Author: cckong
 * @Date:
 */
@Slf4j
@RequestMapping("/common")
@RestController
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) throws IOException {
        log.info(file.toString());
        File dir=new File(basePath);
        if(!dir.exists()){
            dir.mkdirs();
        }

        String name=file.getOriginalFilename();
        String suffix=name.substring(name.lastIndexOf("."));//substring单参数是开始位置 -最后 弄出来格式.jpg .png这种
        //使用uuid重新生成文件名
        String filename= UUID.randomUUID().toString()+suffix;
        file.transferTo(new File(basePath+filename));
        log.info("up filename:{}",filename);
        return R.success(filename);
    }
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){

        //输入流读取文件
        try {
            //输入流，通过输入流读取文件内容
            log.info("down filename:{}",name);
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
            log.info("down2 {}{}",basePath,name);
            //输出流，通过输出流将文件写回浏览器
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            //关闭资源
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //输出流写回浏览器
    }
}
