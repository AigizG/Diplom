import { Modal } from './Modal';

export function ConfirmModal({
  title = 'Подтвердите действие',
  message,
  confirmText = 'Подтвердить',
  cancelText = 'Отмена',
  loading = false,
  danger = false,
  onConfirm,
  onCancel,
}: {
  title?: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
  loading?: boolean;
  danger?: boolean;
  onConfirm: () => void;
  onCancel: () => void;
}) {
  return (
    <Modal title={title} onClose={onCancel}>
      <div className="confirmBody">
        <p>{message}</p>
        <div className="formActions">
          <button type="button" className="secondary" disabled={loading} onClick={onCancel}>{cancelText}</button>
          <button type="button" className={danger ? 'danger' : undefined} disabled={loading} onClick={onConfirm}>
            {loading ? 'Выполняем...' : confirmText}
          </button>
        </div>
      </div>
    </Modal>
  );
}
