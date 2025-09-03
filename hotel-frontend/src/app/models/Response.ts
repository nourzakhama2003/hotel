import { Booking } from "./booking";
import { hotel } from "./hotel";
import { Room } from "./room";
import { UserProfile } from "./userProfile";

export interface AppResponse {
    status:number,
    message:string,
    bookingReference?:string,
    user?:UserProfile,
    users?:UserProfile[],
    hotel?:hotel,
    hotels?:hotel[],
    room?:Room,
    rooms?:Room[],
    booking?:Booking,
    bookings?:Booking[],
    time:string

}