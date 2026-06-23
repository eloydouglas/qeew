import { useCallback, useEffect, useRef, useState } from 'react'
import {
  addProcess,
  cancelProcess,
  clearProcessed,
  listProcessed,
  listProcesses,
  listQueues,
} from './api'
import './App.css'

function ProcessTable({ processes, emptyMessage, showActions, onCancel }) {
  if (processes.length === 0) {
    return <p className="empty">{emptyMessage}</p>
  }

  return (
    <table>
      <thead>
        <tr>
          <th>ID</th>
          <th>Status</th>
          <th>Priority</th>
          {showActions && <th></th>}
        </tr>
      </thead>
      <tbody>
        {processes.map((p) => (
          <tr key={p.id}>
            <td>{p.id}</td>
            <td>
              <span className={`status status-${p.status.toLowerCase()}`}>
                {p.status}
              </span>
            </td>
            <td>{p.priority}</td>
            {showActions && (
              <td>
                {p.status === 'QUEUED' && (
                  <button type="button" onClick={() => onCancel(p.id)}>
                    Cancel
                  </button>
                )}
              </td>
            )}
          </tr>
        ))}
      </tbody>
    </table>
  )
}

function ConfirmModal({ open, title, message, confirmLabel, onConfirm, onCancel, loading }) {
  if (!open) return null

  return (
    <div className="modal-overlay" onClick={onCancel}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <h3>{title}</h3>
        <p>{message}</p>
        <div className="modal-actions">
          <button type="button" className="btn-secondary" onClick={onCancel}>
            Cancel
          </button>
          <button type="button" className="btn-danger" onClick={onConfirm} disabled={loading}>
            {confirmLabel}
          </button>
        </div>
      </div>
    </div>
  )
}

function AllQueuesView({ queues }) {
  if (queues.length === 0) {
    return <p className="empty">No queues found.</p>
  }

  return (
    <div className="all-queues">
      {queues.map((queue) => (
        <section key={queue.queueType} className="queue-block">
          <h2>{queue.queueType}</h2>
          <div className="panels">
            <section className="panel">
              <h3>Processes</h3>
              <ProcessTable
                processes={queue.processes ?? []}
                emptyMessage="No processes in this queue."
              />
            </section>
            <section className="panel">
              <h3>Processed</h3>
              <ProcessTable
                processes={queue.processed ?? []}
                emptyMessage="No processed items yet."
              />
            </section>
          </div>
        </section>
      ))}
    </div>
  )
}

function App() {
  const [view, setView] = useState('single')
  const [queues, setQueues] = useState([])
  const [selectedType, setSelectedType] = useState('')
  const [processes, setProcesses] = useState([])
  const [processed, setProcessed] = useState([])
  const [priority, setPriority] = useState(1)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [showClearConfirm, setShowClearConfirm] = useState(false)
  const [clearing, setClearing] = useState(false)
  const loadRequestId = useRef(0)

  const loadQueues = useCallback(async () => {
    const data = await listQueues()
    setQueues(data)
    if (data.length > 0 && !selectedType) {
      setSelectedType(data[0].queueType)
    }
    return data
  }, [selectedType])

  const loadQueueData = useCallback(async () => {
    if (!selectedType) return
    const requestId = ++loadRequestId.current
    const type = selectedType.toLowerCase()
    const [active, done] = await Promise.all([
      listProcesses(type),
      listProcessed(type),
    ])

    if (requestId !== loadRequestId.current) return

    setProcesses(active ?? [])
    setProcessed(done ?? [])
  }, [selectedType])

  const refresh = useCallback(async () => {
    setError('')
    setLoading(true)
    try {
      await loadQueues()
      if (view === 'single') {
        await loadQueueData()
      }
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }, [loadQueues, loadQueueData, view])

  useEffect(() => {
    refresh()
    const poll = view === 'all' ? loadQueues : loadQueueData
    const interval = setInterval(poll, 2000)
    return () => clearInterval(interval)
  }, [refresh, loadQueues, loadQueueData, view])

  async function handleAdd(e) {
    e.preventDefault()
    setError('')
    try {
      await addProcess(selectedType.toLowerCase(), Number(priority))
      await loadQueueData()
      if (view === 'all') await loadQueues()
    } catch (err) {
      setError(err.message)
    }
  }

  async function handleCancel(id) {
    setError('')
    try {
      const updated = await cancelProcess(selectedType.toLowerCase(), id)
      if (updated) {
        setProcesses((prev) =>
          prev.map((p) => (p.id === updated.id ? updated : p)),
        )
      }
      await loadQueueData()
      if (view === 'all') await loadQueues()
    } catch (err) {
      setError(err.message)
    }
  }

  async function handleClearProcessed() {
    setError('')
    setClearing(true)
    try {
      await clearProcessed(selectedType.toLowerCase())
      setProcessed([])
      setShowClearConfirm(false)
      await loadQueueData()
    } catch (err) {
      setError(err.message)
    } finally {
      setClearing(false)
    }
  }

  return (
    <div className="app">
      <header>
        <h1>Queue Manager</h1>
        <div className="header-actions">
          <div className="view-toggle">
            <button
              type="button"
              className={view === 'single' ? 'active' : ''}
              onClick={() => setView('single')}
            >
              Single queue
            </button>
            <button
              type="button"
              className={view === 'all' ? 'active' : ''}
              onClick={() => setView('all')}
            >
              All queues
            </button>
          </div>
          <button type="button" onClick={refresh} disabled={loading}>
            Refresh
          </button>
        </div>
      </header>

      {error && <p className="error">{error}</p>}

      {view === 'single' && (
        <>
          <section className="controls">
            <label>
              Queue type
              <select
                value={selectedType}
                onChange={(e) => setSelectedType(e.target.value)}
              >
                {queues.map((q) => (
                  <option key={q.queueType} value={q.queueType}>
                    {q.queueType}
                  </option>
                ))}
              </select>
            </label>

            <form onSubmit={handleAdd}>
              <label>
                Priority
                <input
                  type="number"
                  min="1"
                  value={priority}
                  onChange={(e) => setPriority(e.target.value)}
                />
              </label>
              <button type="submit">Add process</button>
            </form>
          </section>

          <div className="panels">
            <section className="panel">
              <h2>Processes — {selectedType}</h2>
              <ProcessTable
                processes={processes}
                emptyMessage="No processes in this queue."
                showActions
                onCancel={handleCancel}
              />
            </section>

            <section className="panel">
              <div className="panel-header">
                <h2>Processed — {selectedType}</h2>
                <button
                  type="button"
                  className="btn-secondary"
                  onClick={() => setShowClearConfirm(true)}
                  disabled={processed.length === 0}
                >
                  Clear processed
                </button>
              </div>
              <ProcessTable
                processes={processed}
                emptyMessage="No processed items yet."
              />
            </section>
          </div>
        </>
      )}

      {view === 'all' && <AllQueuesView queues={queues} />}

      <ConfirmModal
        open={showClearConfirm}
        title="Clear processed queue?"
        message={`This will remove all processed items from the ${selectedType} queue. This action cannot be undone.`}
        confirmLabel={clearing ? 'Clearing…' : 'Clear'}
        onConfirm={handleClearProcessed}
        onCancel={() => setShowClearConfirm(false)}
        loading={clearing}
      />
    </div>
  )
}

export default App
