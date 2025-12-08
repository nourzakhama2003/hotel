import { Booking } from "./booking";
import { Hotel } from "./hotel";
import { Room } from "./room";
import { UserProfile } from "./userProfile";

export interface AppResponse {
    status:number,
    message:string,
    transactionId?:string,
    roomNumber?:number,
    bookingReference?:string,
    user?:UserProfile,
    users?:UserProfile[],
    hotel?:Hotel,
    hotels?:Hotel[],
    room?:Room,
    rooms?:Room[],
    booking?:Booking,
    bookings?:Booking[],
    time:string

}