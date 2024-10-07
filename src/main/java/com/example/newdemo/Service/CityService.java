package com.example.newdemo.Service;


import com.example.newdemo.Entity.State;
import com.example.newdemo.Entity.Users;
import com.example.newdemo.Repository.CityRepository;
import com.example.newdemo.Entity.City;
import com.example.newdemo.Repository.PropertyRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CityService {

    CityRepository cityRepository;
    PropertyRepository propertyRepository;

    public CityService(CityRepository cityRepository, PropertyRepository propertyRepository){
        this.cityRepository = cityRepository;
        this.propertyRepository = propertyRepository;
    }

    public void saveCity(City city){
        cityRepository.save(city);
    }

    public void deleteCity(City city){
        cityRepository.delete(city);
    }

    public List<City> getAllCities(){
        return cityRepository.findAll();
    }

    public List<City> getAllCitiesByFilter(String filter){
        if(filter == null || filter.isEmpty()){
            return cityRepository.findAll();
        } else{
            return cityRepository.search(filter);
        }
    }

    public Set<City> getAllSetOfCities(){
        List<City> cityList = cityRepository.findAll();
        return new HashSet<>(cityList);
    }

    public Set<City> getAllCitiesByState(State state){
        return cityRepository.findByState(state);
    }

    public List<City> getAllCitiesByStateByList(State state){
        return cityRepository.findByStates(state);
    }

    public List<City> findAllCitiesOfPropertyByUser(Users user){
        return propertyRepository.findAllCityByUser(user);
    }
}
