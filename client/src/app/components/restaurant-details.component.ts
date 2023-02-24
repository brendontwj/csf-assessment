import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { Restaurant } from '../models';
import { RestaurantService } from '../restaurant-service';

@Component({
  selector: 'app-restaurant-details',
  templateUrl: './restaurant-details.component.html',
  styleUrls: ['./restaurant-details.component.css']
})
export class RestaurantDetailsComponent implements OnInit {
	
	// TODO Task 4 and Task 5
	// For View 3
  form!: FormGroup
  restaurant!: Restaurant
  restaurantName!: string
  restaurantCuisine!: string
  routeSub$!: Subscription;


  constructor(
    private fb: FormBuilder,
    private restaurantService: RestaurantService,
    private activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
      this.form = this.createForm()
      this.routeSub$ = this.activatedRoute.params.subscribe((params) => {
        this.restaurantName = params['name']
        this.restaurantCuisine = params['cuisine']
      })
  }

  createForm() {
    return this.fb.group({
      name: this.fb.control('', [Validators.required]),
      rating: this.fb.control(1,[Validators.required]),
      text: this.fb.control('', [Validators.required])
    })
  }

  getRestaurant() {
    this.restaurantService.getRestaurant(this.restaurantCuisine, this.restaurantName)
      .then((result) => {
        this.restaurant = result
      }).catch((err) => {
        console.log(err)
      })
  }

}
