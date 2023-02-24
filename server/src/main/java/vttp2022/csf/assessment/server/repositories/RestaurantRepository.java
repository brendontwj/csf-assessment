package vttp2022.csf.assessment.server.repositories;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.StringOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import vttp2022.csf.assessment.server.models.Comment;
import vttp2022.csf.assessment.server.models.LatLng;
import vttp2022.csf.assessment.server.models.Restaurant;

@Repository
public class RestaurantRepository {

	// TODO Task 2
	// Use this method to retrive a list of cuisines from the restaurant collection
	// You can add any parameters (if any) and the return type 
	// DO NOT CHNAGE THE METHOD'S NAME
	// Write the Mongo native query above for this method
	//  db.getCollection("restaurants").distinct("cuisine")

	@Autowired 
	private MongoTemplate mongoTemplate;

	private static final String COMMENT_COLLECTION = "comments";

	public List<String> getCuisines() {
		// Implmementation in here
		List<String> cuisineList = mongoTemplate.findDistinct(Query.query(Criteria.where("cuisine").exists(true)), "cuisine", "restaurants", String.class);
		for(String s:cuisineList) {
			System.out.println(">>>> cuisine %s".formatted(s));
		}
		return cuisineList;
	}

	// TODO Task 3
	// Use this method to retrive a all restaurants for a particular cuisine
	// You can add any parameters (if any) and the return type 
	// DO NOT CHNAGE THE METHOD'S NAME
	// Write the Mongo native query above for this method
	// db.getCollection("restaurants").find({
	// 	"cuisine": "<cuisine>"
	// }) 

	public List<String> getRestaurantsByCuisine(String cuisine) {
		// Implmementation in here
		List<Document> docList = mongoTemplate.find(Query.query(Criteria.where("cuisine").is(cuisine)), Document.class, "restaurants");
		List<String> restaurantList = new LinkedList<>();
		for(Document d: docList)
			restaurantList.add(d.getString("name"));
		for(String s:restaurantList) {
			System.out.println(">>>> restaurant %s".formatted(s));
		}
		return restaurantList;
	}

	// TODO Task 4
	// Use this method to find a specific restaurant
	// You can add any parameters (if any) 
	// DO NOT CHNAGE THE METHOD'S NAME OR THE RETURN TYPE
	// Write the Mongo native query above for this method 
	// db.getCollection("restaurants").aggregate([
	// 	{
	// 		$match: {name: "Ajisen Ramen"}
	// 	},
	// 	{
	// 		$project: {
	// 			restaurant_id: 1,
	// 			name: 1,
	// 			address: {
	// 				$concat: ["$address.building", ", ", "$address.street",", ", "$address.zipcode",", ", "$borough"]
	// 			},
	// 			coordinates: "$address.coord"
	// 		}
	// 	}
	// ])
	public Optional<Restaurant> getRestaurant(String restaurant) {
		// Implmementation in here

		MatchOperation matchRestaurant = Aggregation.match(
			Criteria.where("name").is(restaurant)
		);

		ProjectionOperation projectRestaurantDetails = Aggregation.project(
			"restaurant_id",
			"name",
			"cuisine"
		).and(
			StringOperators.Concat.valueOf("address.building").concat(", ")
				.concat("address.street").concat(", ").concat("address.zipcode").concat(", ")
				.concat("borough")
		).as(
			"address");
		// ).and("address.coord.0").as("coordinates")

		Aggregation pipeline = Aggregation.newAggregation(
			matchRestaurant, projectRestaurantDetails
		);

		AggregationResults<Document> results = mongoTemplate.aggregate(pipeline, "restaurants", Document.class);
		if(results == null) {
			return Optional.empty();
		}
		Document d = (Document) results.getRawResults();
		System.out.println(d.getString("name"));
		return Optional.of(RestaurantRepository.toRestaurant(d));
	}

	// TODO Task 5
	// Use this method to insert a comment into the restaurant database
	// DO NOT CHNAGE THE METHOD'S NAME OR THE RETURN TYPE
	// Write the Mongo native query above for this method
	//  
	public boolean addComment(Comment comment) {
		// Implmementation in here
		Document doc = new Document();
        doc.put("restaurant_id", comment.getRestaurantId());
        doc.put("name", comment.getName());
        doc.put("rating", comment.getRating());
        doc.put("text", comment.getText());

        Document docInserted = mongoTemplate.insert(doc, COMMENT_COLLECTION);
        return docInserted != null;
	}
	
	// You may add other methods to this class

	
	public static Restaurant toRestaurant(Document d) {
		Restaurant restaurant = new Restaurant();
		restaurant.setRestaurantId(d.getString("restaurant_id"));
		restaurant.setName(d.getString("name"));
		restaurant.setCuisine(d.getString("cuisine"));
		restaurant.setAddress(d.getString("address"));
		LatLng latLng = new LatLng();
		latLng.setLatitude(d.getEmbedded(List.of("coordinates", "1"), Float.class));
		latLng.setLongitude(d.getEmbedded(List.of("coordinates", "0"), Float.class));
		restaurant.setCoordinates(latLng);
		System.out.println(restaurant.getRestaurantId());
		System.out.println(restaurant.getName());
		System.out.println(restaurant.getCuisine());
		System.out.println(restaurant.getName());
		System.out.println(restaurant.getName());
		return restaurant;
	}
}
