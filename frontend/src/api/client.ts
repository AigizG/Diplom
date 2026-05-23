import axios, { AxiosError } from 'axios';
import type { ApiErrorBody } from '../types';

const TOKEN_KEY = 'activeOffice.accessToken';

export const getStoredToken = () => localStorage.getItem(TOKEN_KEY);
export const setStoredToken = (token: string) => localStorage.setItem(TOKEN_KEY, token);
export const clearStoredToken = () => localStorage.removeItem(TOKEN_KEY);

export class ApiError extends Error {
  status?: number;

  constructor(message: string, status?: number) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
  }
}

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080',
  headers: { 'Content-Type': 'application/json' },
});

api.interceptors.request.use((config) => {
  const token = getStoredToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error: AxiosError<ApiErrorBody>) => {
    const status = error.response?.status;
    const message =
      error.response?.data?.message ||
      error.response?.data?.error ||
      error.message ||
      'Не удалось выполнить запрос';
    if (status === 401) {
      clearStoredToken();
      window.dispatchEvent(new Event('auth:unauthorized'));
    }
    return Promise.reject(new ApiError(message, status));
  },
);

export function toArray<T>(payload: unknown): T[] {
  if (Array.isArray(payload)) return payload as T[];
  if (payload && typeof payload === 'object') {
    const value = payload as { content?: T[]; items?: T[]; data?: T[] };
    return value.content || value.items || value.data || [];
  }
  return [];
}

export async function getList<T>(url: string, params?: Record<string, unknown>): Promise<T[]> {
  const { data } = await api.get(url, { params });
  return toArray<T>(data);
}

export async function getOne<T>(url: string): Promise<T> {
  const { data } = await api.get<T>(url);
  return data;
}
