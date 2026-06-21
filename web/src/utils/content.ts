export function parseList(value?: string | null): string[] {
  if (!value) return []

  try {
    const parsed = JSON.parse(value)
    if (Array.isArray(parsed)) {
      return parsed.map(String).map((item) => item.trim()).filter(Boolean)
    }
  } catch {
    // Continue with delimiter parsing.
  }

  return value
    .split(/[,，、\s]+/)
    .map((item) => item.trim())
    .filter(Boolean)
}

export function formatDate(value?: string | null): string {
  if (!value) return ''

  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value.slice(0, 10)

  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

export function fallbackText(value: string | null | undefined, fallback: string): string {
  return value && value.trim() ? value : fallback
}
