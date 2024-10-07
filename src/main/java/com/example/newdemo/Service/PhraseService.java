package com.example.newdemo.Service;

import com.example.newdemo.Entity.City;
import com.example.newdemo.Entity.Phrases;
import com.example.newdemo.Entity.Users;
import com.example.newdemo.Repository.PhraseRepository;
import com.example.newdemo.Repository.PropertyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhraseService {
    PhraseRepository phraseRepository;
    PropertyRepository propertyRepository;

    public PhraseService(PhraseRepository phraseRepository, PropertyRepository propertyRepository)
    {
        this.phraseRepository = phraseRepository;
        this.propertyRepository = propertyRepository;
    }

    public  void savePhrases(Phrases phrases){
        phraseRepository.save(phrases);
    }

    public List<Phrases> getAllPhrasesByFilter(String filter){
        if(filter == null || filter.isEmpty()){
            return phraseRepository.findAll();
        } else{
            return phraseRepository.search(filter);
        }
    }
    public void deletePhrases(Phrases phrases){
        phraseRepository.delete(phrases);
    }

    public List<Phrases> getAllPhrases(){
        return  phraseRepository.findAll();
    }

    public List<Phrases> getAllPhrasesByCity(City city){
        return phraseRepository.findByCity(city);
    }

    public List<Phrases> findAllPhrasesOfPropertyByUser(Users user){
        return propertyRepository.findAllPhrasesByUser(user);
    }
}
