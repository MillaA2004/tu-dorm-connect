//export const MAPS_API_KEY = "AIzaSyAwW0GOAwZzixyaWgI-aES_dOcRlCpc1kI";
export const MAPS_API_KEY = import.meta.env.VITE_GOOGLE_MAPS_API_KEY as string;

export const DEFAULT_CENTER = {
  lat: 42.6977,
  lng: 23.3219, // Sofia example
};
