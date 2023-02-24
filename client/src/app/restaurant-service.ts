import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http'
import { Restaurant, Comment } from './models'
import { lastValueFrom } from 'rxjs'
import { Injectable } from '@angular/core'

@Injectable()
export class RestaurantService {

	constructor(private httpClient: HttpClient) { }

	// TODO Task 2 
	// Use the following method to get a list of cuisines
	// You can add any parameters (if any) and the return type 
	// DO NOT CHNAGE THE METHOD'S NAME
	public getCuisineList(): Promise<any> {
		// Implememntation in here
		return lastValueFrom(
			this.httpClient.get<any>(
				'http://localhost:8080/api/cuisines'
			)
		)
	}

	// TODO Task 3 
	// Use the following method to get a list of restaurants by cuisine
	// You can add any parameters (if any) and the return type 
	// DO NOT CHNAGE THE METHOD'S NAME
	public getRestaurantsByCuisine(cuisine: string) {
		// Implememntation in here
		return lastValueFrom(
			this.httpClient.get<any>(
				`http://localhost:8080/api/${cuisine}/restaurants`
			)
		)
	}
	
	// TODO Task 4
	// Use this method to find a specific restaurant
	// You can add any parameters (if any) 
	// DO NOT CHNAGE THE METHOD'S NAME OR THE RETURN TYPE
	public getRestaurant(cuisine: string, restaurant: string): Promise<Restaurant> {
		// Implememntation in here
		return lastValueFrom(
			this.httpClient.get<Restaurant>(
				`http://localhost:8080/api/${cuisine}/${restaurant}/details`	
			)
		)
	}

	// TODO Task 5
	// Use this method to submit a comment
	// DO NOT CHANGE THE METHOD'S NAME OR SIGNATURE
	public postComment(comment: Comment): Promise<any> {
		// Implememntation in here
		return lastValueFrom(
			this.httpClient.post<Restaurant>(
				`http://localhost:8080/api/comments`, comment
			)
		)
	}
}
