package com.aptech.SemesterProject.service.impl;

import com.aptech.SemesterProject.dto.BookingDto;
import com.aptech.SemesterProject.entity.*;
import com.aptech.SemesterProject.exception.CustomRuntimeException;
import com.aptech.SemesterProject.repo.*;
import com.aptech.SemesterProject.service.BookingService;
import com.aptech.SemesterProject.service.EmailService;
import com.aptech.SemesterProject.service.Helper;
import com.aptech.SemesterProject.utility.JWTTokenProvider;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {
    @Autowired
    private BookingRepo bookingRepo;
    @Autowired
    private Helper helper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private TourRepo tourRepo;
    @Autowired
    private JWTTokenProvider jwtTokenProvider;
    @Autowired
    private StartDateRepo startDateRepo;
    @Autowired
    private TourScheduleRepo tourScheduleRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private EmailService emailService;


    @Override
    public List<BookingDto> getBookings() throws CustomRuntimeException {
        List<Booking> bookings = bookingRepo.findAll();
        List<BookingDto> dtoList = bookings.stream().map(booking -> new BookingDto(booking.getId(), booking.getTour(), booking.getUser(), booking.getStartDate(), booking.getPriceOrigin(), booking.getPriceDiscount(), booking.getPriceFinal(), (Integer) booking.getNumPeopleJoined() == null || booking.getNumPeopleJoined() == 0 ? 1 : booking.getNumPeopleJoined(), booking.isPaid(), booking.getCreatedAt(), booking.isStatus(), booking.getSessionId(), booking.getCreationTime(), booking.getKeysOfStartDate(), booking.getStartDateId())).collect(Collectors.toList());
        return dtoList;
    }

    @Override
    public List<BookingDto> getBookingsByUser(String user) throws CustomRuntimeException {
        Map<String, Object> userResult = helper.checkUserValid(user);
        if (userResult.get("valid").equals(false)) {
            // Tk user bi xoa hoac ko valid
            List<Booking> list1 = bookingRepo.findByUserId(user);
            if (list1 == null) throw new CustomRuntimeException("No bookings found with userId:" + user);
            List<BookingDto> dtoList1 = list1.stream().map(booking -> new BookingDto(booking.getId(), booking.getTour(), booking.getUser(), booking.getStartDate(), booking.getPriceOrigin(), booking.getPriceDiscount(), booking.getPriceFinal(), (Integer) booking.getNumPeopleJoined() == null || booking.getNumPeopleJoined() == 0 ? 1 : booking.getNumPeopleJoined(), booking.isPaid(), booking.getCreatedAt(), booking.isStatus(), booking.getSessionId(), booking.getCreationTime(), booking.getKeysOfStartDate(), booking.getStartDateId())).collect(Collectors.toList());
            return dtoList1;
        }
        User userObj = (User) userResult.get("data");
        List<Booking> bookings = bookingRepo.findByUserId(user);
        if (bookings == null) throw new CustomRuntimeException("No bookings found with userId:" + user);
        List<BookingDto> dtoList = bookings.stream().map(booking -> new BookingDto(booking.getId(), booking.getTour(), userObj, booking.getStartDate(), booking.getPriceOrigin(), booking.getPriceDiscount(), booking.getPriceFinal(), (Integer) booking.getNumPeopleJoined() == null || booking.getNumPeopleJoined() == 0 ? 1 : booking.getNumPeopleJoined(), booking.isPaid(), booking.getCreatedAt(), booking.isStatus(), booking.getSessionId(), booking.getCreationTime(), booking.getKeysOfStartDate(), booking.getStartDateId())).collect(Collectors.toList());
        return dtoList;

    }

    @Override
    public List<BookingDto> getBookingsByTour(String tour) throws CustomRuntimeException {
        Map<String, Object> tourResult = helper.checkTourValid(tour);
        if (tourResult.get("valid").equals(false)) {
            // tour bi xoa hoac ko valid
            List<Booking> list1 = bookingRepo.findByTourId(tour);
            if (list1 == null) throw new CustomRuntimeException("No bookings found with tourId:" + tour);
            List<BookingDto> dtoList1 = list1.stream().map(booking -> new BookingDto(booking.getId(), booking.getTour(), booking.getUser(), booking.getStartDate(), booking.getPriceOrigin(), booking.getPriceDiscount(), booking.getPriceFinal(), (Integer) booking.getNumPeopleJoined() == null || booking.getNumPeopleJoined() == 0 ? 1 : booking.getNumPeopleJoined(), booking.isPaid(), booking.getCreatedAt(), booking.isStatus(), booking.getSessionId(), booking.getCreationTime(), booking.getKeysOfStartDate(), booking.getStartDateId())).collect(Collectors.toList());
            return dtoList1;
        }
        Tour tourObj = (Tour) tourResult.get("data");
        List<Booking> list = bookingRepo.findByTourId(tour);
        if (list == null) throw new CustomRuntimeException("No bookings found with tourId:" + tour);
        List<BookingDto> dtoList = list.stream().map(booking -> new BookingDto(booking.getId(), booking.getTour(), booking.getUser(), booking.getStartDate(), booking.getPriceOrigin(), booking.getPriceDiscount(), booking.getPriceFinal(), (Integer) booking.getNumPeopleJoined() == null || booking.getNumPeopleJoined() == 0 ? 1 : booking.getNumPeopleJoined(), booking.isPaid(), booking.getCreatedAt(), booking.isStatus(), booking.getSessionId(), booking.getCreationTime(), booking.getKeysOfStartDate(), booking.getStartDateId())).collect(Collectors.toList());
        return dtoList;
    }

    @Override
    public BookingDto upsertBooking(String id, String tour, String user, String email, String startDate, double priceOrigin,
                                    double priceDiscount, boolean paid, int numJoin, String sessionId, Long creationTime,
                                    String keysOfStartDate, String startDateId, String authorizedToken) throws CustomRuntimeException {
        // case insert
        //check if tour and user and date is valid
        Map<String, Object> tourResult = helper.checkTourValid(tour);
        Map<String, Object> userResult = new HashMap<>();
        if (email != null && !email.isEmpty()) {
            userResult = helper.checkUserValidByEmail(email);
        } else {
            userResult = helper.checkUserValid(user);
        }
        Map<String, Object> dateResult = helper.checkDateValid(startDate);
        if (tourResult.get("valid").equals(false) || userResult.get("valid").equals(false) || dateResult.get("valid").equals(false))
            throw new CustomRuntimeException("Tour id or user email or date input is invalid");
        List<Booking> list1 = bookingRepo.findByTourId(tour);
//        int totalBookingsTour=(int)list1.stream().count();
        Booking b = new Booking();
        Tour tourObj = (Tour) tourResult.get("data");
        User userObj = (User) userResult.get("data");
        List<Booking> listToCheck = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy");
        LocalDate require = LocalDate.parse(startDate);
        list1.forEach(booking -> {
            LocalDate check = LocalDate.parse(booking.getStartDate(), formatter);
            if (check.equals(require) && booking.isStatus() == true) {
                listToCheck.add(booking);
            }
        });
        AtomicInteger numPeople = new AtomicInteger();
        listToCheck.forEach(booking -> {
            Integer num = booking.getNumPeopleJoined();
            if (num == null || num == 0) {
                numPeople.set(numPeople.get() + 1);
            } else {
                numPeople.set(numPeople.get() + num);
            }
        });
        Integer ticketsleft = tourObj.getMaxGroupSize()-numPeople.get()<=0?0:tourObj.getMaxGroupSize()-numPeople.get();
        numPeople.set(numPeople.get() + numJoin);


        if (Integer.parseInt(numPeople.toString()) > tourObj.getMaxGroupSize())
            throw new CustomRuntimeException("This tour has only "+ticketsleft+" tickets left");
//        if (!Arrays.asList(RoleEnum.USER, RoleEnum.ADMIN).contains(userObj.getRole()))
//            throw new CustomRuntimeException("Cannot add booking for this role: " + userObj.getRole());
        Date date = (Date) dateResult.get("data");
        if (id != null) {
//            if(id == null && checkIfExistBooking(user,tour,date) ){
//                throw new CustomRuntimeException("Booking already exist");
//            }else{

            b = bookingRepo.findById(id).orElse(null);
            if (b == null) throw new CustomRuntimeException("No booking found with id: " + id);
            if (!b.getUserId().equals(user))
                throw new CustomRuntimeException("User with id: " + user + " hasn't booked this tour!");
            b.setTour(tourObj);
            b.setTourId(tour);
            b.setStartDate(date.toString());
            double priceOg;
            double priceDis;
            if (priceOrigin > 0) {
                priceOg = priceOrigin;
            } else {
                priceOg = tourObj.getPrice();
            }
            if (priceDiscount > 0) {
                priceDis = priceDiscount;
            } else {
                priceDis = tourObj.getPriceDiscount();
            }
            if ((priceOg - priceDis) < 0) throw new CustomRuntimeException("Price cannot be lower than discount");
            b.setPriceOrigin(priceOg);
            b.setPriceDiscount(priceDis);
            b.setPriceFinal(priceOg - priceDis);
            b.setPaid(paid);
            b.setNumPeopleJoined(numJoin);
            b.setUpdatedAt(LocalDateTime.now().toString());
            if (sessionId != null) b.setSessionId(sessionId);
            if (creationTime != null) b.setCreationTime(creationTime);
            if (keysOfStartDate != null) b.setKeysOfStartDate(keysOfStartDate);
            if (startDateId != null) b.setStartDateId(startDateId);
//            }

        } else if (id == null) {
            b.setTour(tourObj);
            b.setTourId(tour);
            b.setUserId(user);
            b.setUser(userObj);
            boolean hasGuideOrUserAuthority;
            if (authorizedToken == "user") {
                hasGuideOrUserAuthority = true;
            } else {
                List<GrantedAuthority> grantedAuthorityList = jwtTokenProvider.getAuthoritiesFromToken(authorizedToken);
                hasGuideOrUserAuthority = grantedAuthorityList.stream()
                        .anyMatch(authority -> authority.getAuthority().equals("USER") || authority.getAuthority().equals("GUIDE"));
            }

            if (hasGuideOrUserAuthority) {
                LocalDate now = LocalDate.now();
                DateTimeFormatter f1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate check = LocalDate.parse(startDate, f1);
                if (now.isAfter(check)) throw new CustomRuntimeException("Unauthorized access to book this date");
            }
            b.setStartDate(date.toString());
            if (keysOfStartDate != null) b.setKeysOfStartDate(keysOfStartDate);
            if (startDateId != null) b.setStartDateId(startDateId);
            double priceOg;
            double priceDis;
            if (priceOrigin > 0) {
                priceOg = priceOrigin;
            } else {
                priceOg = tourObj.getPrice();
            }
            if (priceDiscount > 0) {
                priceDis = priceDiscount;
            } else {
                priceDis = tourObj.getPriceDiscount();
            }
            b.setPriceOrigin(priceOg);
            b.setPriceDiscount(priceDis);
            b.setPriceFinal(priceOg - priceDis);
            b.setCreatedAt(LocalDateTime.now().toString());
            b.setNumPeopleJoined(numJoin);
            b.setPaid(paid);
            b.setStatus(true);
            if (sessionId != null) b.setSessionId(sessionId);
            if (creationTime != null) b.setCreationTime(creationTime);
        }

        BookingDto dto = new BookingDto();
        try {
            Booking result = bookingRepo.save(b);
            // update tour joining
            dto.setId(result.getId());
            dto.setTour(tourObj);
            dto.setUser(userObj);
            dto.setPriceOrigin(result.getPriceOrigin());
            dto.setPriceDiscount(result.getPriceDiscount());
            dto.setPriceFinal(result.getPriceFinal());
            dto.setStartDate(result.getStartDate());
            if ((Integer) result.getNumPeopleJoined() == null || result.getNumPeopleJoined() == 0) {
                dto.setNumJoin(1);
            } else {
                dto.setNumJoin(result.getNumPeopleJoined());
            }
            dto.setStatus(result.isStatus());
            dto.setSessionId(result.getSessionId());
            dto.setCreationTime(result.getCreationTime());

        } catch (Exception e) {
            throw new CustomRuntimeException("Error when saving booking");
        }
        return dto;


    }


    @Override
    public void deleteBookingById(String id) {
        bookingRepo.deleteById(id);
    }

    @Override
    public void deleteBookingsByTour(String tour) {
        bookingRepo.deleteByTour(tour);
    }

    @Override
    public void deleteBookingsByUser(String user) {
        bookingRepo.deleteByUser(user);
    }

    @Override
    public BookingDto getBookingById(String id) {
        Booking booking = bookingRepo.findById(id).orElse(null);
        BookingDto dto = null;
        if (booking != null) {
            dto = new BookingDto(booking.getId(), booking.getTour(), booking.getUser(), booking.getStartDate(), booking.getPriceOrigin(), booking.getPriceDiscount(), booking.getPriceFinal(), (Integer) booking.getNumPeopleJoined() == null || booking.getNumPeopleJoined() == 0 ? 1 : booking.getNumPeopleJoined(), booking.isPaid(), booking.getCreatedAt(), booking.isStatus(), booking.getSessionId(), booking.getCreationTime(), booking.getKeysOfStartDate(), booking.getStartDateId());
        }
        return dto;
    }

    @Override
    public List<BookingDto> getBookingsFromDate(String dateFrom) {
        List<BookingDto> listDto = null;
        List<Booking> list = bookingRepo.findAll();
        List<String> idList = new ArrayList<>();
        if (list != null && list.size() > 0) {
            list.stream().forEach(booking -> {
                LocalDate date1 = LocalDate.parse(booking.getCreatedAt().split("T")[0], DateTimeFormatter.ISO_LOCAL_DATE);
                LocalDate date2 = LocalDate.parse(dateFrom, DateTimeFormatter.ISO_LOCAL_DATE);
                boolean isAfter = date1.isAfter(date2);
                if (isAfter) idList.add(booking.getId());
            });
            list = list.stream().filter(booking -> idList.contains(booking.getId())).collect(Collectors.toList());
        }
        if (list != null) {
            listDto = list.stream().map(booking -> new BookingDto(booking.getId(), booking.getTour(),
                    booking.getUser(), booking.getStartDate(), booking.getPriceOrigin(), booking.getPriceDiscount(),
                    booking.getPriceFinal(), (Integer) booking.getNumPeopleJoined() == null || booking.getNumPeopleJoined() == 0 ? 1 : booking.getNumPeopleJoined(), booking.isPaid(),
                    booking.getCreatedAt(), booking.isStatus(), booking.getSessionId(), booking.getCreationTime(), booking.getKeysOfStartDate(), booking.getStartDateId())).collect(Collectors.toList());
        }
        return listDto;
    }

    @Override
    public boolean setAllBookingsStatusToValue(boolean value) {
        List<Booking> list = bookingRepo.findAll();
//        System.out.println(list);
        if (list == null || list.isEmpty()) return false;
        try {
            List<Booking> newList = list.stream().map(booking -> {
                booking.setStatus(value);
                Booking result = bookingRepo.save(booking);
                System.out.println("Booking is active");
                return result;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void cancelBooking(String bookingId) throws CustomRuntimeException {
        Booking b = bookingRepo.findById(bookingId).orElse(null);
        if (b == null) throw new CustomRuntimeException("No booking found");
        b.setStatus(false);
        try {
            bookingRepo.save(b);
            String tourId = b.getTourId();
            Tour t = tourRepo.findById(tourId).orElse(null);
            String tourName;
            String dateTime = b.getStartDate();
            String userEmail = b.getUser().getEmail();
            if(t==null){
                tourName="Deleted Tour";
            }else{
                tourName=t.getName();
            }

            Map<String, Object> template = new HashMap<>();
            template.put("recipientName", "User email");
            template.put("tourName", tourName);
            template.put("dateTime", dateTime);
            template.put("senderName", "Vy Truong");

                emailService.sendMessageUsingThymeleafTemplate(userEmail, "Your booking has been canceled", template, "cancelBooking.html");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public BookingDto paidBooking(String sessionId) throws CustomRuntimeException {
        List<Booking> bList = bookingRepo.findBySessionId(sessionId);

        if (bList == null || bList.isEmpty()) throw new CustomRuntimeException("No booking found");
        Booking b = bList.get(0);
        b.setPaid(true);
        Booking booking = bookingRepo.save(b);
        BookingDto dto = new BookingDto(booking.getId(), booking.getTour(), booking.getUser(),
                booking.getStartDate(), booking.getPriceOrigin(), booking.getPriceDiscount(),
                booking.getPriceFinal(), (Integer) booking.getNumPeopleJoined() == null || booking.getNumPeopleJoined() == 0 ? 1 : booking.getNumPeopleJoined(),
                booking.isPaid(), booking.getCreatedAt(), booking.isStatus(), booking.getSessionId(), booking.getCreationTime(),
                booking.getKeysOfStartDate(), booking.getStartDateId());
        return dto;
    }

    @Override
    public void cancelAllBookingsHaveRegex(String keysOfStartDate) {
        List<Booking> list = bookingRepo.findByKeysOfStartDate(keysOfStartDate);
        if (list != null && list.size() > 0) {
            bookingRepo.deleteAll(list);
        }
    }

    @Override
    public void changeAllRelatedBookingsAfterTourChanged(String tourId, String startDateGuide, String startDateLocation) throws CustomRuntimeException {
        List<Booking> list = bookingRepo.findByTourId(tourId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Tour tour = tourRepo.findById(tourId).orElse(null);
        if (tour == null) return;
        List<StartDate> listStartDates = startDateRepo.findByTourId(tourId);
        if (!listStartDates.isEmpty()) {


            list.stream().forEach(booking -> {
                StartDate startRequired = listStartDates.stream().filter(start -> start.getId().equals(booking.getStartDateId())).findAny().orElse(null);
                if (startRequired == null) return;
                // ap dung guide cho booking tu ngay startDateguide
                if (startDateGuide != null&&startDateGuide!="") {
                    LocalDate startDateGuideDate = LocalDate.parse(startDateGuide, formatter);
                    if (startRequired.getStartDate().isEqual(startDateGuideDate) || startRequired.getStartDate().isAfter(startDateGuideDate)) {
                        // get schedules cua startDate do
                        List<TourSchedule> schedules = tourScheduleRepo.findByStartDateId(startRequired.getId());
                        Set<ObjectId> guideList = schedules.stream().map(schedule -> new ObjectId(schedule.getGuideId())).collect(Collectors.toSet());
                        Tour tour1 = booking.getTour();
                        tour1.setGuides(guideList);
                        booking.setTour(tour1);
                        bookingRepo.save(booking);

                    }
                }
                if (startDateLocation != null&&startDateLocation!="") {
                    LocalDate startDateLocationDate = LocalDate.parse(startDateLocation, formatter);
                    if (startRequired.getStartDate().isEqual(startDateLocationDate) || startRequired.getStartDate().isAfter(startDateLocationDate)) {
                        Map<String, Object> locations = startRequired.getLocations();
                        if (locations == null) return;
                        Set<String> keys = locations.keySet();
                        Set<Location> locsAfter = new HashSet<>();
                        keys.stream().forEach(key -> {
                            Location o = (Location) locations.get(key);
                            Location loc = new Location();
                            loc.setDay(o.getDay());
                            loc.setAddress(o.getAddress());
                            loc.setCoordinates(o.getCoordinates());
                            loc.setDescription(o.getDescription());
                            loc.setType(o.getType());
                            locsAfter.add(loc);
                        });
                        Tour t = booking.getTour();
                        t.setLocations(locsAfter);
                        booking.setTour(t);
                        bookingRepo.save(booking);
                    }
                }


                //ap dung loction cho booking tu ngay startDateLocation
            });
        }


    }

    @Override
    public boolean checkingEnoughCapacityToMergeGuestsFromMultipleBookings(String prevKey, String nextKey, String tourId) throws CustomRuntimeException {
        List<Booking> list1 = bookingRepo.findByKeysOfStartDate(prevKey);
        List<Booking> list2 = bookingRepo.findByKeysOfStartDate(nextKey);
        Tour tour = tourRepo.findById(tourId).orElse(null);
        if (tour == null) throw new CustomRuntimeException("Tour not exist or has been deleted");
        int totalGuests = list1.stream().map(b -> {
            Integer num = b.getNumPeopleJoined();
            if (num == null || num == 0) {
                return 1;
            } else {
                return num;
            }
        }).reduce(0, Integer::sum) + list2.stream().map(b -> {
            Integer num = b.getNumPeopleJoined();
            if (num == null || num == 0) {
                return 1;
            } else {
                return num;
            }
        }).reduce(0, Integer::sum);
        if (totalGuests > tour.getMaxGroupSize()) return false;
        return true;
    }

    @Override
    public List<BookingDto> getAllRelatedUpcomingSameTour(String userId, String tourId) {
        List<Booking> list = bookingRepo.findByUserId(userId);
        List<BookingDto> listDto = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy");
        list = list.stream().filter(booking -> booking.getTourId().equals(tourId) && booking.isStatus()).collect(Collectors.toList());
        // loc upcoming booking
        list = list.stream().filter(booking -> LocalDate.parse(booking.getStartDate(), formatter).isAfter(LocalDate.now())).collect(Collectors.toList());
        if (list != null && !list.isEmpty()) {
            listDto = list.stream().map(booking -> new BookingDto(booking.getId(), booking.getTour(),
                    booking.getUser(), booking.getStartDate(), booking.getPriceOrigin(), booking.getPriceDiscount(),
                    booking.getPriceFinal(), (Integer) booking.getNumPeopleJoined() == null || booking.getNumPeopleJoined() == 0 ? 1 : booking.getNumPeopleJoined(), booking.isPaid(),
                    booking.getCreatedAt(), booking.isStatus(), booking.getSessionId(), booking.getCreationTime(), booking.getKeysOfStartDate(), booking.getStartDateId())).collect(Collectors.toList());
        }
        return listDto;
    }

    @Override
    public BookingDto changeBookingSessionId(String bookingId, String freshSessionId) throws CustomRuntimeException {
        Booking booking = bookingRepo.findById(bookingId).orElse(null);
        if(booking==null) throw new CustomRuntimeException("Booking invalid");
        List<Booking> existedSessionId = bookingRepo.findBySessionId(freshSessionId);
        if(existedSessionId!=null && !existedSessionId.isEmpty()) throw new CustomRuntimeException("Invalid session id");
        booking.setSessionId(freshSessionId);
        Booking result = bookingRepo.save(booking);
        BookingDto dto = new BookingDto(result.getId(), result.getTour(), result.getUser(),
                result.getStartDate(), result.getPriceOrigin(), result.getPriceDiscount(),
                result.getPriceFinal(), (Integer) result.getNumPeopleJoined() == null || result.getNumPeopleJoined() == 0 ? 1 : result.getNumPeopleJoined(),
                result.isPaid(), result.getCreatedAt(), result.isStatus(), result.getSessionId(), result.getCreationTime(),
                result.getKeysOfStartDate(), result.getStartDateId());
        return dto;
    }


    private boolean checkIfExistBooking(String user, String tour, Date date) {

        List<Booking> bookings = bookingRepo.findByTourId(tour);

        DateFormat originalFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
        boolean existed = bookings.stream().anyMatch(booking -> {
            try {
                Date bookingDate = originalFormat.parse(booking.getStartDate().toString());
                String formattedBookingDate = targetFormat.format(bookingDate);
                String formattedInputDate = targetFormat.format(date);
                return booking.getUserId().equals(user) && formattedBookingDate.equals(formattedInputDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return false;
        });
        return existed;
    }
}
