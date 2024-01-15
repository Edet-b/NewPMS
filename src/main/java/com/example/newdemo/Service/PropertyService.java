package com.example.newdemo.Service;

import com.example.newdemo.Entity.*;
import com.example.newdemo.Repository.ImageRepository;
import com.example.newdemo.Repository.PropertyRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class PropertyService {

    PropertyRepository propertyRepository;
    public PropertyService(PropertyRepository propertyRepository){
        this.propertyRepository = propertyRepository;
    }
    public void saveProperty(Property property){
        propertyRepository.save(property);
    }
    public Long totalProperties(){
        List<Property> allProperties = propertyRepository.findAll();
        return allProperties.stream().count();
    }
    public Long totalPropertiesByLand(){
        List<Property> landProperties = propertyRepository.findLandPropertiesByType(Property.PropertyType.Land);
        return landProperties.stream().count();
    }
    public Long totalOtherProperties() {
        List<Property> nonLandProperties = propertyRepository.findAllPropertiesExceptLand();
        return nonLandProperties.stream().count();
    }

    public void deleteProperty(Property property){
        propertyRepository.delete(property);
    }
    public List<Property> getAllProperties(){
        return propertyRepository.findAll();
    }
}
