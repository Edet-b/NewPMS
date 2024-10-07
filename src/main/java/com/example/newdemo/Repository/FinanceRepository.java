package com.example.newdemo.Repository;

import com.example.newdemo.Entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinanceRepository extends JpaRepository<Finances, Long> {

    @Query("SELECT c FROM Finances c " +
            "WHERE lower(concat(c.owner.firstName, ' ', c.owner.lastName)) LIKE lower(concat('%', :searchTerm, '%')) " +
            "OR lower(concat(c.owner.lastName, ' ', c.owner.firstName)) LIKE lower(concat('%', :searchTerm, '%'))")
    List<Finances> searchByUserToString(@Param("searchTerm") String filter);

    Finances findFirstByOwnerAndStateAndCityAndPhrasesAndTypeOrderByDateTimeDesc(
            Users owner, State state, City city, Phrases phrase, Property.PropertyType type);

    @Query("SELECT SUM(f.amountPaid) FROM Finances f")
    Double getTotalAmountPaid();
}
