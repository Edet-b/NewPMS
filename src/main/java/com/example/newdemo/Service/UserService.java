package com.example.newdemo.Service;

import com.example.newdemo.Entity.Users;
import com.example.newdemo.Repository.UserRepository;
import com.example.newdemo.View.UserViews.ClientView;
import com.vaadin.flow.component.UI;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }


    public void saveUsers(Users user){
        userRepository.save(user);
        UI.getCurrent().navigate(ClientView.class);
    }

    public void deleteUser(Users user){
        userRepository.delete(user);
    }

    public List<Users> getAllUsers(){
        List<Users> users = userRepository.findAll();
        users.sort(Comparator.comparing(Users::getUpdatedAt).reversed());
        return users;
    }

    public List<Users> getAllClientsByFilter(String filter){
        List<Users> users = userRepository.findUserByUserRoles(Users.userRoles.Client);
        List<Users> usersFilter = userRepository.searchClientUsers(filter);
        if(filter == null || filter.isEmpty()){
            users.sort(Comparator.comparing(Users::getUpdatedAt).reversed());
            return users;
        }else{
            usersFilter.sort(Comparator.comparing(Users::getUpdatedAt).reversed());
            return  usersFilter;
        }
    }

    public List<Users> getAllOtherUsersByFilter(String filter){
        List<Users> users = userRepository.findAllUserRolesExceptClients();
        List<Users> usersFilter = userRepository.searchOtherUsersButClient(filter);
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

    public List<Users> findUserNamesByClientUserRole() {
        //        clients.forEach(user -> user.setFullName(user.getFirstName() + " " + user.getLastName()));
        return userRepository.findUserByUserRoles(Users.userRoles.Client);
    }

    public List<Users> getAllUsersButClients(){
        return userRepository.findAllUserRolesExceptClients();
    }

}
