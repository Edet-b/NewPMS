package com.example.newdemo.Repository;

import com.example.newdemo.Entity.City;
import com.example.newdemo.Entity.Property;
import com.example.newdemo.Entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {


    @Query("SELECT c FROM Property c WHERE (:statuss is null or c.status = :statuss) " +
            "AND (:type is null or c.type = :type)" +
            "AND (:state is null or c.state = :state) " +
            "AND (:city is null or c.city = :city)")
    List<Property> searchByStatusStateTypeAndCity(@Param("statuss") Property.PropertyStatus status,
                                                 @Param("type") Property.PropertyType type,
                                                 @Param("state") State state,
                                                 @Param("city") City city);

    @Query("SELECT p FROM Property p WHERE p.type = :propertyType AND p.type = 'Land'")
    List<Property> findLandPropertiesByType(@Param("propertyType") Property.PropertyType propertyType);

    @Query("SELECT p FROM Property p WHERE p.type <> 'Land'")
    List<Property> findAllPropertiesExceptLand();

}
