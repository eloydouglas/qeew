# qeew

A simple generic queue manager. Add processes to typed queues, track their status as they run, cancel queued items, and clear completed history.

## Project structure

| Directory    | Description                          |
| ------------ | ------------------------------------ |
| `processes/` | REST API backend ([Quarkus](https://quarkus.io/)) |
| `ui/`        | Web UI ([React](https://react.dev/) + [Vite](https://vite.dev/)) |

Built-in queue types: `ORDER` and `PAYMENT`.

## Prerequisites

- **Backend:** Java 25+ and Maven (or use the included `./mvnw` wrapper)
- **UI:** [Node.js](https://nodejs.org/) 18+

## Getting started

### 1. Start the backend

```bash
cd processes
./mvnw quarkus:dev
```

The API runs at [http://localhost:8080](http://localhost:8080).

See the [Quarkus getting started guide](https://quarkus.io/guides/getting-started) for more on setup and tooling.

### 2. Start the UI

In a second terminal:

```bash
cd ui
npm install
npm run dev
```

Open the URL shown in the terminal (usually [http://localhost:5173](http://localhost:5173)). The UI proxies API requests to the backend on port 8080.

See the [Vite guide](https://vite.dev/guide/) for build and preview commands.

## API overview

| Method | Endpoint | Description |
| ------ | -------- | ----------- |
| `GET`  | `/queues` | List all queues with processes and processed items |
| `GET`  | `/queues/{type}/processes` | List active processes for a queue |
| `GET`  | `/queues/{type}/processed` | List processed items for a queue |
| `POST` | `/queues/{type}` | Add a process (`{"priority": 1}`) |
| `POST` | `/queues/{type}/processes/{id}/cancel` | Cancel a queued process |
| `POST` | `/queues/{type}/processed/clear` | Clear the processed history |

Queue type is case-insensitive (`payment`, `order`, etc.).

Example requests are in [`curl-examples.sh`](curl-examples.sh).

## UI features

- **Single queue** — select a queue, add processes, cancel queued items, clear processed history
- **All queues** — overview of every queue from a single request
