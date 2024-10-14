package com.aptech.SemesterProject.dto;

import com.aptech.SemesterProject.entity.Location;
import com.aptech.SemesterProject.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@ToString
public class TourWithCommentsDto {
    private String id;
    private String name;
    private double price;
    private Set<User> guides;
    private String imageCover;
    private String description;
    private String summary;
    private int maxGroupSize;
    private double priceDiscount;
    private Set<String> images;
    private Set<LocalDate> startDates;
    private String countryNameCommon;
    private String countryNameOfficial;
    private String countryFlag;
    private String region;
    private String status;
    private List<ReviewDto> reviews;
    private List<Location> locations;
    private double ratingsAverage;
    private String startTime ="5:00";
    private Map<String,LocalDate> keyOfDatesRelation;


    public TourWithCommentsDto(String id, String name, double price, Set<User> guides, String imageCover, String description, String summary, int maxGroupSize, double priceDiscount, Set<LocalDate> startDates, String countryNameCommon, String countryNameOfficial, String countryFlag, String region, String status, List<ReviewDto> reviews, Set<String> images,List<Location>locations,Double ratingsAverage,String startTime, Map<String,LocalDate> keyOfDatesRelation) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.guides = guides;
        this.imageCover = imageCover;
        this.description = description;
        this.maxGroupSize = maxGroupSize;
        this.summary = summary;
        this.priceDiscount = priceDiscount;
        this.startDates = startDates;
        this.countryNameCommon = countryNameCommon;
        this.countryNameOfficial = countryNameOfficial;
        this.countryFlag = countryFlag;
        this.region = region;
        this.status = status;
        this.reviews = reviews;
        this.images = images;
        this.locations=locations;
        this.ratingsAverage=ratingsAverage;
        this.startTime=startTime;
        this.keyOfDatesRelation=keyOfDatesRelation;

    }
}
