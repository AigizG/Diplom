import { Modal } from './Modal';

export interface ConfirmState {
  title: string;
  text: string;
  confirmText?: string;
  danger?: boolean;
  onConfirm: () => void | Promise<void>;
}

export function ConfirmModal({
  confirm,
  loading,
  onClose,
}: {
  confirm: ConfirmState;
  loading?: boolean;
  onClose: () => void;
}) {
  return (
    <Modal title={confirm.title} onClose={onClose}>
      <div className="confirmBody">
        <p>{confirm.text}</p>
        <div className="formActions">
          <button type="button" className="secondary" disabled={loading} onClick={onClose}>Отмена</button>
          <button type="button" className={confirm.danger ? 'danger' : undefined} disabled={loading} onClick={() => void confirm.onConfirm()}>
            {loading ? 'Выполняем...' : confirm.confirmText || 'Подтвердить'}
          </button>
        </div>
      </div>
    </Modal>
  );
}
