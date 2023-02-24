package vttp2022.csf.assessment.server.controllers;

import java.io.StringReader;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp2022.csf.assessment.server.models.Comment;
import vttp2022.csf.assessment.server.models.Restaurant;
import vttp2022.csf.assessment.server.repositories.MapCache;
import vttp2022.csf.assessment.server.services.RestaurantService;

@Controller
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private MapCache mapCache;
    
    @GetMapping(path = "/cuisines", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> getCuisines() {
        List<String> cuisineList = restaurantService.getCuisines();
        JsonArrayBuilder jab = Json.createArrayBuilder();
        for(String s: cuisineList) {
            jab.add(s);
        }
        JsonObject payload = Json.createObjectBuilder()
            .add("cuisines", jab.build())
            .build();
        return ResponseEntity.ok(payload.toString());
    }

    @GetMapping(path = "/{cuisine}/restaurants", produces = MediaType.APPLICATION_JSON_VALUE) 
    @ResponseBody
    public ResponseEntity<String> getRestaurants(@PathVariable String cuisine) {
        List<String> restaurantList = restaurantService.getRestaurantsByCuisine(cuisine);
        JsonArrayBuilder jab = Json.createArrayBuilder();
        for(String s: restaurantList) {
            jab.add(s);
        }
        JsonObject payload = Json.createObjectBuilder()
            .add("restaurants", jab.build())
            .build();
        return ResponseEntity.ok(payload.toString());
    }

    @GetMapping(path = "/{cuisine}/{restaurant}/details", produces = MediaType.APPLICATION_JSON_VALUE) 
    @ResponseBody
    public ResponseEntity<String> getRestaurantDetails(@PathVariable String cuisine,
        @PathVariable String restaurant
    ) {
        Optional<Restaurant> opt = restaurantService.getRestaurant(restaurant);
        if(opt.isEmpty()) {
            JsonObject resp = Json.createObjectBuilder()
                .add("Error", "No details found")
                .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp.toString());
        }
        Restaurant details = opt.get();
        String key = "";
        byte[] image = mapCache.getMap(details.getCoordinates().getLatitude(), details.getCoordinates().getLongitude());
        if (image == null) {
            image = getMapDetails(details.getCoordinates().getLatitude(), details.getCoordinates().getLongitude());
            key = mapCache.saveMap(details.getCoordinates().getLatitude(), details.getCoordinates().getLongitude(),image);
        }
        JsonObject payload = Json.createObjectBuilder()
            .add("restaurant_id", details.getRestaurantId())
            .add("name", details.getName())
            .add("cuisine", details.getCuisine())
            .add("address", details.getAddress())
            .add("mapUrl", "https://deadmanfred.sgp1.digitaloceanspaces.com/%s".formatted(key))
            .build();
        return ResponseEntity.ok(payload.toString());
    }

    @PostMapping("/comments")
    @ResponseBody
    public ResponseEntity<String> postComment(@RequestBody String body) {
        JsonReader reader = Json.createReader(new StringReader(body));
        JsonObject json = reader.readObject();
        Comment comment = new Comment();
        comment.setName(json.getString("name"));
        comment.setRating(json.getInt("rating"));
        comment.setText(json.getString("text"));
        if(!restaurantService.addComment(comment)) {
            JsonObject resp = Json.createObjectBuilder()
                .add("error", "cannot create comment")
                .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp.toString());
        }
        JsonObject resp = Json.createObjectBuilder()
            .add("message", "comment posted")
            .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(resp.toString());
        
    }

    public byte[] getMapDetails(float lat, float lng) {
        String url = "http://paf.chuklee.com/map/";
        String exchangeUrl = UriComponentsBuilder.fromUriString(url)
            .queryParam("lat", lat)
            .queryParam("lng", lng)
            .toUriString();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.IMAGE_PNG));
        HttpEntity<byte[]> entity = new HttpEntity<byte[]>(headers);
        return restTemplate.exchange(exchangeUrl, HttpMethod.GET, entity, byte[].class).getBody();
    }
}
