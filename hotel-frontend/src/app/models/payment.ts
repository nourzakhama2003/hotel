import { PaymentGateway } from "./enums/paymentgateway";

export interface Payment{
    Id:number,
    bookingId?:number,
    transactionId:string,
    amount:number,
    bookingReference:string,
    failureReason?:string,
    success:boolean,
    paymentGateway:PaymentGateway,
    approvalLink?:string,
    paymentDate:string

}