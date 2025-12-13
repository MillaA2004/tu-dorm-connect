package com.tuconnect.dorm_connect.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface CloudinaryService  {

    String uploadFile(MultipartFile file) throws IOException;


}
