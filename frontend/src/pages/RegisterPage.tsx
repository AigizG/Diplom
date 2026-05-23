import { useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import { SmartForm } from '../components/SmartForm';

export function RegisterPage() {
  const { register } = useAuth();
  const navigate = useNavigate();

  return (
    <section className="page authPage">
      <div className="panel narrow">
        <h1>Регистрация клиента</h1>
        <SmartForm
          submitText="Зарегистрироваться"
          fields={[
            { name: 'fullName', label: 'ФИО', required: true },
            { name: 'email', label: 'Эл. почта', type: 'email', required: true },
            { name: 'phone', label: 'Телефон' },
            { name: 'password', label: 'Пароль', type: 'password', required: true },
          ]}
          initialValues={{ role: 'CLIENT' }}
          onSubmit={async (values) => {
            await register({ ...values, role: 'CLIENT' });
            navigate('/client');
          }}
        />
      </div>
    </section>
  );
}

