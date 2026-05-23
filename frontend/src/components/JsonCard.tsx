export function JsonCard({ data }: { data: unknown }) {
  return <pre className="jsonCard">{JSON.stringify(data, null, 2)}</pre>;
}
