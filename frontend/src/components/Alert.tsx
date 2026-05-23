export function Alert({ type = 'error', children }: { type?: 'error' | 'success' | 'info'; children?: React.ReactNode }) {
  if (!children) return null;
  return <div className={`alert ${type}`}>{children}</div>;
}
