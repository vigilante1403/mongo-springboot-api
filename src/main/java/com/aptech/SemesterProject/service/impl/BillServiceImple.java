package com.aptech.SemesterProject.service.impl;

import com.aptech.SemesterProject.dto.BookingDto;
import com.aptech.SemesterProject.entity.Bill;
import com.aptech.SemesterProject.entity.Booking;
import com.aptech.SemesterProject.repo.BillRepo;
import com.aptech.SemesterProject.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BillServiceImple implements BillService {
    @Autowired
    private BillRepo billRepo;
    @Override
    public List<Bill> getBillsFromDate(String dateFrom) {

        List<Bill> list = billRepo.findAll();
        List<String> idList = new ArrayList<>();
        if (list != null && list.size() > 0) {
            list.stream().forEach(bill -> {
                LocalDate date1 = LocalDate.parse(bill.getPaidAt().toString().split("T")[0], DateTimeFormatter.ISO_LOCAL_DATE);
                LocalDate date2 = LocalDate.parse(dateFrom, DateTimeFormatter.ISO_LOCAL_DATE);
                boolean isAfter = date1.isAfter(date2);
                if (isAfter) idList.add(bill.getId());
            });
            list = list.stream().filter(booking -> idList.contains(booking.getId())).collect(Collectors.toList());
        }
        return list;
    }
}
