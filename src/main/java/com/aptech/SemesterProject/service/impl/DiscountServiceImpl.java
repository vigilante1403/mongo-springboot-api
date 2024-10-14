package com.aptech.SemesterProject.service.impl;

import com.aptech.SemesterProject.entity.Discount;
import com.aptech.SemesterProject.repo.DiscountRepo;
import com.aptech.SemesterProject.service.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiscountServiceImpl implements DiscountService {
    @Autowired
    private DiscountRepo discountRepo;
    @Override
    public List<Discount> listOfDiscountsInPeriodGiven(String from, String to) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate fromDate = LocalDate.parse(from,formatter);
        LocalDate toDate = LocalDate.parse(to,formatter);
        List<Discount> all = discountRepo.findAll();
        all.stream().filter(discount->(discount.getDiscountFrom().isEqual(fromDate)||discount.getDiscountFrom().isAfter(fromDate))&&(discount.getDiscountTo().isEqual(toDate)||discount.getDiscountTo().isBefore(toDate))).collect(Collectors.toList());
        return all;
    }
}
