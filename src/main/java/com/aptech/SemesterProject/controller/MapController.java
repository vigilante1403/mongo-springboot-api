package com.aptech.SemesterProject.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@RestController
@RequestMapping("/api/v1/maps")
public class MapController {
    @Value("${geoapify.secret.key}")
    private String apiKey;
@PostMapping("/locations")
    public ResponseEntity<Object> getLocations(@RequestParam(name="address")String address) throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();
    String uri = "https://api.geoapify.com/v1/geocode/search?text="+address.replaceAll(" ","%20")+"&apiKey="+apiKey;
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(uri))
            .header("Content-Type", "application/json")
            .build();

    HttpResponse response =
            client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

    System.out.println(response.body());
    return new ResponseEntity<>(response.body(), HttpStatus.OK);
}
//@GetMapping("/test")
//    public ResponseEntity<Long> getCurrentTimeMilis(){
//    ZoneId zoneId = ZoneId.of("Asia/Bangkok");
//    ZonedDateTime time = ZonedDateTime.now(zoneId);
//    long currentTime = time.toInstant().toEpochMilli()+7*60*60*1000;
//    System.out.println(currentTime/1000);
//    LocalDateTime now = LocalDateTime.ofEpochSecond(currentTime/1000);
//    return new ResponseEntity<>(currentTime,HttpStatus.OK);
//}
}
