package com.aptech.SemesterProject.controller;

import com.aptech.SemesterProject.entity.Discount;
import com.aptech.SemesterProject.exception.CustomRuntimeException;
import com.aptech.SemesterProject.repo.DiscountRepo;
import com.aptech.SemesterProject.service.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/discounts")
public class DiscountController {
    @Autowired
    private DiscountRepo discountRepo;
    @Autowired
    private DiscountService discountService;
    @GetMapping("/tours-all")
    public ResponseEntity<List<Discount>> getAllDiscountList(){
        return new ResponseEntity<>(discountRepo.findAll(), HttpStatus.OK);
    }
    @GetMapping("/tour/from-to")
    public ResponseEntity<List<Discount>> listOfToursInDiscountsInPeriodGiven(@RequestParam(name="from")String from,@RequestParam(name="to")String to){
        return new ResponseEntity<>(discountService.listOfDiscountsInPeriodGiven(from,to),HttpStatus.OK);
    }
    @PostMapping("/tour/addNew")
    public ResponseEntity<Discount> addDiscountTimerForTour(@RequestParam(name="tourId")String tourId,@RequestParam(name="from")String from,@RequestParam(name="to")String to,@RequestParam(name="percentageDiscount")double percentageDiscount) throws CustomRuntimeException {
        List<Discount> finalList = discountRepo.findAll();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dateFrom = LocalDate.parse(from,formatter);
        LocalDate dateTo = LocalDate.parse(to,formatter);
        // case 1:
        List<Discount> listA = finalList;
        if(!listA.isEmpty()){
            listA=listA.stream().filter(discount -> discount.getDiscountFrom().isBefore(dateFrom)&&(discount.getDiscountTo().isEqual(dateFrom)||discount.getDiscountTo().isAfter(dateFrom))).collect(Collectors.toList());
        }
        List<Discount> listB = finalList;
        if(!listB.isEmpty()){
            listB=listB.stream().filter(discount -> (discount.getDiscountFrom().isEqual(dateFrom)||discount.getDiscountFrom().isAfter(dateFrom))&&(discount.getDiscountTo().isEqual(dateTo)||discount.getDiscountTo().isBefore(dateTo))).collect(Collectors.toList());
        }
        List<Discount> listC = finalList;
        if(!listC.isEmpty()){
            listC=listC.stream().filter(discount -> (discount.getDiscountFrom().isEqual(dateTo)||discount.getDiscountFrom().isBefore(dateTo))&&discount.getDiscountTo().isAfter(dateTo)).collect(Collectors.toList());
        }
        List<Discount> combined = new ArrayList<>();
        combined.addAll(listA);
        combined.addAll(listB);
        combined.addAll(listC);
        combined=combined.stream().distinct().collect(Collectors.toList());
        // get distinct list
        Discount existed = combined.stream().filter(discount -> discount.getTourId().equals(tourId)).findFirst().orElse(null);
        if(existed!=null){
            throw new CustomRuntimeException("Tour with id: "+tourId+" has discount timer start from "+existed.getDiscountFrom()+" to"+existed.getDiscountTo());
        }
        Discount newDiscount = new Discount();
        newDiscount.setDiscountFrom(dateFrom);
        newDiscount.setDiscountTo(dateTo);
        if(percentageDiscount>1||percentageDiscount<=0)throw new CustomRuntimeException("Invalid percentage discount");
        newDiscount.setPercentageDiscount(percentageDiscount);
        newDiscount.setTourId(tourId);
        Discount result = discountRepo.save(newDiscount);
        return new ResponseEntity<>(result,HttpStatus.CREATED);

    }
    @PutMapping("/tour/editDiscount")
    public ResponseEntity<Discount> editDiscount(@RequestParam(name="discountId")String discountId,
                                                 @RequestParam(name="from",required = false)String from,
                                                 @RequestParam(name="to",required = false)String to,
                                                 @RequestParam(name="percentageDiscount",required = false) Double percentageDiscount ) throws CustomRuntimeException {
        // check existed
        Discount existed = discountRepo.findById(discountId).orElse(null);
        if(existed==null) throw new CustomRuntimeException("Discount not existed!");
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if(from!=null||to!=null){
            LocalDate dateFrom = from!=null? LocalDate.parse(from,formatter):existed.getDiscountFrom();
            LocalDate dateTo = to!=null?LocalDate.parse(to,formatter):existed.getDiscountTo();
            List<Discount> finalList = discountRepo.findAll();
            List<Discount> listA = finalList;
            if(!listA.isEmpty()){
                listA=listA.stream().filter(discount -> discount.getDiscountFrom().isBefore(dateFrom)&&(discount.getDiscountTo().isEqual(dateFrom)||discount.getDiscountTo().isAfter(dateFrom))).collect(Collectors.toList());
            }
            List<Discount> listB = finalList;
            if(!listB.isEmpty()){
                listB=listB.stream().filter(discount -> (discount.getDiscountFrom().isEqual(dateFrom)||discount.getDiscountFrom().isAfter(dateFrom))&&(discount.getDiscountTo().isEqual(dateTo)||discount.getDiscountTo().isBefore(dateTo))).collect(Collectors.toList());
            }
            List<Discount> listC = finalList;
            if(!listC.isEmpty()){
                listC=listC.stream().filter(discount -> (discount.getDiscountFrom().isEqual(dateTo)||discount.getDiscountFrom().isBefore(dateTo))&&discount.getDiscountTo().isAfter(dateTo)).collect(Collectors.toList());
            }
            List<Discount> combined = new ArrayList<>();
            combined.addAll(listA);
            combined.addAll(listB);
            combined.addAll(listC);
            combined=combined.stream().distinct().filter(discount -> !discount.getId().equals(discountId)&&discount.getTourId().equals(existed.getTourId())).collect(Collectors.toList());
            if(!combined.isEmpty()) throw new CustomRuntimeException("date start: "+from+" and date to "+to+" is duplicated with another discount timer");
            existed.setDiscountFrom(dateFrom);
            existed.setDiscountTo(dateTo);
        }
        if(percentageDiscount!=null){
            if(percentageDiscount.isNaN()||percentageDiscount>1||percentageDiscount<=0)throw new CustomRuntimeException("Invalid percentage discount");
            existed.setPercentageDiscount(percentageDiscount);
        }
        Discount result = discountRepo.save(existed);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }
    @DeleteMapping("/tour/delete")
    public ResponseEntity deleteADiscount(@RequestParam(name="discountId")String discountId) throws CustomRuntimeException {
        Discount existed = discountRepo.findById(discountId).orElse(null);
        if(existed==null) throw new CustomRuntimeException("DiscountId: "+discountId+" cannot be found");
        discountRepo.deleteById(discountId);
        return new ResponseEntity("ok", HttpStatus.NO_CONTENT);
    }
}
