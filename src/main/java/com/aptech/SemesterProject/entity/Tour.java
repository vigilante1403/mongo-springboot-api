package com.aptech.SemesterProject.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;
import java.util.Set;
@Document("tours")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tour {
    @Id
    private String id;
    @Indexed(unique = true)
    private String name;
    private String slug;
    private int maxGroupSize;
    private double ratingsAverage=4.6;
    private double price;
    private double priceDiscount;
    private String summary;
    private String description;
    private String imageCover;
    private String countryNameCommon;
    private String countryNameOfficial;
    private String countryFlag;
    private String region;
    private String status;
    private Set<String> images;
    @CreatedDate
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Date createdAt;
    private Set<LocalDate> startDates;
    private Set<Location> locations;
    @JsonSerialize(contentUsing = ObjectIdSerializer.class)//serialize to String
    private Set<ObjectId> guides; // connect to users who have role guide and tour guide
    private String startTime="5:00";
    private Map<String,LocalDate> keyOfDatesRelation;



}
