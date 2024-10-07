package com.example.newdemo.Repository;

import com.example.newdemo.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    @Query("select c from Users c " +
            "where lower(c.firstName) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(c.lastName) like lower(concat('%', :searchTerm, '%'))" +
            "or lower(c.username) like lower(concat('%', :searchTerm, '%'))")
    List<Users> search(@Param("searchTerm") String filter);


    @Query("select c from Users c where c.userRoles = :userRole")
    List<Users> searchByUserRoles(@Param("userRole") Users.userRoles userRole);


    Optional<Users> findByEmail(String email);

    @Query("SELECT u.password FROM Users u WHERE u.username = :username")
    Optional<String> getHashedPasswordByUsername(@Param("username") String username);

    Optional<Users> findByUsername(String username);

    @Query("SELECT p FROM Users p WHERE p.userRoles = :userRoles AND p.userRoles = 'Client'")
    List<Users> findUserByUserRoles(@Param("userRoles") Users.userRoles roles);



    @Query("SELECT p FROM Users p WHERE p.userRoles <> 'Client'")
    List<Users> findAllUserRolesExceptClients();

    @Query("SELECT u FROM Users u WHERE u.userRoles <> 'Client' " +
            "AND (lower(u.firstName) LIKE lower(concat('%', :searchTerm, '%')) " +
            "OR lower(u.lastName) LIKE lower(concat('%', :searchTerm, '%')) " +
            "OR lower(u.username) LIKE lower(concat('%', :searchTerm, '%')))")
    List<Users> searchOtherUsersButClient(@Param("searchTerm") String filter);

    @Query("SELECT u FROM Users u WHERE u.userRoles = 'Client' " +
            "AND (lower(u.firstName) LIKE lower(concat('%', :searchTerm, '%')) " +
            "OR lower(u.lastName) LIKE lower(concat('%', :searchTerm, '%')) " +
            "OR lower(u.username) LIKE lower(concat('%', :searchTerm, '%')))")
    List<Users> searchClientUsers(@Param("searchTerm") String filter);



}
