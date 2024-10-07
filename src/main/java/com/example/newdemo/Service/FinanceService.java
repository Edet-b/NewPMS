package com.example.newdemo.Service;


import com.example.newdemo.Entity.*;
import com.example.newdemo.Repository.FinanceRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class FinanceService {

    FinanceRepository financeRepository;

    public FinanceService(FinanceRepository financeRepository){
        this.financeRepository = financeRepository;
    }

    public void saveFinanceRecords(Finances finances){
        financeRepository.save(finances);
    }

    public List<Finances> searchFinancialRecordsByUserToString(String searchTerm) {
        List<Finances> wholeList = financeRepository.findAll();
        List<Finances> listByUserToString =  financeRepository.searchByUserToString(searchTerm);

        if(searchTerm == null || searchTerm.isEmpty()){
            wholeList.sort(Comparator.comparing(Finances::getDateTime).reversed());
            return wholeList;
        } else{
            listByUserToString.sort(Comparator.comparing(Finances::getDateTime).reversed());
            return listByUserToString;
        }
    }

    public double getTotalAmountPaid(){
        Double totalAmountPaid= financeRepository.getTotalAmountPaid();
        if (totalAmountPaid != null) {
            return totalAmountPaid;
        } else {
            return totalAmountPaid = 0.0;
        }
    }

    public List<Finances> getAllRecords(){
        List<Finances> wholeList = financeRepository.findAll();
        wholeList.sort(Comparator.comparing(Finances::getDateTime).reversed());
        return wholeList;
    }

    public Finances getLastRecordForUser(Users user, State state, City city, Phrases phrase, Property.PropertyType type) {
        return financeRepository.findFirstByOwnerAndStateAndCityAndPhrasesAndTypeOrderByDateTimeDesc(user, state, city, phrase, type);
    }

}


