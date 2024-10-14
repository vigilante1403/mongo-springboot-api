package com.aptech.SemesterProject.service;

import com.aptech.SemesterProject.dto.BookingDto;
import com.aptech.SemesterProject.entity.Booking;
import com.aptech.SemesterProject.exception.CustomRuntimeException;

import java.util.Date;
import java.util.List;

public interface BookingService {
    List<BookingDto> getBookings() throws CustomRuntimeException;
    List<BookingDto> getBookingsByUser(String user) throws CustomRuntimeException;
    List<BookingDto> getBookingsByTour(String tour) throws CustomRuntimeException;
    BookingDto upsertBooking(String id, String tour, String user,String email, String startDate,double priceOrigin,
                             double priceDiscount,boolean paid,int numJoin,String sessionId,
                             Long creationTime,String keysOfStartDate,String startDateId,String authorizedToken) throws CustomRuntimeException;
    void deleteBookingById(String id);
    void deleteBookingsByTour(String tour);
    void deleteBookingsByUser(String user);
    BookingDto getBookingById(String id);
    List<BookingDto> getBookingsFromDate(String dateFrom);
    boolean setAllBookingsStatusToValue(boolean value);
    void cancelBooking(String bookingId) throws CustomRuntimeException;
    BookingDto paidBooking(String sessionId) throws CustomRuntimeException;
    void cancelAllBookingsHaveRegex(String keysOfStartDate);
    void changeAllRelatedBookingsAfterTourChanged(String tourId,String startDateGuide,String startDateLocation) throws CustomRuntimeException;
    boolean checkingEnoughCapacityToMergeGuestsFromMultipleBookings(String prevKey,String nextKey,String tourId) throws CustomRuntimeException;
    List<BookingDto> getAllRelatedUpcomingSameTour(String userId,String tourId);
    BookingDto changeBookingSessionId(String bookingId,String freshSessionId) throws CustomRuntimeException;


}
