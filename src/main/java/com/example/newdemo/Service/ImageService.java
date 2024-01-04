package com.example.newdemo.Service;


import com.example.newdemo.Entity.Property;
import com.example.newdemo.Entity.PropertyImage;
import com.example.newdemo.Repository.ImageRepository;
import org.springframework.stereotype.Service;

@Service
public class ImageService {

    ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository){
        this.imageRepository = imageRepository;
    }

    public void saveImageToDatabase(byte[] imageData) {
        PropertyImage imageEntity = new PropertyImage();
        imageEntity.setPropertyImages(imageData);
        imageRepository.save(imageEntity);
    }
}
