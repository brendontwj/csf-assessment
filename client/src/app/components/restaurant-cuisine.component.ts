import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { RestaurantService } from '../restaurant-service';

@Component({
  selector: 'app-restaurant-cuisine',
  templateUrl: './restaurant-cuisine.component.html',
  styleUrls: ['./restaurant-cuisine.component.css']
})
export class RestaurantCuisineComponent implements OnInit {

  cuisine!: string
  restaurantList: String[] = []
  routeSub$!: Subscription;
	
	// TODO Task 3
	// For View 2
  constructor(
    private activatedRoute: ActivatedRoute,
    private restaurantService: RestaurantService
  ) {}

  ngOnInit(): void {
    this.routeSub$ = this.activatedRoute.params.subscribe((params) => {
      this.cuisine = params['cuisine']
    })
    this.getRestaurants()
  }

  getRestaurants() {
    this.restaurantService.getRestaurantsByCuisine(this.cuisine)
      .then((result) => {
        this.restaurantList = result['restaurants']
      }).catch((err) => {
        console.log(err)
      })
  }

}
