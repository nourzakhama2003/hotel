import { Booking } from "./booking";
import { UserRole } from "./enums/userRole";

export interface UserProfile{
    Id:string;
    userName: string;
    email: string;
    firstName: string;
    isActive:boolean;
    lastName: string;
    role:UserRole;
    profileImage?:string;
    token?: string;
    bookings?:Booking[]
}