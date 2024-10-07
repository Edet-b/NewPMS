package com.example.newdemo.Repository;


import com.example.newdemo.Entity.City;
import com.example.newdemo.Entity.Phrases;
import com.example.newdemo.Entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PhraseRepository extends JpaRepository<Phrases, Long> {

    @Query("select c from Phrases c where lower(c.name) like lower(concat('%', :searchTerm, '%'))")
    List<Phrases> search(@Param("searchTerm") String filter);

    @Query("SELECT c FROM Phrases c WHERE (:state is null or c.state = :state)" +
        "AND (:city is null or c.city = :city)")
    List<Phrases> searchByStateAndCity(@Param("state") State state,
                                       @Param("city") City city);

    Optional<Phrases> findByName(String name);
    Optional<Phrases> findByPhraseId(String phraseId);

    @Query("SELECT c FROM Phrases c WHERE c.city = :city")
    List<Phrases> findByCity(@Param("city") City city);
}
