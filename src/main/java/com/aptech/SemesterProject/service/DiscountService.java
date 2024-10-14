package com.aptech.SemesterProject.service;

import com.aptech.SemesterProject.entity.Discount;

import java.util.List;

public interface DiscountService {
    List<Discount> listOfDiscountsInPeriodGiven(String from,String to);
}
