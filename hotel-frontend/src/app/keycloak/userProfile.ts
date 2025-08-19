export interface UserProfile{
    id:string;
    userName: string;
    email: string;
    firstName: string;
    lastName: string;
    role:string


    profileImage?:string;
    token?: string;
}