package com.example.newdemo.Repository;

import com.example.newdemo.Entity.City;
import com.example.newdemo.Entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;


@Repository
public interface CityRepository extends JpaRepository<City, Long> {


    @Query("select c from City c where lower(c.name) like lower(concat('%', :searchTerm, '%'))")
    List<City> search(@Param("searchTerm") String filter);

    Set<City> findByState(State state);
}
