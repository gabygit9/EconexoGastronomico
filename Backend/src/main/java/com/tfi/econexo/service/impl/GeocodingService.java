package com.tfi.econexo.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class GeocodingService {

    @Value("${econexo.google.maps.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public record Coordinates(Double lat, Double lng){}

    public Coordinates getCoordinates(String address) {
     String url = UriComponentsBuilder
             .fromUriString("https://maps.googleapis.com/maps/api/geocode/json")
             .queryParam("address", address)
             .queryParam("key", apiKey)
             .toUriString();

     try{
         JsonNode response = restTemplate.getForObject(url, JsonNode.class);

         if(response != null && "OK".equals(response.get("status").asText())){
             JsonNode location = response.get("results").get(0).get("geometry").get("location");

             Double lat = location.get("lat").asDouble();
             Double lng = location.get("lng").asDouble();

             return new Coordinates(lat, lng);
         }
     } catch(Exception e){
         System.err.println("Error connecting with Google Maps API: " + e.getMessage());
     }
     return null;
    }
}
