package com.aptech.SemesterProject.controller;

import com.aptech.SemesterProject.constant.RoleEnum;
import com.aptech.SemesterProject.dto.HttpResponse;
import com.aptech.SemesterProject.dto.ScheduleWorking;
import com.aptech.SemesterProject.dto.TourDto;
import com.aptech.SemesterProject.dto.TourWithCommentsDto;
import com.aptech.SemesterProject.entity.*;
import com.aptech.SemesterProject.exception.CustomRuntimeException;
import com.aptech.SemesterProject.repo.*;
import com.aptech.SemesterProject.service.*;
import com.aptech.SemesterProject.utility.StorageProperties;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;
import jakarta.mail.MessagingException;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/tours")
public class TourController {

    private TourService tourServiceImpl;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private TourRepo tourRepo;
    @Autowired
    private StartDateRepo startDateRepo;
    @Autowired
    private StorageProperties storageProperties;
    @Autowired
    private LocationRepo locationRepo;
    @Autowired
    private StorageService storageService;
    @Autowired
    private TourScheduleRepo tourScheduleRepo;

    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private Helper helper;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private BookingRepo bookingRepo;
    @Autowired
    private EmailService emailService;

    @Autowired
    public TourController(TourService tourServiceImpl) {
        this.tourServiceImpl = tourServiceImpl;
    }

