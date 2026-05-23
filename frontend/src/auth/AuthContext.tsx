import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { clearStoredToken, getStoredToken, setStoredToken } from '../api/client';
import { authApi } from '../api/endpoints';
import type { AuthResponse, Role, UserDto } from '../types';

interface AuthContextValue {
  user: UserDto | null;
  token: string | null;
  loading: boolean;
  isAuthenticated: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (body: Record<string, unknown>) => Promise<void>;
  logout: () => void;
  hasRole: (roles: Role[]) => boolean;
}

const AuthContext = createContext<AuthContextValue | null>(null);

function extractToken(data: AuthResponse): string {
  const token = data.accessToken || data.token || data.jwt;
  if (!token) throw new Error('Бэкенд не вернул accessToken');
  return token;
}

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [token, setToken] = useState<string | null>(() => getStoredToken());
  const [user, setUser] = useState<UserDto | null>(null);
  const [loading, setLoading] = useState(true);

  const logout = useCallback(() => {
    clearStoredToken();
    setToken(null);
    setUser(null);
  }, []);

  const loadMe = useCallback(async () => {
    if (!getStoredToken()) {
      setLoading(false);
      return;
    }
    try {
      const currentUser = await authApi.me();
      setUser(currentUser);
    } catch {
      logout();
    } finally {
      setLoading(false);
    }
  }, [logout]);

  useEffect(() => {
    void loadMe();
    const handler = () => logout();
    window.addEventListener('auth:unauthorized', handler);
    return () => window.removeEventListener('auth:unauthorized', handler);
  }, [loadMe, logout]);

  const login = useCallback(async (email: string, password: string) => {
    const { data } = await authApi.login({ email, password });
    const nextToken = extractToken(data);
    setStoredToken(nextToken);
    setToken(nextToken);
    const currentUser = await authApi.me();
    setUser(data.user || currentUser);
  }, []);

  const register = useCallback(async (body: Record<string, unknown>) => {
    const { data } = await authApi.register(body);
    const nextToken = extractToken(data);
    setStoredToken(nextToken);
    setToken(nextToken);
    const currentUser = await authApi.me();
    setUser(data.user || currentUser);
  }, []);

  const value = useMemo<AuthContextValue>(
    () => ({
      user,
      token,
      loading,
      isAuthenticated: Boolean(user && token),
      login,
      register,
      logout,
      hasRole: (roles) => Boolean(user && roles.includes(user.role)),
    }),
    [loading, login, logout, register, token, user],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth должен использоваться внутри AuthProvider');
  return context;
}
