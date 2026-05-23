import { Navigate, Outlet, useLocation } from 'react-router-dom';
import type { Role } from '../types';
import { useAuth } from './AuthContext';

export function RequireAuth({ roles }: { roles?: Role[] }) {
  const { loading, isAuthenticated, hasRole } = useAuth();
  const location = useLocation();

  if (loading) return <div className="page"><div className="panel">Проверяем сессию...</div></div>;
  if (!isAuthenticated) return <Navigate to="/login" replace state={{ from: location }} />;
  if (roles && !hasRole(roles)) return <Navigate to="/forbidden" replace />;
  return <Outlet />;
}
