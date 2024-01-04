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

    Optional<Users> findByUsername(String username);
}
