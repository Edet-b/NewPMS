package com.example.newdemo.Repository;

import com.example.newdemo.Entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface StateRepository extends JpaRepository<State, Long> {

    @Query("select c from State c where lower(c.name) like lower(concat('%', :searchTerm, '%'))")
    List<State> search(@Param("searchTerm") String filter);

    Optional<State> findByName(String name);

    Optional<State> findByStateId(String stateId);
}
