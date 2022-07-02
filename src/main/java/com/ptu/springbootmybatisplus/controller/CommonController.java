package com.ptu.springbootmybatisplus.controller;

import com.ptu.springbootmybatisplus.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件下载上传功能
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${tupian.path}")
    private String tupian;
    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //file是临时文件，需要转存到指定位置

        //得到原始文件名
        String originalFilename = file.getOriginalFilename();
        //截取原始文件名.和后面的格式
        String substring = originalFilename.substring(originalFilename.lastIndexOf("."));

        //如果目录不存在则创建目录
        File file1 = new File(tupian);
        if (!file1.exists()){
            file1.mkdir();
        }


        //用uuid方法重新生成不重复的文件名
        String s = UUID.randomUUID().toString()+substring;
        try {
            file.transferTo(new File(tupian+s));
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("成功");
        return R.success(s);
    }
    /**
     * 文件上传
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){

        try {
            //输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(tupian+name));
            //输出流写回浏览器，浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");

            int len=0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) !=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            outputStream.close();
            fileInputStream.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
