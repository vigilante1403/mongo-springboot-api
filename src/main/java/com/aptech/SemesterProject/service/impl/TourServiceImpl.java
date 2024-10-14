package com.aptech.SemesterProject.service.impl;

import com.aptech.SemesterProject.dto.ReviewDto;
import com.aptech.SemesterProject.dto.TourDto;
import com.aptech.SemesterProject.dto.TourWithCommentsDto;
import com.aptech.SemesterProject.entity.Location;
import com.aptech.SemesterProject.entity.Tour;

import com.aptech.SemesterProject.exception.CustomRuntimeException;
import com.aptech.SemesterProject.repo.TourRepo;
import com.aptech.SemesterProject.repo.UserRepo;
import com.aptech.SemesterProject.service.ReviewService;
import com.aptech.SemesterProject.service.TourService;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.geojson.Geometry;
import com.mongodb.client.model.geojson.Point;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.mongodb.MongoExpression;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.search.SearchOperator.text;
import static com.mongodb.client.model.search.SearchOptions.searchOptions;
import static com.mongodb.client.model.search.SearchPath.fieldPath;

@Service
public class TourServiceImpl implements TourService {
    @Autowired
    private TourRepo tourRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ReviewService reviewService;

    private final static Logger logger= LoggerFactory.getLogger(TourServiceImpl.class);
    private final MongoCollection<Document> collection;
    @Autowired
    private MongoTemplate mongoTemplate;
    public TourServiceImpl(MongoTemplate mongoTemplate){

        this.collection=mongoTemplate.getCollection("tours");
//        this.collection.createIndex(Indexes.geo2d("locations"));
    }


    @Override
    public List<TourWithCommentsDto> getAllTours() {

        List<Tour> list = tourRepo.findAll();
        List<TourWithCommentsDto> list1 = list.stream().map(tour-> {
            try {
                return new TourWithCommentsDto(tour.getId(),tour.getName(),tour.getPrice(),tour.getGuides().stream().map(guide->userRepo.findById(guide.toString()).get()).collect(Collectors.toSet()),tour.getImageCover(),tour.getDescription(),tour.getSummary(),tour.getMaxGroupSize(),tour.getPriceDiscount(),tour.getStartDates()!=null?tour.getStartDates():new HashSet<LocalDate>(),tour.getCountryNameCommon(),tour.getCountryNameOfficial(),tour.getCountryFlag(),tour.getRegion(),tour.getStatus(),reviewService.findReviewsOfTour(tour.getId()),tour.getImages(),tour.getLocations()!=null?tour.getLocations().stream().toList():new ArrayList<Location>(),tour.getRatingsAverage(),tour.getStartTime(),tour.getKeyOfDatesRelation());
            } catch (CustomRuntimeException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());

        return list1;
    }

    @Override
    public Tour addNewTour(Tour tour) {
        return tourRepo.save(tour);
    }

    @Override
    public Collection<Tour> toursOnKeywords(String keywords) {
       logger.info("search tours by keywords ",keywords);
       Query query=new Query();
       String regexString = "^"+keywords;
       query.addCriteria(Criteria.where("name").regex(regexString));
        query.fields().project((MongoExpression) include("name"));
       List<Tour> list = mongoTemplate.find(query,Tour.class);
//        Bson searchStage = search(text(fieldPath("name"),keywords));
//        Bson projectStage = project(fields(excludeId(),include("name","price","guides")));
//        Bson limitStage = limit(5);
//        List<Bson> pipeline = List.of(searchStage,projectStage,limitStage);
//        List<Document> docs = collection.aggregate(pipeline).into(new ArrayList<>());
//        if(docs.isEmpty()){
//            throw new EntityNotFoundException("Not found");
//        }
        return list;
    }

    @Override
    public void deleteTour(String id) {
        tourRepo.deleteById(id);
    }

    @Override
    public TourWithCommentsDto getTourByIdOrSlug(String id, String slug) throws CustomRuntimeException {
        Tour tour =null;
        TourWithCommentsDto dto = null;
        if(StringUtils.isNotBlank(id)){
             tour = tourRepo.findById(id).orElse(null);
            if(tour!=null){
                // tim review co tourId = tour.id
                List<ReviewDto> reviewDtos = reviewService.findReviewsOfTour(tour.getId());
                dto = new TourWithCommentsDto(tour.getId(),tour.getName(),tour.getPrice(),tour.getGuides().stream().map(guide->userRepo.findById(guide.toString()).get()).collect(Collectors.toSet()),tour.getImageCover(),tour.getDescription(),tour.getSummary(),tour.getMaxGroupSize(),tour.getPriceDiscount(),tour.getStartDates()!=null?tour.getStartDates():new HashSet<LocalDate>(),tour.getCountryNameCommon(),tour.getCountryNameOfficial(),tour.getCountryFlag(),tour.getRegion(),tour.getStatus(),reviewDtos,tour.getImages(),tour.getLocations()!=null?tour.getLocations().stream().toList():new ArrayList<Location>(),tour.getRatingsAverage(),tour.getStartTime(),tour.getKeyOfDatesRelation());
            }
        }else{
            tour = tourRepo.findTourBySlug(slug);
            if(tour!=null){
                List<ReviewDto> reviewDtos = reviewService.findReviewsOfTour(tour.getId());
                dto = new TourWithCommentsDto(tour.getId(),tour.getName(),tour.getPrice(),tour.getGuides().stream().map(guide->userRepo.findById(guide.toString()).get()).collect(Collectors.toSet()),tour.getImageCover(),tour.getDescription(),tour.getSummary(),tour.getMaxGroupSize(),tour.getPriceDiscount(),tour.getStartDates()!=null?tour.getStartDates():new HashSet<LocalDate>(),tour.getCountryNameCommon(),tour.getCountryNameOfficial(),tour.getCountryFlag(),tour.getRegion(),tour.getStatus(),reviewDtos,tour.getImages(),tour.getLocations()!=null?tour.getLocations().stream().toList():new ArrayList<Location>(),tour.getRatingsAverage(),tour.getStartTime(),tour.getKeyOfDatesRelation());
            }
        }
        return dto;
    }
    @Override
    public Tour updateTour(Tour tour) {
        return tourRepo.save(tour);
    }

    @Override
    public FindIterable<Document> findToursNearByGivingPosition(Point point,double minDistanceInMeters,double maxDistanceInMeters) {
        FindIterable<Document> result = collection.find(Filters.near("locations",point,maxDistanceInMeters,minDistanceInMeters));
        return result;
    }

    @Override
    public FindIterable<Document> findNearByLocationWithinCircleSphere(double longitude,double latitude, double distanceInKm) {
        double distanceInRad = distanceInKm/6371;
        FindIterable<Document> result = collection.find(Filters.geoWithinCenterSphere("locations",longitude,latitude,distanceInRad));

        return result;
    }

    @Override
    public List<Tour> findToursWithinGivenTime(String dateGiven,long days) {
        LocalDateTime now = LocalDateTime.parse(dateGiven,DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDateTime then = now.plusDays(days);
        // transfer to yyyy-MM-dd and in string in order to compare
        DateTimeFormatter fomatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String nowFormatter = now.format(fomatter);
        String thenFormatter=then.format(fomatter);
        Query query = new Query();
        query.addCriteria(Criteria.where("startDates").elemMatch(Criteria.where("$gte").is(nowFormatter).andOperator(Criteria.where("$lte").is(thenFormatter))));
        return mongoTemplate.find(query,Tour.class);

    }


}
