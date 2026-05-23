import { FormEvent, useState } from 'react';
import { enumLabel } from '../utils';
import { Alert } from './Alert';

export type FieldType = 'text' | 'email' | 'password' | 'number' | 'date' | 'datetime-local' | 'textarea' | 'select';

export interface FieldConfig {
  name: string;
  label: string;
  type?: FieldType;
  required?: boolean;
  options?: string[];
  placeholder?: string;
}

export function SmartForm({
  fields,
  submitText,
  initialValues = {},
  onSubmit,
  onCancel,
}: {
  fields: FieldConfig[];
  submitText: string;
  initialValues?: Record<string, unknown>;
  onSubmit: (values: Record<string, unknown>) => Promise<void>;
  onCancel?: () => void;
}) {
  const [values, setValues] = useState<Record<string, unknown>>(initialValues);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const update = (name: string, value: string) => {
    const field = fields.find((item) => item.name === name);
    setValues((current) => ({ ...current, [name]: field?.type === 'number' && value !== '' ? Number(value) : value }));
  };

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setError('');
    setSuccess('');
    for (const field of fields) {
      if (field.required && !values[field.name]) {
        setError(`Заполните поле "${field.label}"`);
        return;
      }
    }
    setLoading(true);
    try {
      await onSubmit(values);
      setSuccess('Сохранено');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Ошибка сохранения');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form className="form" onSubmit={submit}>
      <Alert>{error}</Alert>
      <Alert type="success">{success}</Alert>
      <div className="formGrid">
        {fields.map((field) => (
          <label key={field.name}>
            <span>{field.label}</span>
            {field.type === 'textarea' ? (
              <textarea
                required={field.required}
                placeholder={field.placeholder}
                value={String(values[field.name] ?? '')}
                onChange={(event) => update(field.name, event.target.value)}
              />
            ) : field.type === 'select' ? (
              <select
                required={field.required}
                value={String(values[field.name] ?? '')}
                onChange={(event) => update(field.name, event.target.value)}
              >
                <option value="">Выберите</option>
                {field.options?.map((option) => (
                  <option key={option} value={option}>{enumLabel(option)}</option>
                ))}
              </select>
            ) : (
              <input
                type={field.type || 'text'}
                required={field.required}
                placeholder={field.placeholder}
                value={String(values[field.name] ?? '')}
                onChange={(event) => update(field.name, event.target.value)}
              />
            )}
          </label>
        ))}
      </div>
      <div className="formActions">
        {onCancel && <button type="button" className="secondary" onClick={onCancel}>Отмена</button>}
        <button disabled={loading}>{loading ? 'Сохраняем...' : submitText}</button>
      </div>
    </form>
  );
}
