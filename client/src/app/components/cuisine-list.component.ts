import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { RestaurantService } from '../restaurant-service';

@Component({
  selector: 'app-cuisine-list',
  templateUrl: './cuisine-list.component.html',
  styleUrls: ['./cuisine-list.component.css']
})
export class CuisineListComponent implements OnInit {

  cuisineList: String[] = []

	// TODO Task 2
	// For View 1

  constructor(private activatedRoute: ActivatedRoute, private restaurantService: RestaurantService) {}

  ngOnInit(): void {
      this.restaurantService.getCuisineList().then((result) => {
        this.cuisineList = result['cuisines']
      }).catch((err) =>{
        console.log(err)
      })

      console.log(this.cuisineList)
  }
}
