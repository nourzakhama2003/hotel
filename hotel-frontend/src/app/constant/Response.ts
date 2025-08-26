import { hotel } from "./hotel";
import { UserProfile } from "./userProfile";

export interface AppResponse {
    status:number,
    message:string,
    user?:UserProfile,
    users?:UserProfile[],
    hotel?:hotel,
    hotels?:hotel[],
    timestamp:string

}