package com.example.newdemo.Service;

import com.example.newdemo.Entity.Users;
import com.example.newdemo.Repository.UserRepository;
import com.example.newdemo.View.UserViews.UserView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }


    public void saveUsers(Users user){
        userRepository.save(user);
        UI.getCurrent().navigate(UserView.class);
    }

    public void deleteUser(Users user){
        userRepository.delete(user);
    }

    public List<Users> getAllUsers(){
        List<Users> users = userRepository.findAll();
        users.sort(Comparator.comparing(Users::getUpdatedAt).reversed());
        return users;
    }

    public List<Users> getAllUsersByFilter(String filter){
        List<Users> users = userRepository.findAll();
        List<Users> usersFilter = userRepository.search(filter);
        if(filter == null || filter.isEmpty()){
            users.sort(Comparator.comparing(Users::getUpdatedAt).reversed());
            return users;
        }else{
            usersFilter.sort(Comparator.comparing(Users::getUpdatedAt).reversed());
            return  usersFilter;
        }
    }

    public List<Users> getAllUsersByUserRole(Users.userRoles userRole) {
        String roles = userRole.toString();
        List<Users> users = userRepository.findAll();
        List<Users> usersByUserRole = userRepository.searchByUserRoles(userRole);

        if(roles.isEmpty() || roles == null){
            users.sort(Comparator.comparing(Users::getUpdatedAt).reversed());
            return users;
        } else {
            usersByUserRole.sort(Comparator.comparing(Users::getUpdatedAt).reversed());
            return usersByUserRole;
        }
    }

    public Long findUserByUserRoles(){
        List<Users> clientRoles = userRepository.findUserByUserRoles(Users.userRoles.Client);
        return  clientRoles.stream().count();
    }

    public List<Users> findUserByUserRoleClient(){
        List<Users> clients = new ArrayList<>(4);
        clients = userRepository.findUserByUserRoles(Users.userRoles.Client);

        return clients;
    }

    public Long findOtherUserRolesExceptClients(){
        List<Users> otherUserRolesExceptClients = userRepository.findAllUserRolesExceptClients();
        return  otherUserRolesExceptClients.stream().count();
    }


}
