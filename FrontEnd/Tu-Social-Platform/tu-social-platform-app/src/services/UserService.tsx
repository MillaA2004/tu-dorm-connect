
import apiClient from "./apiClient";
import type { UserProfileData } from "../components/UserDetails";

const USERS_BASE = "/api/users";


export type UserDTO = {
  userId: number;
  firstName: string;
  lastName: string;
  email: string;
  password?: string | null;
  profileImageUrl: string | null;
  gender: "MALE" | "FEMALE" | string;
  major: string;
  year: number;
  role: string;
};

type UpdateUserPayload = {
  user: UserDTO;
  file?: File;
};


function mapDtoToProfile(dto: UserDTO): UserProfileData {
  return {
    firstName: dto.firstName,
    lastName: dto.lastName,
    major: dto.major,
    academicYear: dto.year,
    profileImageUrl: dto.profileImageUrl ?? "",
  };
}


export async function getUserById(
  userId: number
): Promise<{ dto: UserDTO; profile: UserProfileData }> {
  try {
    const res = await apiClient.get<UserDTO>(`${USERS_BASE}/${userId}`);
    const dto = res.data;
    return {
      dto,
      profile: mapDtoToProfile(dto),
    };
  } catch (err: any) {
    if (err?.response?.status === 404) {
      const e = new Error("USER_NOT_FOUND");
      (e as any).code = "USER_NOT_FOUND";
      throw e;
    }
    console.error("getUserById failed:", err?.response ?? err);
    throw err;
  }
}


export async function getUserByEmail(
  email: string
): Promise<{ dto: UserDTO; profile: UserProfileData }> {
  try {
    const idRes = await apiClient.get<{ userId: number }>(
      `${USERS_BASE}/id-by-email/${encodeURIComponent(email)}`
    );

    const userId = idRes.data.userId;
    if (userId === null || userId === undefined) {
      const err = new Error("USER_NOT_FOUND");
      (err as any).code = "USER_NOT_FOUND";
      throw err;
    }

    return getUserById(userId);
  } catch (err: any) {
    if (err?.response?.status === 404) {
      const notFound = new Error("USER_NOT_FOUND");
      (notFound as any).code = "USER_NOT_FOUND";
      throw notFound;
    }
    console.error("getUserByEmail failed:", err?.response ?? err);
    throw err;
  }
}



export async function updateUser(
  userId: number,
  payload: UpdateUserPayload
): Promise<{ dto: UserDTO; profile: UserProfileData }> {
  const formData = new FormData();

  formData.append(
    "user", 
    new Blob([JSON.stringify(payload.user)], {
      type: "application/json",
    })
  );

  if (payload.file) {
    formData.append("file", payload.file);
  }

  const res = await apiClient.put<UserDTO>(`${USERS_BASE}/${userId}`, formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });

  const dto = res.data;
  return {
    dto,
    profile: mapDtoToProfile(dto),
  };
}
