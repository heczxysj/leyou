package com.leyou.upload.service.impl;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.upload.service.IUploadService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class UploadService implements IUploadService {

    //文件类型白名单
    private static final List<String> CONTENT_TYPE = Arrays.asList("image/jpeg","image/gif");

    //打印日志工具
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadService.class);

    //
    @Autowired
    private FastFileStorageClient storageClient;


    @Override
    public String upload(MultipartFile file) {

        //获取文件名
        String originalFilename = file.getOriginalFilename();
        //获取文件类型
        String contentType = file.getContentType();
        //校验文件类型
        if(!CONTENT_TYPE.contains(contentType)){
            //类型不合法 直接返回null
            LOGGER.info("文件类型不合法: {}",originalFilename);  //{}是占位符，代表后续的参数
            return null;
        }

        try {
            //检验文件内容
            //会把文件转换成二进制文件，如果bufferedImage等于空，则不是图片
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if(bufferedImage == null){
                LOGGER.info("文件内容不合法：{}",originalFilename);
                return null;
            }

            //保存到服务器
            //file.transferTo(new File("D:\\ideaProject\\image\\"+originalFilename));
            String ext = StringUtils.substringAfterLast(originalFilename, ".");  //"jpg"
            StorePath storePath = this.storageClient.uploadFile(file.getInputStream(), file.getSize(), ext, null);

            //生成url地址，返回
            //return "http://image.leyou.com/"+originalFilename;
            return "http://image.leyou.com/" + storePath.getFullPath();
        } catch (IOException e) {
            LOGGER.info("服务器内部错误：{}", originalFilename);
            e.printStackTrace();
        }
        return null;
    }
}
