package com.aptech.SemesterProject.controller;

import com.aptech.SemesterProject.dto.HttpResponse;
import com.aptech.SemesterProject.entity.Bill;
import com.aptech.SemesterProject.entity.Booking;
import com.aptech.SemesterProject.entity.User;
import com.aptech.SemesterProject.exception.CustomRuntimeException;
import com.aptech.SemesterProject.repo.BillRepo;
import com.aptech.SemesterProject.repo.BookingRepo;
import com.aptech.SemesterProject.repo.UserRepo;
import com.aptech.SemesterProject.service.BillService;
import com.aptech.SemesterProject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/bills")
public class BillController {
    @Autowired
    private BillRepo billRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private BillService billService;
    @Autowired
    private BookingRepo bookingRepo;
    @PreAuthorize("hasAnyAuthority('ADMIN','LEADGUIDE','GUIDE')")
    @GetMapping
    public ResponseEntity<List<Bill>> getAllBills() {
        return new ResponseEntity<>(billRepo.findAll(), HttpStatus.OK);
    }
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping
    public ResponseEntity<Bill> addBill(@RequestParam(name = "userId") String userId,
                                        @RequestParam(name = "bookingId") String bookingId
    ) throws CustomRuntimeException {
        // check coi exist chua hang add
        List<Bill> existed = billRepo.findByBookingId(bookingId);
        if(existed!=null&&!existed.isEmpty()) throw new CustomRuntimeException("Existed");
        Bill bill = new Bill();
        Optional<User> user = userRepo.findById(userId);
        Optional<Booking> booking = bookingRepo.findById(bookingId);
        if (booking.isEmpty()) {
            throw new CustomRuntimeException("No booking found");

        }
        if (!booking.get().getUser().getId().equals(userId)) {
            throw new CustomRuntimeException("This booking is not registered by this user");
        }
        if(!booking.get().isPaid()) {
            throw new CustomRuntimeException("Booking not paid");
        }
        bill.setUserEmail(user.get().getEmail());
        bill.setBooking(booking.get());
        bill.setPaidAt(LocalDateTime.now());
        bill.setBookingId(bookingId);
        Bill result = billRepo.save(bill);
        return new ResponseEntity<>(result, HttpStatus.OK);

    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{userEmail}")
    public ResponseEntity<List<Bill>> getBillsByUserEmail(@PathVariable(name = "userEmail")String userEmail) {
        return new ResponseEntity<>(billRepo.findByUserEmail(userEmail), HttpStatus.OK);
    }
//    @DeleteMapping
//    public ResponseEntity<HttpResponse> deleteBill(@PathVariable(name = "id") String id) {
//        billRepo.deleteById(id);
//        return new ResponseEntity<>(new HttpResponse(
//                HttpStatus.NO_CONTENT.value(),
//                HttpStatus.NO_CONTENT,
//                HttpStatus.NO_CONTENT.getReasonPhrase(),
//                "Bill has been delete successfully"),
//                HttpStatus.OK);
//
//    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/from-to-dates")
    public ResponseEntity<List<Bill>> getListBillsFromToDate(@RequestParam(name="dateFrom")String dateFrom){
        return new ResponseEntity<>(billService.getBillsFromDate(dateFrom),HttpStatus.OK);
    }
}
