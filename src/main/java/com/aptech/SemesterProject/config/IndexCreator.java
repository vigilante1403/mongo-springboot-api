package com.aptech.SemesterProject.config;

import com.aptech.SemesterProject.entity.Location;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.stereotype.Component;

@Component
public class IndexCreator {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Bean
    public void createGeospatialIndex() {
        mongoTemplate.indexOps(Location.class).ensureIndex(new GeospatialIndex("coordinates"));


    }
}
