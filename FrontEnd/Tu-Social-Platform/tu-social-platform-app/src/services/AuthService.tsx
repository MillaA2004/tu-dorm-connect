
import { jwtDecode } from "jwt-decode";
import apiClient from "./apiClient";
import { setToken, clearToken, getToken } from "./tokenStorage";
import {
  type AcademicYear,
  type AuthJwtPayload,
  type AuthUser,
  type JwtResponse,
  type RegisterFormValues,
} from "./authTypes";
import { getUserByEmail } from "./UserService";

const AUTH_BASE = "/auth";


function mapAcademicYearToInt(academicYear: AcademicYear): number {
  switch (academicYear) {
    case "freshman":
      return 1;
    case "sophomore":
      return 2;
    case "junior":
      return 3;
    case "senior":
      return 4;
    case "master":
        return 5;
    default:
      return 0;
  }
}


function buildRegisterFormData(form: RegisterFormValues): FormData {
  const formData = new FormData();

  const userDTO = {
    firstName: form.firstName,
    lastName: form.lastName,
    email: form.email,
    password: form.password,
    major: form.major,
    year: mapAcademicYearToInt(form.academicYear),
    gender: form.gender.toUpperCase(), 
    role: "User",
    profileImageUrl: null,
  };

  formData.append(
    "userDTO", 
    new Blob([JSON.stringify(userDTO)], { type: "application/json" })
  );

  if (form.imageFile) {
    formData.append("file", form.imageFile);
  }

  return formData;
}


function decodeTokenPayload(token: string): AuthJwtPayload {
  return jwtDecode<AuthJwtPayload>(token);
}


async function getAuthUserFromToken(token: string): Promise<AuthUser> {
  const payload = decodeTokenPayload(token);

  if (payload.exp && payload.exp * 1000 < Date.now()) {
    throw new Error("Token has expired");
  }

  const email = payload.sub;
  if (!email) {
    throw new Error("Token does not contain an email (sub)");
  }

  
  const { dto } = await getUserByEmail(email);

  return {
    id: dto.userId,
    email: dto.email,
    firstName: dto.firstName,
    lastName: dto.lastName,
    role: dto.role ?? "User",
  };
}



export async function registerUser(
  form: RegisterFormValues
): Promise<{ token: string; user: AuthUser }> {
  const formData = buildRegisterFormData(form);

  const response = await apiClient.post<JwtResponse>(
    `${AUTH_BASE}/register`,
    formData,
    {
      headers: { "Content-Type": "multipart/form-data" },
    }
  );

  const token = response.data.token;
  if (!token) {
    throw new Error("No token received from register endpoint.");
  }

  setToken(token);

  const user = await getAuthUserFromToken(token);
  return { token, user };
}


export interface LoginRequest {
  email: string;
  password: string;
}

export async function loginUser(
  payload: LoginRequest
): Promise<{ token: string; user: AuthUser }> {
  const response = await apiClient.post<JwtResponse>(
    `${AUTH_BASE}/login`,
    payload,
    {
      headers: { "Content-Type": "application/json" },
    }
  );

  const token = response.data.token;
  if (!token) {
    throw new Error("No token received from login endpoint.");
  }

  setToken(token);

  const user = await getAuthUserFromToken(token);
  return { token, user };
}


export async function restoreSessionFromStoredToken(): Promise<{
  token: string;
  user: AuthUser;
} | null> {
  const token = getToken();
  if (!token) return null;

  try {
    const user = await getAuthUserFromToken(token);
    return { token, user };
  } catch (e) {
    console.error("Failed to restore session from token:", e);
    clearToken();
    return null;
  }
}


export function logoutUser() {
  clearToken();
}




