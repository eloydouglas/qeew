const BASE = '/api/queues'

function parseJson(text) {
  if (!text) return null
  const safe = text.replace(/"id"\s*:\s*(\d+)/g, '"id":"$1"')
  return JSON.parse(safe)
}

async function request(path, options = {}) {
  const response = await fetch(`${BASE}${path}`, {
    cache: 'no-store',
    headers: { 'Content-Type': 'application/json', ...options.headers },
    ...options,
  })

  if (!response.ok) {
    throw new Error(`Request failed: ${response.status}`)
  }

  if (response.status === 204) {
    return null
  }

  return parseJson(await response.text())
}

export function listQueues() {
  return request('')
}

export function listProcesses(queueType) {
  return request(`/${queueType}/processes`)
}

export function listProcessed(queueType) {
  return request(`/${queueType}/processed`)
}

export function addProcess(queueType, priority) {
  return request(`/${queueType}`, {
    method: 'POST',
    body: JSON.stringify({ priority }),
  })
}

export function cancelProcess(queueType, id) {
  return request(`/${queueType}/processes/${id}/cancel`, {
    method: 'POST',
  })
}

export function clearProcessed(queueType) {
  return request(`/${queueType}/processed/clear`, {
    method: 'POST',
  })
}
