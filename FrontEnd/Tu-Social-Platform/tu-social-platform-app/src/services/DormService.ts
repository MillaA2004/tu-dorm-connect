import apiClient from "./apiClient";

// Match your backend DTOs
export interface DormRequestDTO {
  name: string;
  address: string;
  description: string;
  price: number;
  imageUrlsList?: string[];
  latitude: number;
  longitude: number;
}

export interface DormUpdateRequestDTO {
  name?: string;
  address?: string;
  description?: string;
  price?: number;
  latitude?: number;
  longitude?: number;
  replaceImages?: boolean;
}

export interface DormResponseDTO {
  id: number;
  name: string;
  address: string;
  description: string;
  price: number;
  imageUrlsList: string[];
  latitude: number;
  longitude: number;
  reviews: any[]; // type later if you want
}

export type DormMapItem = {
  id: number;
  name: string;
  address: string;
  price: number;
  imageUrl?: string;
  lat: number;
  lng: number;
};

export type DormUI = {
  id: number;
  name: string;
  address: string;
  price: number;
  imageUrl?: string;
};

const DORMS_BASE = "/api/dorms";

// Helper: build multipart/form-data that Spring @RequestPart("dto") expects
function buildDormFormData(
  dto: DormRequestDTO | DormUpdateRequestDTO,
  files?: File[]
): FormData {
  const form = new FormData();

  // IMPORTANT: Send dto as JSON Blob so Spring can parse it as object
  form.append(
    "dto",
    new Blob([JSON.stringify(dto)], { type: "application/json" })
  );

  if (files?.length) {
    files.forEach((file) => form.append("files", file));
  }

  return form;
}

export const dormService = {
  // GET /api/dorms
  getAllDorms: async (): Promise<DormResponseDTO[]> => {
    const res = await apiClient.get<DormResponseDTO[]>(DORMS_BASE);
    return res.data;
  },

  // GET /api/dorms/{id}
  getDormById: async (id: number): Promise<DormResponseDTO> => {
    const res = await apiClient.get<DormResponseDTO>(`${DORMS_BASE}/${id}`);
    return res.data;
  },

  // POST /api/dorms/dorms (multipart)
  createDorm: async (
    dto: DormRequestDTO,
    files?: File[]
  ): Promise<DormResponseDTO> => {
    const formData = buildDormFormData(dto, files);

    const res = await apiClient.post<DormResponseDTO>(
      `${DORMS_BASE}/dorms`,
      formData,
      {
        // Let Axios set boundary automatically; just declare multipart.
        headers: { "Content-Type": "multipart/form-data" },
      }
    );

    return res.data;
  },

  // PUT /api/dorms/dorms/{id} (multipart)
  updateDorm: async (
    id: number,
    dto: DormUpdateRequestDTO,
    files?: File[]
  ): Promise<DormResponseDTO> => {
    const formData = buildDormFormData(dto, files);

    const res = await apiClient.put<DormResponseDTO>(
      `${DORMS_BASE}/dorms/${id}`,
      formData,
      {
        headers: { "Content-Type": "multipart/form-data" },
      }
    );

    return res.data;
  },

  // DELETE /api/dorms/{id}
  deleteDorm: async (id: number): Promise<void> => {
    await apiClient.delete(`${DORMS_BASE}/${id}`);
  },
};
