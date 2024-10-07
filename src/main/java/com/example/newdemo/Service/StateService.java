package com.example.newdemo.Service;

import com.example.newdemo.Entity.Users;
import com.example.newdemo.Repository.PropertyRepository;
import com.example.newdemo.Repository.StateRepository;
import com.example.newdemo.Entity.State;
import com.vaadin.flow.component.notification.Notification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StateService {

        StateRepository stateRepository;

        PropertyRepository propertyRepository;

        public StateService(StateRepository stateRepository, PropertyRepository propertyRepository){
            this.stateRepository = stateRepository;
            this.propertyRepository = propertyRepository;
        }

        public void saveState(State state){
            if(state == null)
                Notification.show("Form is null");
            else
                stateRepository.save(state);
        }

        public void deleteState(State state){
            stateRepository.delete(state);
        }

        public List<State> getAllStates(){
            return stateRepository.findAll();
        }

    public List<State> getAllStatesByFilter(String filterText){
        if(filterText == null || filterText.isEmpty()){
            System.out.println(filterText);
            return stateRepository.findAll();
        } else{
            System.out.println(filterText);
            return stateRepository.search(filterText);
        }
    }

    public List<State> findAllPropertyByStateThroughUser(Users user){
        return propertyRepository.findAllStateByUser(user);
    }
}
