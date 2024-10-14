package com.aptech.SemesterProject.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.lang.reflect.Array;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Location {
    @Id
    private String id;
    private String type="Point";

    private double[] coordinates;
    private String address;
    private String description;// lịch trình
//    private String placeName;
    private int day;
    public Location(double longitude, double latitude,String address,String description,int day){
        this.coordinates=new double[]{longitude,latitude};
        this.address=address;
        this.description=description;
        this.day=day;
    }

}
