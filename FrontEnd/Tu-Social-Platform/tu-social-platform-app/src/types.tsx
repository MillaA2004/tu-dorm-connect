export interface LatLng {
  lat: number;
  lng: number;
}

export interface NewEvent {
  title: string;
  description: string;
  address: string;
  eventType: string;
  dateTime: string; 
  capacity: number;
  location: LatLng;
  
}

export interface EventItem extends NewEvent {
  id: number;
  creatorName: string;
}

