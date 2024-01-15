package com.example.newdemo.Repository;

import com.example.newdemo.Entity.City;
import com.example.newdemo.Entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@Repository
public interface CityRepository extends JpaRepository<City, Long> {


    @Query("select c from City c where lower(c.name) like lower(concat('%', :searchTerm, '%'))")
    List<City> search(@Param("searchTerm") String filter);

    Set<City> findByState(State state);

    @Query("SELECT c FROM City c WHERE c.state = :state")
    List<City> findByStates(@Param("state") State state);

    Optional<City> findByName(String name);

    Optional<City> findByCityId(String cityId);
}
