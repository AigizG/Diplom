export function Modal({
  title,
  children,
  onClose,
}: {
  title: string;
  children: React.ReactNode;
  onClose: () => void;
}) {
  return (
    <div className="modalBackdrop" role="dialog" aria-modal="true" onMouseDown={onClose}>
      <div className="modal" onMouseDown={(event) => event.stopPropagation()}>
        <div className="modalHeader">
          <h3>{title}</h3>
          <button className="iconButton" onClick={onClose} aria-label="Закрыть">x</button>
        </div>
        {children}
      </div>
    </div>
  );
}