    public static boolean checkTourName(String value) {
        if (value == null) {
            return false;
        }
        String NAME_PATTERN = "^[a-zA-Z0-9\\s]+$";
        Pattern pattern = Pattern.compile(NAME_PATTERN);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    @GetMapping
    public ResponseEntity<List<TourWithCommentsDto>> getAllTours() {

        return new ResponseEntity<>(tourServiceImpl.getAllTours(), HttpStatus.OK);
    }

    //    @PostMapping
//    public ResponseEntity<Tour> upsertTour(@RequestParam(name = "id", required = false) String id, @RequestParam(name = "name") String name, @RequestParam(name = "price") double price, @RequestParam(name = "maxGroupSize") int maxGroupSize, @RequestParam(name = "ratingsAverage", required = false) Double ratingsAverage, @RequestParam(name = "priceDiscount") double priceDiscount, @RequestParam(name = "summary") String summary, @RequestParam(name = "description") String description,@RequestParam(name="imageCoverCopy",required = false)String imageCoverCopy, @RequestParam(name = "imageCover", required = false) MultipartFile imageCover, @RequestParam(name = "images", required = false) MultipartFile[] images, @RequestParam(name = "imageNamesSubmit", required = false) String[] imageNamesSubmit, @RequestParam(name = "startDates",required = false) String[] startDates, @RequestParam(name = "lats",required = false) double[] lats, @RequestParam(name = "lngs",required = false) double[] lngs, @RequestParam(name = "addresses",required = false) String[] addresses, @RequestParam(name = "descriptions",required = false) String[] descriptions, @RequestParam(name = "days",required = false) int[] days, @RequestParam(name = "guides",required = false) String[] guides,@RequestParam(name="countryNameCommon")String countryNameCommon,@RequestParam(name="countryNameOfficial")String countryNameOfficial,@RequestParam(name="countryFlag")String countryFlag,@RequestParam(name="region")String region,@RequestParam(name="status")String status) throws CustomRuntimeException, IOException {
//        Tour result = null;
//        try {
//        if (!checkTourName(name)) throw new IllegalArgumentException("Tour name is not valid");
//        Tour tour = new Tour();
//        if (id==null||id.isEmpty()) {
//            validateTourName("", name);
//        } else {
//            tour = tourRepo.findById(id).orElse(null);
//            if (tour == null) throw new CustomRuntimeException("No tour with id: " + id + " found!");
//            validateTourName(tour.getName(), name);
//        }
//        // set slug
//        String[] slugs = name.toLowerCase().split(" ");
//        String slug = String.join("-", slugs);
//        tour.setSlug(slug);
//        tour.setName(name);
//        tour.setPrice(price);
//        tour.setMaxGroupSize(maxGroupSize);
//        tour.setCountryNameCommon(countryNameCommon);
//        tour.setCountryNameOfficial(countryNameOfficial);
//        tour.setCountryFlag(countryFlag);
//        tour.setRegion(region);
//        tour.setStatus(status);
//        if(ratingsAverage!=null){
//            tour.setRatingsAverage(ratingsAverage);
//        }else{
//            tour.setRatingsAverage(0);
//        }
//
//        tour.setPriceDiscount(priceDiscount);
//        tour.setSummary(summary);
//        tour.setDescription(description);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        if(startDates!=null){
//            try {
//                Set<LocalDate> newStartDates = Arrays.stream(startDates)
//                        .map(date -> LocalDate.parse(date, formatter))
//                        .collect(Collectors.toSet());
//                tour.setStartDates(newStartDates);
//            } catch (DateTimeParseException e) {
//                throw new CustomRuntimeException("Invalid date format in startDates");
//            }
//        }
//
//        if(lats!=null && lngs!=null){
//            Set<Location> locations = new HashSet<>();
//            for (var i = 0; i < lats.length; i++) {
//                Location loc = new Location(lngs[i], lats[i], addresses[i], descriptions[i], days[i]);
//                Location result1 = locationRepo.save(loc);
//                locations.add(result1);
//            }
//            tour.setLocations(locations);
//        }
//            if(imageCoverCopy!=null) {
//                tour.setImageCover(imageCoverCopy);
//            }
////        if ((id == null || StringUtils.isBlank(id)) && imageCover.getSize() == 0) {
////            throw new CustomRuntimeException("ImageCover is empty");
////        }
//            if (imageCover!=null&&imageCover.getSize() > 0) {
//                String imageCoverDir = storageService.store(storageProperties.getLocation(),"tour", imageCover);
//                if ((imageCoverDir == null || imageCoverDir.length() == 0) && (id == null || StringUtils.isBlank(id))) {
//                    throw new CustomRuntimeException("Error when saving imageCover to server");
//                }
//                if (id != null && imageCoverDir.length() > 0) {
//                    String imageCoverOld = tour.getImageCover();
//                    if (imageCoverOld != null && imageCoverOld.length() > 0) {
//                        storageService.deleteFile(imageCoverOld,"tour");
//                    }
//                    tour.setImageCover(imageCoverDir);
//                } else if (id == null) {
//                    tour.setImageCover(imageCoverDir);
//                }
//            }
//            if (id != null&&tour.getImages()!=null) {
//                List<String> imageNames = tour.getImages().stream().toList();
//                if (imageNamesSubmit.length > 0) {
//                    List<String> imagesSubmit = new ArrayList<>();
//                    imagesSubmit.addAll(List.of(imageNamesSubmit));
//                    for (String image : imageNames) {
//                        if (!imagesSubmit.contains(image)) {
//                            storageService.deleteFile(image,"tour");
//                        }
//                    }
//                }
//
//            }
//            if (images!=null&&images.length>0) {
//                System.out.println("Saving images");
//                Set<String> tourImages = Arrays.stream(images)
//                        .filter(image -> !image.isEmpty())
//                        .map(image -> storageService.store(storageProperties.getLocation(),"tour", image))
//                        .collect(Collectors.toSet());
//                if (id != null&&id.length()>0&&!id.isEmpty()) {
//                    List<String> imagesSubmit = Arrays.stream(imageNamesSubmit).toList();
//
//                    tourImages.stream().forEach(image -> {
//                        imagesSubmit.add(image);
//                    });
//                    tour.setImages(imagesSubmit.stream().collect(Collectors.toSet()));
//
//                } else {
//                    tour.setImages(tourImages);
//                }
//            }
//        if(guides!=null&&guides.length>0){
//            Set<ObjectId> guidesId = new HashSet<>();
//            Arrays.stream(guides).forEach(guide -> {
//                User tourguide = userRepo.findById(guide).orElse(null);
//                if (tourguide == null) {
//                    throw new IllegalArgumentException("Invalid guides id");
//                }
//                if (!Arrays.asList(RoleEnum.GUIDE, RoleEnum.LEADGUIDE).contains(tourguide.getRole())) {
//                    throw new IllegalArgumentException("User isn't guide");
//                }
//                guidesId.add(new ObjectId(guide));
//            });
//            tour.setGuides(guidesId);
//        }
//
//            result = tourServiceImpl.addNewTour(tour);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new CustomRuntimeException("Cannot save tour");
//        }
//        return new ResponseEntity<>(result, HttpStatus.OK);
//    }
    @PostMapping
    public ResponseEntity<Tour> upsertTour(@RequestParam(name = "id", required = false) String id, @RequestParam(name = "name") String name, @RequestParam(name = "price") double price, @RequestParam(name = "maxGroupSize") int maxGroupSize, @RequestParam(name = "ratingsAverage", required = false) Double ratingsAverage, @RequestParam(name = "priceDiscount") double priceDiscount, @RequestParam(name = "summary") String summary, @RequestParam(name = "description") String description, @RequestParam(name = "imageCoverCopy", required = false) String imageCoverCopy,
                                           @RequestParam(name = "imageCover", required = false) MultipartFile imageCover, @RequestParam(name = "images", required = false) MultipartFile[] images, @RequestParam(name = "imageNamesSubmit", required = false) String[] imageNamesSubmit, @RequestParam(name = "startDates", required = false) String[] startDates,
                                           @RequestParam(name = "lats", required = false) double[] lats, @RequestParam(name = "lngs", required = false) double[] lngs, @RequestParam(name = "addresses", required = false) String[] addresses, @RequestParam(name = "descriptions", required = false) String[] descriptions, @RequestParam(name = "days", required = false) int[] days, @RequestParam(name = "guides", required = false) String[] guides,
                                           @RequestParam(name = "countryNameCommon") String countryNameCommon, @RequestParam(name = "countryNameOfficial") String countryNameOfficial, @RequestParam(name = "countryFlag") String countryFlag, @RequestParam(name = "region") String region, @RequestParam(name = "status") String status,
                                           @RequestParam(name = "startTime") String startTime, @RequestParam(name = "dateOfGuideAfter", required = false) String dateOfGuideAfter,
                                           @RequestParam(name = "dateOfLocationAfter", required = false) String dateOfLocationAfter

    ) throws CustomRuntimeException, IOException {
        Tour result = null;
        try {
            if (!checkTourName(name)) throw new IllegalArgumentException("Tour name is not valid");
            Tour tour = new Tour();
            if (id == null || id.isEmpty()) {
                validateTourName("", name);
            } else {
                tour = tourRepo.findById(id).orElse(null);
                if (tour == null) throw new CustomRuntimeException("No tour with id: " + id + " found!");
                validateTourName(tour.getName(), name);
            }
            // set slug
            String[] slugs = name.toLowerCase().split(" ");
            String slug = String.join("-", slugs);
            tour.setSlug(slug);
            tour.setName(name);
            tour.setPrice(price);
            tour.setMaxGroupSize(maxGroupSize);
            tour.setCountryNameCommon(countryNameCommon);
            tour.setCountryNameOfficial(countryNameOfficial);
            tour.setCountryFlag(countryFlag);
            tour.setRegion(region);
            tour.setStatus(status);
            tour.setStartTime(startTime);
            if (ratingsAverage != null) {
                tour.setRatingsAverage(ratingsAverage);
            } else {
                tour.setRatingsAverage(0);
            }

            tour.setPriceDiscount(priceDiscount);
            tour.setSummary(summary);
            tour.setDescription(description);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            if (startDates != null) {
                try {
                    Set<LocalDate> newStartDates = Arrays.stream(startDates)
                            .map(date -> LocalDate.parse(date, formatter))
                            .collect(Collectors.toSet());
                    if (id == null) {//create new tour
                        tour.setStartDates(newStartDates);
                        Map<String, LocalDate> keysAndStartDates = new HashMap<>();
                        newStartDates.stream().forEach(startDate -> {
                            String random = UUID.randomUUID().toString().substring(0, 15);
                            random = helper.checkIfExistKeyAndRepeat(random, keysAndStartDates);
                            keysAndStartDates.put(random, startDate);
                        });
                        tour.setKeyOfDatesRelation(keysAndStartDates);
                    }
//                    } else {
//                        Tour finalTour = tour;
//                        Map<String, LocalDate> keysAndStartDates = finalTour.getKeyOfDatesRelation();
//                        if (mapOfKeysAndStartDates == null || mapOfKeysAndStartDates.length == 0) {
////                            throw new CustomRuntimeException("Missing mapOfKeysAndStartDates field");
//                            Map<String, LocalDate> keysAndStartDatesNew = new HashMap<>();
//                            newStartDates.stream().forEach(startDate -> {
//                                String random = UUID.randomUUID().toString().substring(0, 15);
////                                random = helper.checkIfExistKeyAndRepeat(random, keysAndStartDates);
//                                keysAndStartDatesNew.put(random, startDate);
//                            });
//                            tour.setKeyOfDatesRelation(keysAndStartDatesNew);
//                        } else {
//                            Arrays.stream(mapOfKeysAndStartDates).forEach(pair -> {
//                                //c1:huy date nao do submit key(that date random string):null
//                                //c2: doi sang ngay khac chua ton tai key(that date random string):new startDate
//                                //c3: doi sang ngay to chuc lan sau da ton tai key(that date random string):key(existing key of another date)
//                                //c4: startDate moi chua ton tai key
//                                //c5: giu nguyen
//                                List<String> splitted1 = List.of(pair.split(":"));
//                                //c1
//                                if (pair.contains("null")) {//date nay bi huy
//                                    List<String> splitted = List.of(pair.split(":"));
//                                    keysAndStartDates.replace(splitted.get(0), null);
//                                    //apply all booking has this splitted.get(0) key start Date to be unidentified and status->false
//
//                                } else {
//                                    try {
//
//                                        System.out.println(JSON.parse(splitted1.get(1)));
//                                        LocalDate parseSomeDate = LocalDate.parse(JSON.parse(splitted1.get(1)).toString(), formatter);
//                                        System.out.println(parseSomeDate);
//                                        //c2&&c5
//                                        if (keysAndStartDates.containsKey(splitted1.get(0))) {
//                                            LocalDate date = LocalDate.parse(JSON.parse(splitted1.get(1)).toString(), formatter);
//
//                                            keysAndStartDates.replace(JSON.parse(splitted1.get(0)).toString(), date);
//                                        } else {
//                                            //c4
//                                            LocalDate date = LocalDate.parse(JSON.parse(splitted1.get(1)).toString(), formatter);
//
//                                            keysAndStartDates.put(JSON.parse(splitted1.get(0)).toString(), date);
//                                        }
//                                    } catch (Exception e) {
//                                        //c3 error when parsing date because it's key not formatted date
//                                        if (!keysAndStartDates.containsKey(JSON.parse(splitted1.get(1)).toString())) {
//                                            try {
//                                                throw new CustomRuntimeException("Cannot get key: " + splitted1.get(1));
//                                            } catch (CustomRuntimeException ex) {
//                                                ex.printStackTrace();
//                                            }
//                                        }
//                                        try {
//                                            if (bookingService.checkingEnoughCapacityToMergeGuestsFromMultipleBookings(splitted1.get(0), splitted1.get(1), id))
//                                                throw new CustomRuntimeException("Tour does not have enough capacity to merge customers from dates");
//                                        } catch (CustomRuntimeException ex) {
//                                            ex.printStackTrace();
//                                        }
//                                        LocalDate dateGet = keysAndStartDates.get(JSON.parse(splitted1.get(1)).toString());
//
//                                        keysAndStartDates.replace(JSON.parse(splitted1.get(0)).toString(), dateGet);
//                                    }
//                                }
//                            });
//                            System.out.println(keysAndStartDates);
//                            tour.setKeyOfDatesRelation(keysAndStartDates);
//                        }


                } catch (DateTimeParseException e) {
                    throw new CustomRuntimeException("Invalid date format in startDates");
                }
            }
            Set<Location> locations = new HashSet<>();
            if (lats != null && lngs != null) {
                for (var i = 0; i < lats.length; i++) {
                    Location loc = new Location(lngs[i], lats[i], addresses[i], descriptions[i], days[i]);
                    Location result1 = locationRepo.save(loc);
                    locations.add(result1);
                }
                tour.setLocations(locations);
            }
            if (imageCoverCopy != null) {
                tour.setImageCover(imageCoverCopy);
            }
            if (imageCover != null && imageCover.getSize() > 0) {
                String imageCoverDir = storageService.store(storageProperties.getLocation(), "tour", imageCover);
                if ((imageCoverDir == null || imageCoverDir.length() == 0) && (id == null || StringUtils.isBlank(id))) {
                    throw new CustomRuntimeException("Error when saving imageCover to server");
                }
                if (id != null && imageCoverDir.length() > 0) {
                    String imageCoverOld = tour.getImageCover();
                    if (imageCoverOld != null && imageCoverOld.length() > 0) {
                        storageService.deleteFile(imageCoverOld, "tour");
                    }
                    tour.setImageCover(imageCoverDir);
                } else if (id == null) {
                    tour.setImageCover(imageCoverDir);
                }
            }

            if (images != null && images.length > 0) {
                Set<MultipartFile> newImages = Arrays.stream(images)
                        .filter(image -> !image.isEmpty())
                        .collect(Collectors.toSet());

                Set<String> existingImages = tour.getImages() != null ? tour.getImages() : Collections.emptySet();
                if (!existingImages.isEmpty()) {
                    existingImages.stream()

                            .forEach(image -> {

                                Path file = storageService.load(image, "tour");
                                if (Files.exists(file)) {
                                    try {
                                        storageService.deleteFile(image, "tour");
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            });
                }
                Set<String> storedImages = newImages.stream()
                        .map(image -> storageService.store(storageProperties.getLocation(), "tour", image))
                        .collect(Collectors.toSet());
                tour.setImages(storedImages);
            }
//            else {
//                if (tour.getImages() != null) {
//                    tour.getImages().forEach(image -> {
//                        Path file = storageService.load(image, "tour");
//                        if (Files.exists(file)) {
//                            try {
//                                storageService.deleteFile(image, "tour");
//                            } catch (IOException e) {
//                                throw new RuntimeException(e);
//                            }
//                        }
//                    });
//                }
//                tour.setImages(Collections.emptySet());
//            }
            Set<ObjectId> guidesId = new HashSet<>();
            if (guides != null && guides.length > 0 && id == null) {

                Arrays.stream(guides).forEach(guide -> {
                    User tourguide = userRepo.findById(guide).orElse(null);
                    if (tourguide == null) {
                        throw new IllegalArgumentException("Invalid guides id");
                    }
                    if (!Arrays.asList(RoleEnum.GUIDE, RoleEnum.LEADGUIDE).contains(tourguide.getRole())) {
                        throw new IllegalArgumentException("User isn't guide");
                    }
                    guidesId.add(new ObjectId(guide));
                });
                tour.setGuides(guidesId);
            }

            result = tourServiceImpl.addNewTour(tour);
            // trong truong hop co  chinh sua location
            // ap dung cho cac start date ve sau tinh tu thoi gian submit form
            List<StartDate> list = startDateRepo.findByTourId(result.getId());

            if (dateOfLocationAfter != null&&dateOfLocationAfter.length()>0) {
                LocalDate dateLocation = LocalDate.parse(dateOfLocationAfter, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                if (list != null && !list.isEmpty()) {
                    list = list.stream().filter(start -> start.getStartDate().isAfter(dateLocation)).collect(Collectors.toList());
                    if (!list.isEmpty()) {
                        Tour finalResult1 = result;
                        list.stream().forEach(startDate -> {
                            int duration = finalResult1.getLocations().size();
                            LocalDate endDate = startDate.getStartDate().plusDays(duration - 1);
                            startDate.setEndDate(endDate);
                        });

                        Map<String, Object> locs = new HashMap<>();
                        result.getLocations().stream().forEach(location -> {
                            Location loc = new Location();
                            loc.setAddress(location.getAddress());
                            loc.setCoordinates(location.getCoordinates());
                            loc.setDescription(location.getDescription());
                            loc.setDay(location.getDay());
                            locs.put(location.getDay() + "", loc);
                        });
                        list.stream().forEach(start -> start.setLocations(locs));
                        startDateRepo.saveAll(list);

                    }
                }
            }
            if (dateOfGuideAfter != null && dateOfGuideAfter.length()>0) {
                LocalDate dateGuide = LocalDate.parse(dateOfGuideAfter, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                // chinh sua guide ap dung cho tu sau ngay dateGuide
                List<StartDate> list1 = startDateRepo.findByTourId(result.getId());
                if (list1 != null && !list1.isEmpty()) {
                    list1 = list1.stream().filter(start -> start.getStartDate().isAfter(dateGuide)).collect(Collectors.toList());
                    if (!list1.isEmpty()) {
                        list1.stream().forEach(startDate -> {
                            List<TourSchedule> schedules = tourScheduleRepo.findByStartDateId(startDate.getId());
                            List<User> users = userRepo.findAll();
                            if (schedules.isEmpty() || schedules.size() < guides.length || schedules.size() > guides.length) {
                                if (!schedules.isEmpty() && (schedules.size() < guides.length || schedules.size() > guides.length)) {
                                    // erase all existing ones
                                    tourScheduleRepo.deleteAll(schedules);
                                }
                                Arrays.stream(guides).forEach(guide -> {
                                    TourSchedule schedule = new TourSchedule();
                                    User guide1 = users.stream().filter(g -> g.getId().equals(guide) && (g.getRole().equals(RoleEnum.GUIDE) || g.getRole().equals(RoleEnum.LEADGUIDE))).findFirst().orElse(null);
                                    if (guide1 == null) try {
                                        throw new CustomRuntimeException("Input guide Id invalid");
                                    } catch (CustomRuntimeException e) {
                                        e.printStackTrace();
                                    }
                                    schedule.setStartDate(startDate.getStartDate());
                                    int day = startDate.getLocations().size();
                                    schedule.setEndDate(startDate.getEndDate());
                                    schedule.setTourId(startDate.getTourId());
                                    schedule.setGuideId(guide);
                                    schedule.setStartDateId(startDate.getId());
                                    tourScheduleRepo.save(schedule);
                                });
                            } else {
                                for (int i = 0; i < guides.length; i++) {
                                    TourSchedule schedule = schedules.get(i);
                                    User guide = userRepo.findById(guides[i]).orElse(null);
                                    if (guide == null || guide.getRole().equals(RoleEnum.ADMIN) || guide.getRole().equals(RoleEnum.USER)) {
                                        try {
                                            throw new CustomRuntimeException("Guide id invalid");
                                        } catch (CustomRuntimeException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    schedule.setGuideId(guide.getId());
                                    tourScheduleRepo.save(schedule);
                                }
                            }
                        });

                    }
                }
            }

            if (id == null) {
                List<StartDate> startDateList = new ArrayList<>();
                List<LocalDate> newStartDates = Arrays.stream(startDates)
                        .map(date -> LocalDate.parse(date, formatter))
                        .collect(Collectors.toList());
                Tour finalResult = result;
                int duration = result.getLocations() != null ? result.getLocations().size() : 1;
                try {
                    newStartDates.stream().forEach(start -> {
                        StartDate date = new StartDate();
                        date.setTourId(finalResult.getId());
                        date.setStartDate(start);
                        LocalDate end = start.plusDays(duration - 1);
                        date.setEndDate(end);
                        Map<String, Object> locations1 = new HashMap<>();
                        if (finalResult.getLocations() != null && !finalResult.getLocations().isEmpty()) {
                            finalResult.getLocations().stream().forEach(location -> {
                                Location loc = new Location();
                                loc.setAddress(location.getAddress());
                                loc.setCoordinates(location.getCoordinates());
                                loc.setDescription(location.getDescription());
                                loc.setDay(location.getDay());
                                locations1.put(location.getDay() + "", loc);
                            });
                            date.setLocations(locations1);
                        }
                        date.setStatus(true);
                        StartDate res1 = startDateRepo.save(date);
                        startDateList.add(res1);

                    });
                    // sau khi co list type StartDate
                    startDateList.stream().forEach(startDate -> {

                        if (finalResult != null) {
                            if (finalResult.getGuides() != null) {
                                finalResult.getGuides().stream().forEach(guide -> {
                                    TourSchedule schedule = new TourSchedule();
                                    schedule.setTourId(startDate.getTourId());
                                    schedule.setStartDate(startDate.getStartDate());
                                    schedule.setGuideId(guide.toString());
                                    schedule.setEndDate(startDate.getEndDate());
                                    schedule.setStartDateId(startDate.getId());
                                    tourScheduleRepo.save(schedule);
                                });
                            }

                        }

                    });
                } catch (Exception e) {
                    throw new CustomRuntimeException("Error when creating documents for StartDate and Schedule");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomRuntimeException("Cannot save tour");
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/search/{keywords}")
    public ResponseEntity<Collection<Tour>> getToursWithKeywords(@PathVariable String keywords) {
        return new ResponseEntity<>(tourServiceImpl.toursOnKeywords(keywords), HttpStatus.OK);
    }

    private Tour validateTourName(String currentTourName, String newTourName) throws CustomRuntimeException {
        List<Tour> tours = tourRepo.findByName(newTourName);

        if (StringUtils.isNotBlank(currentTourName)) {
            // Update logic
            System.out.println("Running1");
            List<Tour> currentTours = tourRepo.findByName(currentTourName);
            System.out.println("Running2");

            if (currentTours == null || currentTours.isEmpty()) {
                System.out.println("Running3");
                throw new CustomRuntimeException("No tour found by name " + currentTourName);
            }

            // Check if `tours` list is empty
            if (tours == null || tours.isEmpty()) {
                System.out.println("Running4");
                return currentTours.get(0);
            } else if (!currentTours.get(0).getId().equals(tours.get(0).getId())) {
                System.out.println("Running5");
                throw new CustomRuntimeException("Tour name already exists");
            }

            return currentTours.get(0);
        } else {
            // Create new logic
            if (tours != null && !tours.isEmpty()) {
                throw new CustomRuntimeException("Tour name already exists");
            }
            return null;
        }
    }
    @DeleteMapping("/tour/hidden-temp")
    public ResponseEntity deleteTourTemp(@RequestParam(name="tourId")String tourId) throws CustomRuntimeException {
        Tour tour = tourRepo.findById(tourId).orElse(null);
        if(tour==null) throw new CustomRuntimeException("Cannot find this tour");
        tour.setStatus("in-active");
        tourRepo.save(tour);
        return new ResponseEntity("temp deleted",HttpStatus.NO_CONTENT);
    }
    @DeleteMapping
    public ResponseEntity deleteTour(@RequestParam(name = "id") String id) throws CustomRuntimeException, IOException {
        try {
            Tour tour = tourRepo.findById(id).orElse(null);
            if (tour == null) throw new CustomRuntimeException("No tour with id: " + id + " found");
            Set<String> images = tour.getImages();
            String imageCover = tour.getImageCover();
            if (imageCover != null && imageCover.length() > 0) {
                try {
                    Path file = storageService.load(imageCover, "tour");
                    if (file != null) {
                        storageService.deleteFile(imageCover, "tour");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            if (images != null && images.size() > 0) {
                images.stream().forEach(image -> {
                    if (image != null && image.length() > 0) {
                        try {
                            Path file = storageService.load(image, "tour");
                            if (file != null) {
                                storageService.deleteFile(image, "tour");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            tourServiceImpl.deleteTour(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomRuntimeException("Cannot delete tour");
        }


        // check and delete images and imageCover

    }

    @GetMapping("/{id}")
    public ResponseEntity<TourWithCommentsDto> getTourById(@PathVariable String id) throws CustomRuntimeException {
        TourWithCommentsDto tour = tourServiceImpl.getTourByIdOrSlug(id, null);
        return new ResponseEntity<>(tour, HttpStatus.OK);
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<TourWithCommentsDto> getTourBySlug(@PathVariable String slug) throws CustomRuntimeException {
        TourWithCommentsDto tour = tourServiceImpl.getTourByIdOrSlug(null, slug);
        return new ResponseEntity<>(tour, HttpStatus.OK);
    }

    @PostMapping("/tourNearByMe")
    public ResponseEntity<FindIterable<Document>> getToursNearByWithGivenLocation(@RequestParam(name = "longitude") double longitude, @RequestParam(name = "latitude") double latitude, double maxDistanceInKm, double minDistanceInKm) {
        Point point = new Point(new Position(longitude, latitude));
        FindIterable<Document> result = tourServiceImpl.findToursNearByGivingPosition(point, minDistanceInKm, maxDistanceInKm);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/tourInCircleAria")
    public ResponseEntity<FindIterable<Document>> getToursNearInSphereWithGivenLocation(@RequestParam(name = "longitude") double longitude, @RequestParam(name = "latitude") double latitude, double radiusInKm) {
        FindIterable<Document> result = tourServiceImpl.findNearByLocationWithinCircleSphere(longitude, latitude, radiusInKm);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/toursInSelectedRange")
    public ResponseEntity<List<Tour>> getToursNearByWithGivenTime(@RequestParam(name = "date") Date dateChosen, @RequestParam(name = "period") long period) {
        List<Tour> list = tourServiceImpl.findToursWithinGivenTime(dateChosen.toString(), period);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

//    private List<TourSchedule> createSchedule(String tourId, String[] startDates, long totalDays,
//                                              String[] guides) throws CustomRuntimeException {
//        List<TourSchedule> list = new ArrayList<>();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        Arrays.stream(startDates).forEach(startDate -> {
//            Arrays.stream(guides).forEach(guide -> {
//                TourSchedule schedule = new TourSchedule();
//                schedule.setTourId(new ObjectId(tourId));
//                schedule.setGuideId(new ObjectId(guide));
//                schedule.setStartDate(startDate);
//                LocalDate date = LocalDate.parse(startDate, formatter);
//                LocalDate endDate = date.plusDays(totalDays);
//                schedule.setEndDate(endDate.toString());//o:oo:oo
//                long startDateEpoch = date.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli() + (7 * 60 * 60 * 1000);
//                long endDateEpoch = endDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli() + (7 * 60 * 60 * 1000);
//                schedule.setStartDateByMillisEpoch(startDateEpoch);
//                schedule.setEndDateByMillisEpoch(endDateEpoch);
//                try {
//                    TourSchedule result = tourScheduleRepo.save(schedule);
//                    list.add(result);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    try {
//                        throw new CustomRuntimeException("Cannot add schedule for guideId: " + guide + " with tourId: " + tourId + " in date: " + startDate);
//                    } catch (CustomRuntimeException ex) {
//                        ex.printStackTrace();
//                    }
//                }
//            });
//        });
//        return list;
//    }
//
//    @PostMapping("/tour/schedule/create")//tour moi
//    public ResponseEntity<List<TourSchedule>> createScheduleApi(@RequestParam(name = "tourId") String tourId, @RequestParam(name = "startDates") String[] startDates, @RequestParam(name = "totalDays") long totalDays,
//                                                                @RequestParam(name = "guides", required = false) String[] guides) throws CustomRuntimeException {
//
//        List<TourSchedule> listExisted = tourScheduleRepo.findByTourId(new ObjectId(tourId));
//        List<TourSchedule> list;
//        if (listExisted == null || listExisted.isEmpty()) {
//            list = createSchedule(tourId, startDates, totalDays, guides);
//        } else {
//            throw new CustomRuntimeException("Please delete existing ones before create new schedule");
//
//        }
//        if (list.isEmpty()) return new ResponseEntity<>(list, HttpStatus.EXPECTATION_FAILED);
//        return new ResponseEntity<>(list, HttpStatus.OK);
//    }

    @PostMapping("/add/startDate")
    public ResponseEntity<Tour> addStartDatesTour(@RequestParam(name = "startDates") String startDates, @RequestParam(name = "tourId") String tourId, @RequestParam(name = "guides") String[] guides) throws CustomRuntimeException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Tour tour = tourRepo.findById(tourId).orElse(null);
        System.out.println("Running");
        if (tour == null) throw new CustomRuntimeException("Tour has been deleted or not exist");
        TourDto dto;
        //test guideId
        List<User> users = userRepo.findAll();
        List<User> guideList = new ArrayList<>();
        Arrays.stream(guides).forEach(guide -> {
            User guide1 = users.stream().filter(g -> g.getId().equals(guide) && Arrays.asList(RoleEnum.GUIDE, RoleEnum.LEADGUIDE).contains(g.getRole())).findFirst().orElse(null);
            if (guide1 == null) try {
                throw new CustomRuntimeException("Guide id input invalid");
            } catch (CustomRuntimeException e) {
                e.printStackTrace();
            }
            guideList.add(guide1);
        });
        if (tour.getLocations() == null) {
            throw new CustomRuntimeException("Please add location before add up startDates");
        }
        System.out.println("running2");
        Integer days = tour.getLocations().size();
        System.out.println(days);
        if (days == null) days = 1;
        LocalDate newStartDates = LocalDate.parse(startDates, formatter);
        System.out.println(newStartDates);
        List<LocalDate> temp1 = tour.getStartDates().stream().toList();
        List<LocalDate> temp = new ArrayList<>();

        temp1.stream().forEach(date -> {
            temp.add(date);
        });
        temp.add(newStartDates);
        System.out.println(temp);
        System.out.println("list");
        try {
            temp.add(newStartDates);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(temp);
//        Integer finalDays = days;
        System.out.println("testing");
//        temp.stream().forEach(start -> {
//            LocalDate end = start.plusDays(finalDays - 1);
//            if (newStartDates.isEqual(end)) try {
//                throw new CustomRuntimeException("Previous end date conflict with new start Date");
//            } catch (CustomRuntimeException e) {
//                e.printStackTrace();
//            }
//        });
        System.out.println("running3");
        tour.setStartDates(temp.stream().collect(Collectors.toSet()));
        Tour result = tourRepo.save(tour);
        List<StartDate> newly = new ArrayList<>();
        Integer finalDays1 = days;

        LocalDate end = newStartDates.plusDays(finalDays1 - 1);
        StartDate date = new StartDate();
        date.setStartDate(newStartDates);
        date.setEndDate(end);
        date.setTourId(result.getId());
        Map<String, Object> locations = new HashMap<>();
        if (tour.getLocations() != null && !tour.getLocations().isEmpty()) {
            tour.getLocations().stream().forEach(location -> {
                Location loc = new Location();
                loc.setAddress(location.getAddress());
                loc.setCoordinates(location.getCoordinates());
                loc.setDescription(location.getDescription());
                loc.setDay(location.getDay());
                locations.put(location.getDay() + "", loc);
            });
            date.setLocations(locations);
        }
        date.setStatus(true);
        StartDate res1 = startDateRepo.save(date);
        newly.add(res1);

        System.out.println("running4");
        newly.stream().forEach(startDate -> {
            if (result != null) {

                guideList.stream().forEach(guide -> {
                    TourSchedule schedule = new TourSchedule();
                    schedule.setTourId(startDate.getTourId());
                    schedule.setStartDate(startDate.getStartDate());
                    schedule.setGuideId(guide.getId());
                    schedule.setEndDate(startDate.getEndDate());
                    schedule.setStartDateId(startDate.getId());
                    tourScheduleRepo.save(schedule);
                });
            }
        });
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/test/set")
    public ResponseEntity setAllStatus() {
        List<StartDate> list = startDateRepo.findAll();
        list.stream().forEach(start -> start.setStatus(true));
        startDateRepo.saveAll(list);
        return new ResponseEntity<>(startDateRepo.findAll(), HttpStatus.OK);
    }

    @PostMapping("/cancel/startDate")
    public ResponseEntity cancelAStartDate(@RequestParam(name = "startId") String startId) throws CustomRuntimeException, MessagingException, UnsupportedEncodingException {
        StartDate date = startDateRepo.findById(startId).orElse(null);
        if (date == null) throw new CustomRuntimeException("Date isn't listed in tour start Date");
        date.setStatus(false);
        startDateRepo.save(date);
        String tourName = "";
        Tour tour = tourRepo.findById(date.getTourId()).orElse(null);
        if (tour != null) {
            tourName = tour.getName();
        }
        List<Booking> bookingList = bookingRepo.findByStartDateId(startId);
        if (bookingList != null && !bookingList.isEmpty()) {
            List<String> bccRecipients = new ArrayList<>();
            bookingList.stream().forEach(booking -> {
                bccRecipients.add(booking.getUser().getEmail());
                booking.setStatus(false);
            });
            bookingRepo.saveAll(bookingList);
            String dateTime = bookingList.get(0).getStartDate();
            Map<String, Object> template = new HashMap<>();
            template.put("recipientName", "User email");
            template.put("tourName", tourName);
            template.put("dateTime", dateTime);
            template.put("senderName", "Vy Truong");
            try {
                emailService.sendMessageUsingThymeleafTemplateToMultiple("m61uoyni4@mozmail.com", "Your booking has been canceled", template, "cancelBooking.html", bccRecipients);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return new ResponseEntity("all set", HttpStatus.OK);
    }

    @GetMapping("/test/addLocationsForStartDate")
    public ResponseEntity addLocationsAllStartDates() {
        List<StartDate> startDateList = startDateRepo.findAll();
        if (startDateList != null && !startDateList.isEmpty()) {
            startDateList.stream().forEach(startDate -> {
                Tour tourFind = tourRepo.findById(startDate.getTourId()).orElse(null);
                if (tourFind != null) {
                    if (tourFind.getLocations() != null && !tourFind.getLocations().isEmpty()) {
                        Map<String, Object> locations = new HashMap<>();
                        tourFind.getLocations().stream().forEach(location -> {
                            Location a = new Location();
                            a.setDescription(location.getDescription());
                            a.setAddress(location.getAddress());
                            a.setCoordinates(location.getCoordinates());
                            a.setDay(location.getDay());
                            locations.put(location.getDay() + "", a);
                        });
                        startDate.setLocations(locations);
                    }
                }
            });
            startDateRepo.saveAll(startDateList);
        }
        return new ResponseEntity("all set", HttpStatus.OK);
    }

    @PostMapping("/edit/startDate") //chua add truowng hop chinh sua location dac biet cho ngay do
    public ResponseEntity editStartDatesOfTour(@RequestParam(name = "startDate", required = false) String startDate, @RequestParam(name = "tourId", required = false) String tourId, @RequestParam(name = "startId") String startId, @RequestParam(name = "guides", required = false) String[] guides) throws CustomRuntimeException, ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Tour tour = tourRepo.findById(tourId).orElse(null);
        if (tour == null) throw new CustomRuntimeException("Tour has been deleted or not exist");
        TourDto dto;
        if (tour.getLocations() == null) {
            throw new CustomRuntimeException("Please add location before add up startDates");
        }
        // case change ngay
        int days = tour.getLocations().size();
        LocalDate newStartDate = LocalDate.parse(startDate, formatter);
        StartDate dateFind = startDateRepo.findById(startId).orElse(null);
        if (dateFind == null) throw new CustomRuntimeException("startId not exist");
        LocalDate endDate = newStartDate.plusDays(days - 1);
        StartDate exist = startDateRepo.findByStartDate(endDate).stream().findFirst().orElse(null);
        if (exist != null && !exist.getId().equals(dateFind.getId()) && exist.getTourId().equals(dateFind.getTourId()))
            throw new CustomRuntimeException("Conflict of end date:" + endDate + " with another startDate of same tour");
        dateFind.setStartDate(newStartDate);
        dateFind.setEndDate(endDate);
        StartDate result = startDateRepo.save(dateFind);
        // change ngay dan den change booking start Date dua vao ma startId
        List<Booking> bookingList = bookingRepo.findByStartDateId(result.getId());
        if (!bookingList.isEmpty()) {
            DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy");
            LocalDate date = result.getStartDate();
            LocalDateTime localDateTime = date.atStartOfDay();
            ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Bangkok"));
            String formattedDate = zonedDateTime.format(formatter1);
            bookingList.stream().forEach(booking -> booking.setStartDate(formattedDate));
            bookingRepo.saveAll(bookingList);
        }
        // case change hdv temp
        List<TourSchedule> schedules = tourScheduleRepo.findByStartDateId(result.getId());
        List<User> users = userRepo.findAll();
        if (schedules.isEmpty() || schedules.size() != guides.length) {
            if (!schedules.isEmpty() && schedules.size() != guides.length) {
                // erase all existing ones
                tourScheduleRepo.deleteAll(schedules);
            }
            Arrays.stream(guides).forEach(guide -> {
                TourSchedule schedule = new TourSchedule();
                User guide1 = users.stream().filter(g -> g.getId().equals(guide) && (g.getRole().equals(RoleEnum.GUIDE) || g.getRole().equals(RoleEnum.LEADGUIDE))).findFirst().orElse(null);
                if (guide1 == null) try {
                    throw new CustomRuntimeException("Input guide Id invalid");
                } catch (CustomRuntimeException e) {
                    e.printStackTrace();
                }
                schedule.setStartDate(newStartDate);
                schedule.setEndDate(endDate);
                schedule.setTourId(tourId);
                schedule.setGuideId(guide);
                schedule.setStartDateId(result.getId());
                tourScheduleRepo.save(schedule);
            });
        } else {
            for (int i = 0; i < guides.length; i++) {
                TourSchedule schedule = schedules.get(i);
                User guide = userRepo.findById(guides[i]).orElse(null);
                if (guide == null || guide.getRole().equals(RoleEnum.ADMIN) || guide.getRole().equals(RoleEnum.USER)) {
                    throw new CustomRuntimeException("Guide id invalid");
                }
                schedule.setGuideId(guide.getId());
                tourScheduleRepo.save(schedule);
            }
        }
        List<Booking> bookingList1 = bookingRepo.findByStartDateId(startId);
        if (bookingList1 != null && !bookingList1.isEmpty()) {
            bookingList1 = bookingList1.stream().filter(b -> b.isStatus()).collect(Collectors.toList());
            List<String> bccRecipients = new ArrayList<>();
            bookingList1.stream().forEach(booking -> {
                bccRecipients.add(booking.getUser().getEmail());
            });
            StartDate startDate1 = startDateRepo.findById(startId).orElse(null);
            String duration;
            String guideListName = null;
            if (startDate1 != null && startDate1.getLocations() != null) {
                duration = startDate1.getLocations().size() + "-day trip";
                List<TourSchedule> tourScheduleList = tourScheduleRepo.findByStartDateId(startId);
                if (!tourScheduleList.isEmpty()) {
                    List<User> userList = new ArrayList<>();
                    tourScheduleList.forEach(schedule -> {
                        User u = userRepo.findById(schedule.getGuideId()).orElse(null);
                        if (u != null) {
                            userList.add(u);
                        }
                    });
                    if (userList.size() > 0) {
                        guideListName = userList.stream().map(user -> user.getFullName() + " - " + user.getEmail()).collect(Collectors.joining(" "));
                    } else {
                        guideListName = "Admin will reannounce you later";
                    }

                }
            } else {
                duration = tour.getLocations().size() + "-day trip";
            }
            SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
            Date parsedDate = inputFormat.parse(bookingList1.get(0).getStartDate());
            LocalDate localDate = parsedDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedDate = localDate.format(outputFormat);

            Map<String, Object> template = new HashMap<>();
            template.put("tourName", tour.getName());
            template.put("duration", duration);
            template.put("startDate", formattedDate + " " + tour.getStartTime());
            template.put("guideListName", guideListName);
            try {
                emailService.sendMessageUsingThymeleafTemplateToMultiple("m61uoyni4@mozmail.com", "Your booking has changes", template, "booking_change.html", bccRecipients);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new ResponseEntity("Changed startDate and guide temporary on selected date success", HttpStatus.OK);

    }


    @GetMapping("/test/create-all-startDates")
    public ResponseEntity<List<StartDate>> createStartDatesForAll() {
        List<Tour> list = tourRepo.findAll().stream().filter(tour -> tour.getStartDates() != null).collect(Collectors.toList());
        list.stream().forEach(tour -> {
            tour.getStartDates().stream().forEach(start -> {
                StartDate date = new StartDate();
                date.setTourId(tour.getId());
                date.setStartDate(start);
                Integer days = tour.getLocations() != null ? tour.getLocations().size() : 1;
                LocalDate endDate = start.plusDays(days - 1);
                date.setEndDate(endDate);
                StartDate result = startDateRepo.save(date);
            });
        });
        List<StartDate> startDateList = startDateRepo.findAll();
        return new ResponseEntity<>(startDateList, HttpStatus.OK);
    }

    @GetMapping("/test/delete-all-startDates")
    public ResponseEntity deleteAll() {
        startDateRepo.deleteAll();
        return new ResponseEntity<>("deleted", HttpStatus.OK);
    }

    @GetMapping("/test/create-all-schedule")
    public void createScheduleAll() {
        List<Tour> list = tourRepo.findAll();
        List<StartDate> startDateList = startDateRepo.findAll();
        startDateList.stream().forEach(startDate -> {
            Tour tour = list.stream().filter(tour1 -> tour1.getId().equals(startDate.getTourId())).findFirst().orElse(null);
            if (tour != null) {
                if (tour.getGuides() != null) {
                    tour.getGuides().stream().forEach(guide -> {
                        TourSchedule schedule = new TourSchedule();
                        schedule.setTourId(startDate.getTourId());
                        schedule.setStartDate(startDate.getStartDate());
                        schedule.setGuideId(guide.toString());
                        schedule.setEndDate(startDate.getEndDate());
                        schedule.setStartDateId(startDate.getId());
                        tourScheduleRepo.save(schedule);
                    });
                }

            }

        });

    }

    @GetMapping("/test/add-startDateId-to-all-bookings")
    public ResponseEntity addStartDateIdToAllBookings() {
        List<StartDate> startDateList = startDateRepo.findAll();
        List<Booking> bookingList = bookingRepo.findAll();
        List<Booking> temp = bookingList;
        temp.stream().forEach(booking -> {
            String tourId = booking.getTourId();
            StartDate start = startDateList.stream().filter(startDate -> startDate.getTourId().equals(tourId)).findFirst().orElse(null);
            if (start != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy");
                LocalDate date = start.getStartDate();
                LocalDateTime localDateTime = date.atStartOfDay();

                // Convert LocalDateTime to ZonedDateTime with ICT time zone
                ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Bangkok"));

                // Format the ZonedDateTime to the required string format
                String formattedDate = zonedDateTime.format(formatter);
                System.out.println("Formatted Start Date (ICT): " + formattedDate);

                booking.setStartDate(formattedDate);  // Set formatted date

                booking.setStartDate(formattedDate);
                booking.setStartDateId(start.getId());
                bookingRepo.save(booking);
            }
        });
        return new ResponseEntity("all set", HttpStatus.OK);
    }

    @GetMapping("/get-all-schedules")
    public ResponseEntity<List<ScheduleWorking>> getSchedulesOfAllGuides() {
        List<ScheduleWorking> list = scheduleService.getAllSchedulesOfAllGuides();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/get-all-schedules-of-a-guide")
    public ResponseEntity<List<ScheduleWorking>> getGuideSchedule(@RequestParam(name = "guideId") String guideId) throws CustomRuntimeException {
        List<ScheduleWorking> list = scheduleService.getSchedulesOfSpecificGuide(guideId);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/get-all-schedules-of-a-tour")
    public ResponseEntity<List<ScheduleWorking>> getSchedulesOfTour(@RequestParam(name = "tourId") String tourId) {
        List<ScheduleWorking> list = scheduleService.getSchedulesOfTour(tourId);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/get-all-startDates-of-a-tour")
    public ResponseEntity<List<StartDate>> getStartDatesOfATour(@RequestParam(name = "tourId") String tourId) {
        List<StartDate> list = startDateRepo.findByTourId(tourId);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/get-all-startDates")
    public ResponseEntity<List<StartDate>> getAllStartDates() {
        List<StartDate> list = startDateRepo.findAll();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/get-detail-schedule")
    public ResponseEntity<ScheduleWorking> getDetailsSchedule(@RequestParam(name = "scheduleId") String scheduleId) throws CustomRuntimeException {
        ScheduleWorking schedule = scheduleService.getDetailsOfASchedule(scheduleId);
        return new ResponseEntity<>(schedule, HttpStatus.OK);
    }

//    @PostMapping("/tour/schedule/update")//edit
//    public ResponseEntity<List<TourSchedule>> updateScheduleApi(@RequestParam(name = "tourId") String tourId, @RequestParam(name = "startDates") String[] startDates, @RequestParam(name = "totalDays") long totalDays,
//                                                                @RequestParam(name = "guides", required = false) String[] guides) throws CustomRuntimeException {
//
//        List<TourSchedule> listExisted = tourScheduleRepo.findByTourId(new ObjectId(tourId));
//        List<TourSchedule> list;
//        if (listExisted == null || listExisted.isEmpty()) {
//            throw new CustomRuntimeException("Schedules haven't been created");
//        } else {
//            listExisted.stream().forEach(tourSchedule -> {
//                try {
//                    tourScheduleRepo.deleteById(tourSchedule.getId());
//                } catch (Exception e) {
//                    try {
//                        throw new CustomRuntimeException("Error when delete schedule before update! Try again");
//                    } catch (CustomRuntimeException ex) {
//                        ex.printStackTrace();
//                    }
//                }
//            });
//            list = createSchedule(tourId, startDates, totalDays, guides);
//        }
//        if (list.isEmpty()) return new ResponseEntity<>(list, HttpStatus.EXPECTATION_FAILED);
//        return new ResponseEntity<>(list, HttpStatus.OK);
//    }

//    @GetMapping("/tour/schedule/addNew")// tao moi tour
//    public ResponseEntity<List<TourSchedule>> getScheduleListOfEmployees(@RequestParam(name = "currentEpochTime") long currentEpochTime) {//26/9/2024 0:00:00->epoch
//        List<TourSchedule> list = tourScheduleService.getListAfterATimeRequired(currentEpochTime);
//        return new ResponseEntity<>(list, HttpStatus.OK);
////list ban
//    }
//
//    @GetMapping("/tour/schedule/edit")//edit tour
//    public ResponseEntity<List<TourSchedule>> getScheduleListEmployeesEditTour(@RequestParam(name = "startDateEpochTimeInArray") long[] startDateEpochTimeInArray, @RequestParam(name = "tourId") String tourId) {
//        List<TourSchedule> combinedList = new ArrayList<>();
//        List<TourSchedule> finalList;
//        Arrays.stream(startDateEpochTimeInArray).forEach(startEpoch -> {
//            List<TourSchedule> list = tourScheduleService.getListAfterATimeRequired(startEpoch);
//            combinedList.addAll(list);
//        });
//        finalList = combinedList.stream().filter(tourSchedule -> tourSchedule.getTourId() != new ObjectId(tourId)).collect(Collectors.toList());
//        System.out.println(finalList);
//        return new ResponseEntity<>(finalList, HttpStatus.OK);
//    }

    @GetMapping("/test/create-all-keys-for-tours")
    public ResponseEntity<HttpResponse> createKeysForTours() {
        List<Tour> list = tourRepo.findAll();
        list.stream().forEach(tour -> {
            if (tour.getStartDates() != null && (tour.getKeyOfDatesRelation() == null || tour.getKeyOfDatesRelation().isEmpty())) {
                Map<String, LocalDate> keysAndStartDates = new HashMap<>();
                tour.getStartDates().stream().forEach(startDate -> {
                    String random = UUID.randomUUID().toString().substring(0, 15);

                    keysAndStartDates.put(random, startDate);
                });
                tour.setKeyOfDatesRelation(keysAndStartDates);
                tourRepo.save(tour);
            }
        });
        return new ResponseEntity<>(new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(), "All changed"), HttpStatus.OK);
    }

    @GetMapping("/schedules/from-a-bookingId")
    public ResponseEntity<List<ScheduleWorking>> getListSchedulesFromASingleBookingId(@RequestParam(name = "bookingId") String bookingId) {
        return new ResponseEntity<>(scheduleService.getListOfSchedulesFromASingleBookingId(bookingId), HttpStatus.OK);
    }


}
