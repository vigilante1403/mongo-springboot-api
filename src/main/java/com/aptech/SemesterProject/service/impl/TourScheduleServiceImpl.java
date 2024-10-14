package com.aptech.SemesterProject.service.impl;

import com.aptech.SemesterProject.entity.Tour;
import com.aptech.SemesterProject.entity.TourSchedule;
import com.aptech.SemesterProject.service.TourScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class TourScheduleServiceImpl implements TourScheduleService {
    @Autowired
    private MongoTemplate mongoTemplate;
//
//    @Override
//    public List<TourSchedule> getListAfterATimeRequired(long requireTimeEpochMillisInGmt7) {
//        Query query = new Query();
//        query.addCriteria(Criteria.where("endDateByMillisEpoch").gte(requireTimeEpochMillisInGmt7));
//
//        return mongoTemplate.find(query, TourSchedule.class);
//
//    }
}
