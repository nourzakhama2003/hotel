import { Booknigstatus } from "./enums/bookingStatus";
import { PaymentStatus } from "./enums/paymentStatus";
import { Payment } from "./payment";

export interface Booking{
    Id:number,
    checkInDate:string,
    checkOutDate:string,
    bookingReference:string,
    totalPrice:number,
    userId?:number,
    roomId:number,
   payment:Payment,
    bookingStatus:Booknigstatus,
    paymentStatus:PaymentStatus,
    createAt:string
}