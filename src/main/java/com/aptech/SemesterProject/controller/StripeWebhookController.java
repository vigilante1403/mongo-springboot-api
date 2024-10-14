package com.aptech.SemesterProject.controller;

import com.aptech.SemesterProject.dto.BookingDto;
import com.aptech.SemesterProject.entity.*;
import com.aptech.SemesterProject.exception.CustomRuntimeException;
import com.aptech.SemesterProject.repo.*;
import com.aptech.SemesterProject.service.BookingService;
import com.aptech.SemesterProject.service.EmailService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/webhook/stripe")
public class StripeWebhookController {
    @Value("${stripe.secret.signing.key}")
    private String stripeSecretKey;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private StartDateRepo startDateRepo;
    @Autowired
    private TourScheduleRepo tourScheduleRepo;
    @Autowired
    private BillRepo billRepo;
    @Autowired
    private BookingRepo bookingRepo;
    @Autowired
    private UserRepo userRepo;
    @PostMapping
    public ResponseEntity<BookingDto> handleWebhook(@RequestBody String payload, @RequestHeader("stripe-signature") String sigHeader) throws CustomRuntimeException, ParseException, MessagingException, UnsupportedEncodingException {
        Event event;
        try{
            event = Webhook.constructEvent(payload,sigHeader,stripeSecretKey);
        }catch(SignatureVerificationException e){
            throw new CustomRuntimeException("Invalid signature");

        }
        BookingDto dto=new BookingDto();
        // handle event
        if("checkout.session.completed".equals(event.getType())){
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            System.out.println(session);
             dto = bookingService.paidBooking(session.getId());
            List<Bill> billList = billRepo.findByBookingId(dto.getId());
            Booking booking = bookingRepo.findById(dto.getId()).orElse(null);
            if(booking==null) throw new CustomRuntimeException("Booking not existed");
            if(billList.isEmpty()){
                Bill billAdd = new Bill();
                billAdd.setBookingId(dto.getId());
                billAdd.setPaidAt(LocalDateTime.now());
                billAdd.setUserEmail(dto.getUser().getEmail());
                billAdd.setBooking(booking);
                billRepo.save(billAdd);
            }
            StartDate startDate=startDateRepo.findById(dto.getStartDateId()).orElse(null);
            String duration;
            String guideListName = null;
            if(startDate!=null&&startDate.getLocations()!=null){
                duration = startDate.getLocations().size()+"-day trip";
                List<TourSchedule> tourScheduleList=tourScheduleRepo.findByStartDateId(startDate.getId());
                if(!tourScheduleList.isEmpty()){
                    List<User> userList = new ArrayList<>();
                    tourScheduleList.forEach(schedule->{
                        User u = userRepo.findById(schedule.getGuideId()).orElse(null);
                        if(u!=null){
                            userList.add(u);
                        }
                    });
                    if(userList.size()>0){
                        guideListName=userList.stream().map(user -> user.getFullName()+" - "+user.getEmail()).collect(Collectors.joining(" "));
                    }else{
                        guideListName="Admin will reannounce you later";
                    }

                }
            }else{
                duration = dto.getTour().getLocations().size()+"-day trip";
            }
            SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
            Date parsedDate = inputFormat.parse(dto.getStartDate());
            LocalDate localDate = parsedDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedDate = localDate.format(outputFormat);
            String userEmail = dto.getUser().getEmail();
            Map<String, Object> template = new HashMap<>();
            template.put("username", dto.getUser().getFullName());
            template.put("bookingId", dto.getId());
            template.put("tourName", dto.getTour().getName());
            template.put("duration", duration);
            template.put("startDate", formattedDate+" "+dto.getTour().getStartTime());
            template.put("ticketNum", dto.getNumJoin());
            template.put("guideListName", guideListName);
            try{
                emailService.sendMessageUsingThymeleafTemplate(userEmail, "Booking success", template, "booking_success.html");
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return new ResponseEntity<>(dto, HttpStatus.OK);

    }
}
