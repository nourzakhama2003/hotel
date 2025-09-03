import { Booking } from "./booking";
import { Roomtype } from "./enums/roomType";

export interface Room{

    
 Id:number,
 roomNumber:number,
 capacity:number,
type:Roomtype,
 pricePerNight:number,
description:string
 roomImage?:string,
   createAt:string ;
    hotelId:number
  booking?:Booking[];

}