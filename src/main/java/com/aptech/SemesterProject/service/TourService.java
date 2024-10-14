package com.aptech.SemesterProject.service;

import com.aptech.SemesterProject.dto.TourDto;
import com.aptech.SemesterProject.dto.TourWithCommentsDto;
import com.aptech.SemesterProject.entity.Tour;
import com.aptech.SemesterProject.exception.CustomRuntimeException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.geojson.Point;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


import java.util.Collection;
import java.util.List;

public interface TourService {
    List<TourWithCommentsDto> getAllTours();
    Tour addNewTour(Tour tour);
    Collection<Tour> toursOnKeywords(String keywords);
    void deleteTour(String id);
    TourWithCommentsDto getTourByIdOrSlug(String id, String slug) throws CustomRuntimeException;
    Tour updateTour(Tour tour);
    FindIterable<Document> findToursNearByGivingPosition(Point point, double minDistanceInMeters, double maxDistanceInMeters);
    FindIterable<Document> findNearByLocationWithinCircleSphere(double longitude,double latitude,double distanceInKm);
    List<Tour> findToursWithinGivenTime(String dateGiven,long days);

}
