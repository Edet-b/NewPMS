package com.example.newdemo.Repository;

import com.example.newdemo.Entity.PropertyImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<PropertyImage, Long> {
}
