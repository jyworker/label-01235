export function toMediaUrl(url) {
  if (!url || typeof url !== 'string') return ''

  const trimmed = url.trim()
  if (!trimmed) return ''

  if (
    trimmed.startsWith('http://') ||
    trimmed.startsWith('https://') ||
    trimmed.startsWith('//') ||
    trimmed.startsWith('data:') ||
    trimmed.startsWith('blob:')
  ) {
    return trimmed
  }

  const normalized = trimmed.startsWith('/') ? trimmed : `/${trimmed}`
  if (typeof window === 'undefined') return normalized

  const customBase = (import.meta.env.VITE_MEDIA_BASE_URL || '').trim()
  if (customBase) {
    return `${customBase.replace(/\/$/, '')}${normalized}`
  }

  // 本地双前端端口场景兜底：用户端(8082)静态代理异常时回退到8081
  if (window.location.hostname === 'localhost' && window.location.port === '8082') {
    return `http://localhost:8081${normalized}`
  }

  return `${window.location.origin}${normalized}`
}
