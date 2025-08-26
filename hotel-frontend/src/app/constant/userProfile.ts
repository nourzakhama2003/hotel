export interface UserProfile{
    id:string;
    userName: string;
    email: string;
    firstName: string;
    isActive:boolean;
    lastName: string;
    role:string;
    profileImage?:string;
    token?: string;
}