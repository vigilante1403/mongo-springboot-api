package com.aptech.SemesterProject.service;

import com.aptech.SemesterProject.entity.Bill;

import java.util.List;

public interface BillService {
    List<Bill> getBillsFromDate(String dateFrom);
}
