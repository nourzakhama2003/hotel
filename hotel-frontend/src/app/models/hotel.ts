import { Room } from "./room"

export interface Hotel {
    id: number,
    hotelName: string,
    hotelLocation:string,
    hotelImage?:string,
    rooms?: Room[]
}