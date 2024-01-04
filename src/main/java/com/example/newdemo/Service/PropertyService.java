package com.example.newdemo.Service;

import com.example.newdemo.Entity.Property;
import com.example.newdemo.Repository.PropertyRepository;
import org.springframework.stereotype.Service;

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

    public void deleteProperty(Property property){
        propertyRepository.delete(property);
    }

    public List<Property> getAllProperties(){
        return propertyRepository.findAll();
    }
}
