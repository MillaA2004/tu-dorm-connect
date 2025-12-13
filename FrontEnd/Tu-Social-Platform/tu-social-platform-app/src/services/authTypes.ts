export type Gender = "male" | "female";

export type AcademicYear = "freshman" | "sophomore" | "junior" | "senior" | "master";

export interface RegisterFormValues {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  gender: Gender;
  major: string;
  academicYear: AcademicYear;
  imageFile: File | null;
}

export interface JwtResponse {
  token: string;
}



export interface AuthJwtPayload {
  sub: string; 
  exp?: number;
  firstName?: string;
  lastName?: string;
  role?: string;
}


export interface AuthUser {
  id: number;
  email: string;
  firstName?: string;
  lastName?: string;
  role: string;
}
