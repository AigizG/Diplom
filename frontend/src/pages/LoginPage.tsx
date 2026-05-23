import { FormEvent, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import { Alert } from '../components/Alert';
import { enumLabel } from '../utils';

const testUsers = [
  ['admin@example.com', 'admin123', 'ADMIN'],
  ['manager@example.com', 'manager123', 'MANAGER'],
  ['instructor@example.com', 'instructor123', 'INSTRUCTOR'],
  ['client@example.com', 'client123', 'CLIENT'],
] as const;

export function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [email, setEmail] = useState('client@example.com');
  const [password, setPassword] = useState('client123');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const redirectTo = (location.state as { from?: { pathname?: string } } | null)?.from?.pathname || '/';

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setError('');
    if (!email || !password) {
      setError('Введите электронную почту и пароль');
      return;
    }
    setLoading(true);
    try {
      await login(email, password);
      navigate(redirectTo, { replace: true });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Ошибка входа');
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="page authPage">
      <div className="panel narrow">
        <h1>Вход</h1>
        <Alert>{error}</Alert>
        <form className="form" onSubmit={submit}>
          <label><span>Эл. почта</span><input type="email" value={email} onChange={(event) => setEmail(event.target.value)} /></label>
          <label><span>Пароль</span><input type="password" value={password} onChange={(event) => setPassword(event.target.value)} /></label>
          <button disabled={loading}>{loading ? 'Входим...' : 'Войти'}</button>
        </form>
        <div className="quickLogin">
          {testUsers.map(([mail, pass, role]) => (
            <button className="secondary" key={mail} onClick={() => { setEmail(mail); setPassword(pass); }}>{enumLabel(role)}</button>
          ))}
        </div>
      </div>
    </section>
  );
}


