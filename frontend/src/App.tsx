import { Navigate, Route, Routes } from 'react-router-dom';
import { RequireAuth } from './auth/RequireAuth';
import { Layout } from './layout/Layout';
import { ActivityDetailPage } from './pages/ActivityDetailPage';
import { CatalogPage } from './pages/CatalogPage';
import { ClientDashboard } from './pages/ClientDashboard';
import { ForbiddenPage } from './pages/ForbiddenPage';
import { HomePage } from './pages/HomePage';
import { InstructorDashboard } from './pages/InstructorDashboard';
import { LoginPage } from './pages/LoginPage';
import { ManagerDashboard } from './pages/ManagerDashboard';
import { RegisterPage } from './pages/RegisterPage';

export function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route index element={<HomePage />} />
        <Route path="activities" element={<CatalogPage />} />
        <Route path="activities/:id" element={<ActivityDetailPage />} />
        <Route path="login" element={<LoginPage />} />
        <Route path="register" element={<RegisterPage />} />
        <Route path="forbidden" element={<ForbiddenPage />} />
        <Route element={<RequireAuth roles={['CLIENT']} />}>
          <Route path="client" element={<ClientDashboard />} />
        </Route>
        <Route element={<RequireAuth roles={['MANAGER', 'ADMIN']} />}>
          <Route path="manager" element={<ManagerDashboard />} />
        </Route>
        <Route element={<RequireAuth roles={['INSTRUCTOR']} />}>
          <Route path="instructor" element={<InstructorDashboard />} />
        </Route>
        <Route element={<RequireAuth roles={['ADMIN']} />}>
          <Route path="admin" element={<ManagerDashboard adminMode />} />
        </Route>
        <Route path="*" element={<Navigate to="/" replace />} />
      </Route>
    </Routes>
  );
}
