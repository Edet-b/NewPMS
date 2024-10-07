package com.example.newdemo.Repository;

import com.example.newdemo.Entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {


    @Query("SELECT c FROM Property c WHERE " +
            "(:statuss is null or c.status = :statuss) " +
            "AND (:type is null or c.type = :type)" +
            "AND (:state is null or c.state = :state) " +
            "AND (:city is null or c.city = :city)" +
            "AND (:phrases is null or c.phrases = :phrases)")
    List<Property> searchByStatusStateTypeCityAndPhrases(@Param("statuss") Property.PropertyStatus status,
                                                 @Param("type") Property.PropertyType type,
                                                 @Param("state") State state,
                                                 @Param("city") City city,
                                                 @Param("phrases") Phrases phrase);

    @Query("SELECT p FROM Property p WHERE p.type = :propertyType AND p.type = 'Land'")
    List<Property> findLandPropertiesByType(@Param("propertyType") Property.PropertyType propertyType);

    @Query("SELECT p FROM Property p WHERE p.type <> 'Land'")
    List<Property> findAllPropertiesExceptLand();

    @Query("SELECT DISTINCT p.type FROM Property p " +
            "WHERE p.owners = :owners " +
            "AND p.state = :state " +
            "AND p.city = :city " +
            "AND p.phrases = :phrases")
    List<Property.PropertyType> findAllPropertyTypesByUser(@Param("owners") Users user,
                                                           @Param("state") State state,
                                                           @Param("city") City city,
                                                           @Param("phrases") Phrases phrase);


    @Query("SELECT p.state FROM Property  p WHERE p.owners = :owners")
    List<State> findAllStateByUser(@Param("owners") Users user);

    @Query("SELECT p.city FROM Property p WHERE p.owners = :owners")
    List<City> findAllCityByUser(@Param("owners") Users user);

    @Query("SELECT p.phrases FROM Property p WHERE p.owners = :owners")
    List<Phrases> findAllPhrasesByUser(@Param("owners") Users user);

    @Query("SELECT p.price FROM Property p " + "WHERE p.owners = :owners AND p.type = :type")
    Double findPropertyPriceByUserAndType(@Param("owners") Users user, @Param("type") Property.PropertyType propertyType);


    @Query("SELECT SUM(p.price) FROM Property p")
    Double findAllPropertyPrice();

    @Query("SELECT SUM(p.price) FROM Property p WHERE p.owners IS NOT NULL")
    Double findTotalPriceForPropertiesWithOwners();

}
