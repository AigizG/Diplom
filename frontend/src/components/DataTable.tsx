import { label } from '../utils';

export interface Column<T> {
  key: string;
  title: string;
  render?: (item: T) => React.ReactNode;
}

export function DataTable<T extends { id?: number | string }>({
  items,
  columns,
  actions,
  empty = 'Нет данных',
}: {
  items: T[];
  columns: Column<T>[];
  actions?: (item: T) => React.ReactNode;
  empty?: string;
}) {
  if (!items.length) return <div className="empty">{empty}</div>;
  return (
    <div className="tableWrap">
      <table>
        <thead>
          <tr>
            {columns.map((column) => (
              <th key={column.key}>{column.title}</th>
            ))}
            {actions && <th>Действия</th>}
          </tr>
        </thead>
        <tbody>
          {items.map((item, index) => (
            <tr key={item.id || index}>
              {columns.map((column) => (
                <td key={column.key}>{column.render ? column.render(item) : label((item as Record<string, unknown>)[column.key])}</td>
              ))}
              {actions && <td className="actionsCell">{actions(item)}</td>}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
