import { LogOut, Menu, UserRound } from 'lucide-react';
import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import { enumLabel, isManagerRole, userName } from '../utils';

export function Layout() {
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();

  const signOut = () => {
    logout();
    navigate('/');
  };

  return (
    <div className="shell">
      <aside className="sidebar">
        <div className="brand">
          <Menu size={22} />
          <div>
            <strong>Активный отдых</strong>
            <span>Управление программами и бронированиями</span>
          </div>
        </div>
        <nav>
          <NavLink to="/">Главная</NavLink>
          <NavLink to="/activities">Каталог</NavLink>
          {user?.role === 'CLIENT' && <NavLink to="/client">Кабинет клиента</NavLink>}
          {user?.role === 'INSTRUCTOR' && <NavLink to="/instructor">Кабинет инструктора</NavLink>}
          {isManagerRole(user?.role) && <NavLink to={user?.role === 'ADMIN' ? '/admin' : '/manager'}>{user.role === 'ADMIN' ? 'Администрирование' : 'Панель менеджера'}</NavLink>}
        </nav>
        <div className="sidebarFooter">
          {isAuthenticated && user ? (
            <>
              <div className="userPill">
                <UserRound size={18} />
                <div>
                  <strong>{userName(user)}</strong>
                  <span>{enumLabel(user.role)}</span>
                </div>
              </div>
              <button className="secondary full" onClick={signOut}><LogOut size={16} /> Выйти</button>
            </>
          ) : (
            <div className="authLinks">
              <NavLink to="/login">Вход</NavLink>
              <NavLink to="/register">Регистрация</NavLink>
            </div>
          )}
        </div>
      </aside>
      <main className="content">
        <Outlet />
      </main>
    </div>
  );
}
