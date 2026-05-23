import { enumLabel } from '../utils';

const toneMap: Record<string, string> = {
  ACTIVE: 'success',
  PLANNED: 'info',
  CONFIRMED: 'info',
  PAID: 'success',
  COMPLETED: 'success',
  PENDING: 'warning',
  NEW: 'warning',
  PARTIALLY_PAID: 'warning',
  POSTPONED: 'warning',
  CANCELLED: 'danger',
  REFUNDED: 'muted',
  HIDDEN: 'muted',
  ARCHIVED: 'muted',
  AVAILABLE: 'success',
  NEEDS_REPAIR: 'warning',
  WRITTEN_OFF: 'danger',
  ASSIGNED: 'info',
  RETURNED: 'success',
};

export function StatusBadge({ value }: { value?: string | null }) {
  if (!value) return <span className="badge muted">-</span>;
  return <span className={`badge ${toneMap[value] || 'muted'}`}>{enumLabel(value)}</span>;
}
