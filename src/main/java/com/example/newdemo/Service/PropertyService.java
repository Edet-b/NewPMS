package com.example.newdemo.Service;

import com.example.newdemo.Entity.*;
import com.example.newdemo.Repository.ImageRepository;
import com.example.newdemo.Repository.PropertyRepository;
import com.example.newdemo.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PropertyService {

    PropertyRepository propertyRepository;

    UserRepository userRepository;
    public PropertyService(PropertyRepository propertyRepository, UserRepository userRepository){
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
    }
    public void saveProperty(Property property){
        propertyRepository.save(property);
    }
    public Long totalProperties(){
        List<Property> allProperties = propertyRepository.findAll();
        return allProperties.stream().count();
    }
    public double getAllPropertyPrices(){
        Double propertyPrice = propertyRepository.findAllPropertyPrice();
        if (propertyPrice != null) {
            return propertyPrice;
        } else {
            return propertyPrice = 0.0;
        }
    }

    public double getAllPropertyPricesWithOwners(){
        return propertyRepository.findTotalPriceForPropertiesWithOwners();
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

    public List<Property.PropertyType> findAllPropertyTypesForUser(Users user, State state, City city, Phrases phrases) {
        return propertyRepository.findAllPropertyTypesByUser(user, state, city, phrases);
    }

    public Double findPropertyPriceByUserAndType(Users user, Property.PropertyType type) {
        return propertyRepository.findPropertyPriceByUserAndType(user, type);
    }

}
