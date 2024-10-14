package com.aptech.SemesterProject.utility;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.stereotype.Component;

@ConfigurationProperties("storage")
@ConfigurationPropertiesScan
@Component
public class StorageProperties {

    private String location = "upload/image";
    public String getLocation(){
        return location;
    }
    public void setLocation(String location){
        this.location=location;
    }
}
