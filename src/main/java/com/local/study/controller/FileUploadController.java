package com.local.study.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @PostMapping("upload")
    public String service(MultipartFile file){
        if (logger.isDebugEnabled()){
            logger.debug("upload start ...");
        }
        String s = "";
        try {
            s = new String(file.getBytes(), "utf-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        if (logger.isDebugEnabled()){
            logger.debug("upload finished ...");
        }
        return s;
    }
}
