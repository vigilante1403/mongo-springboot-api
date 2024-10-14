package com.aptech.SemesterProject.controller;

import com.aptech.SemesterProject.dto.BookingDto;
import com.aptech.SemesterProject.dto.HttpResponse;
import com.aptech.SemesterProject.exception.CustomRuntimeException;
import com.aptech.SemesterProject.repo.TourRepo;
import com.aptech.SemesterProject.service.BookingService;
import com.aptech.SemesterProject.utility.JWTTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private TourRepo tourRepo;
    @Autowired
    private JWTTokenProvider jwtTokenProvider;
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable String id){
        return new ResponseEntity<>(bookingService.getBookingById(id),HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<List<BookingDto>> getAllBookings() throws CustomRuntimeException {
        return new ResponseEntity<>(bookingService.getBookings(),HttpStatus.OK);
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingDto>> getBookingsByUserId(@PathVariable String userId) throws CustomRuntimeException {
        return new ResponseEntity<>(bookingService.getBookingsByUser(userId), HttpStatus.OK);
    }
    @GetMapping("/tour/{tourId}")
    public ResponseEntity<List<BookingDto>> getBookingsByTourId(@PathVariable String tourId) throws CustomRuntimeException {
        return new ResponseEntity<>(bookingService.getBookingsByTour(tourId),HttpStatus.OK);
    }
    @PreAuthorize("hasAnyAuthority('ADMIN','LEADGUIDE','USER')")
    @PostMapping
    public ResponseEntity<BookingDto> addNewBooking(@RequestParam(name="tour")String tour, @RequestParam(name="user")String user, @RequestParam(name="email",required = false)String email,
                                                    @RequestParam(name="priceOrigin",required = false)Double priceOrigin, @RequestParam(name="priceDiscount",required = false)Double priceDiscount,
                                                    @RequestParam(name="date")String date, @RequestParam(name="paid")boolean paid, @RequestParam(name="numJoin")int numJoin,
                                                    @RequestParam(name="sessionId",required = false)String sessionId, @RequestParam(name="creationTime",required = false) Long creationTime,
                                                    @RequestParam(name="keysOfStartDate",required = false)String keysOfStartDate,
                                                    @RequestParam(name="startDateId")String startDateId,
                                                    HttpServletRequest request) throws CustomRuntimeException {
//        String token = Arrays.stream(request.getCookies()).filter(cookie->cookie.getName().equals("token-vtravel-lib0-authw")).findFirst().orElse(null).getValue();
//        String username = Arrays.stream(request.getCookies()).filter(cookie->cookie.getName().equals("user-vtravel-info")).findFirst().orElse(null).getValue();
//        boolean isTokenValid = jwtTokenProvider.isTokenValid(username,token);
//        if(isTokenValid){
            BookingDto result = bookingService.upsertBooking(null,tour,user,email,date,priceOrigin,priceDiscount,paid,numJoin,sessionId,creationTime,keysOfStartDate,startDateId,"user");

            return new ResponseEntity<>(result,HttpStatus.OK);
//        }
//        return ResponseEntity.badRequest().build();

    }
    @PreAuthorize("hasAnyAuthority('ADMIN','LEADGUIDE')")
    @PutMapping
    public ResponseEntity<BookingDto> updateBooking(@RequestParam(name="id")String id,@RequestParam(name="tour")String tour,
                                                    @RequestParam(name="user")String user,@RequestParam(name="email",required = false)String email,
                                                    @RequestParam(name="date")String date,@RequestParam(name="priceOrigin",required = false)Double priceOrigin,
                                                    @RequestParam(name="priceDiscount",required = false)Double priceDiscount,@RequestParam(name="paid")boolean paid,
                                                    @RequestParam(name="numJoin")int numJoin,@RequestParam(name="sessionId",required = false)String sessionId,
                                                    @RequestParam(name="creationTime",required = false) Long creationTime,@RequestParam(name="keysOfStartDate",required = false)String keysOfStartDate,
                                                    @RequestParam(name="startDateId")String startDateId,
                                                    HttpServletRequest request) throws CustomRuntimeException {
        double priceOg;
        double priceDis;
        if(priceOrigin!=null&&priceOrigin>0){
            priceOg=priceOrigin;
        }else{
            priceOg=0;
        }
        if(priceDiscount!=null&&priceDiscount>0){
            priceDis=priceDiscount;
        }else{
            priceDis=0;
        }String token = Arrays.stream(request.getCookies()).filter(cookie->cookie.getName().equals("token-vtravel-lib0-authw")).findFirst().orElse(null).getValue();
        String username = Arrays.stream(request.getCookies()).filter(cookie->cookie.getName().equals("user-vtravel-info")).findFirst().orElse(null).getValue();
        boolean isTokenValid = jwtTokenProvider.isTokenValid(username,token);
        if(isTokenValid){
            BookingDto result = bookingService.upsertBooking(id,tour,user,email,date,priceOg,priceDis,paid,numJoin,sessionId,creationTime,keysOfStartDate,startDateId,token);
            return new ResponseEntity<>(result,HttpStatus.OK);
        }
        return ResponseEntity.badRequest().build();

    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping
    public ResponseEntity deleteBookingById(@RequestParam(name="id")String id){
        bookingService.deleteBookingById(id);
        return ResponseEntity.noContent().build();
    }
    @PreAuthorize("hasAnyAuthority('ADMIN','LEADGUIDE','GUIDE')")
    @PostMapping("/from-to-dates")
    public ResponseEntity<List<BookingDto>> getBookingsFromDate(@RequestParam(name="from")String from){
        return new ResponseEntity<>(bookingService.getBookingsFromDate(from),HttpStatus.OK);
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/change-status")
    public ResponseEntity<HttpResponse> setAllBookingsToTrueTemp(){
         bookingService.setAllBookingsStatusToValue(true);
        return new ResponseEntity<HttpResponse>(new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(), "Set all bookings success"),HttpStatus.OK);
    }
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PutMapping("/cancel-booking")
    public ResponseEntity<HttpResponse> cancelBooking(@RequestParam(name="bookingId")String bookingId) throws CustomRuntimeException {
        bookingService.cancelBooking(bookingId);
        return new ResponseEntity<HttpResponse>(new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(), "Cancel booking successfully"),HttpStatus.OK);
    }
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/changing-related-booking-after-update-tour")
    public ResponseEntity<HttpResponse> changingBooking(@RequestParam(name="tourId")String tourId,@RequestParam(name="dateOfLocationAfter",required = false)String dateOfLocationAfter,@RequestParam(name="dateOfGuideAfter",required = false)String dateOfGuideAfter) throws CustomRuntimeException {
        bookingService.changeAllRelatedBookingsAfterTourChanged(tourId,dateOfGuideAfter,dateOfLocationAfter);
        return new ResponseEntity<>(new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(), "Set all bookings success"),HttpStatus.OK);
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user/booking-upcoming-related")
    public ResponseEntity<List<BookingDto>> getRelatedBookingOfSameTour(@RequestParam(name="userId")String userId,@RequestParam(name="tourId")String tourId){
        return new ResponseEntity<>(bookingService.getAllRelatedUpcomingSameTour(userId,tourId),HttpStatus.OK);
    }
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    @PostMapping("/change-session")
    public ResponseEntity<BookingDto> changeSessionIdOfBooking(@RequestParam(name="bookingId") String bookingId,@RequestParam(name="sessionId")String sessionId) throws CustomRuntimeException {
        return new ResponseEntity<>(bookingService.changeBookingSessionId(bookingId,sessionId),HttpStatus.OK);
    }



}
