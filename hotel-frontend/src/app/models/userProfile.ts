import { Booking } from "./booking";
import { UserRole } from "./enums/userRole";

export interface UserProfile {
    id: number;
    userName: string;
    email: string;
    firstName: string;
    isActive: boolean;
    lastName: string;
    role: UserRole;
    profileImage?: string;
    faceAuthEnabled?: boolean;
    token?: string;
    bookings?: Booking[]
}