# CAR

CAR is a distributed framework to analyze and visualize log files, or any text files. You upload files into a dataset, CAR detects the log type, parses each message into typed fields using regex rules, indexes the result, and lets you search it and build dashboards on top.

## What it does

- **Log type detection**: picks the right rule set for a file from its name and content.
- **Rule-based parsing**: regex rules built from reusable tokens (`regexTokens.json`) turn raw log lines into structured documents. Many rules are chained and evaluated together with Google's [re2j](https://github.com/google/re2j), so parsing time stays linear in the input.
- **Record readers**: single-line and multi-line log messages, CSV and JSON.
- **Pluggable stores**: parsed documents go to Solr, MySQL or SQLite through one `Store` interface.
- **Pluggable processing**: files are processed on a local thread pool, or split into blocks and processed across a cluster on [SDFS](https://github.com/mmpataki/sdfs).
- **Search and dashboards**: a Vue UI with search, tables, time-series and timeline charts, tree views and saved dashboards.
- **Collaboration**: users, groups, comments and notifications.

## Architecture

| Module | Purpose |
|---|---|
| `car-common` | Engine: detectors, record readers, rules, stores, filesystem layer |
| `car-ui` | Spring Boot REST API and the Vue front end |
| `car-solr-store` | Solr-backed index store |
| `car-sql-store` | MySQL and SQLite stores |
| `car-sdfs` | Filesystem adapter for SDFS |
| `car-nativecluster-processor` | Submits per-block parsing jobs to an SDFS cluster |
| `car-localfs`, `car-lucene-store` | Local filesystem and Lucene store modules (work in progress) |

The flow for one file:

1. The file is uploaded into a dataset and stored on the configured filesystem (local or SDFS).
2. A detector identifies its log type.
3. The processing engine reads records and evaluates the rules for that log type. On a cluster, one task runs per SDFS block, preferably on a node that holds the block.
4. Parsed documents are written in batches to the configured store.
5. The UI queries the store for search and dashboards.

## Quick start

You need Docker with Compose. `compose.yaml` builds SDFS from a sibling directory, so clone both repos side by side:

```
git clone https://github.com/mmpataki/car
git clone https://github.com/mmpataki/sdfs
cd car
docker compose up --build
```

This starts the CAR app, MySQL, Solr, and an SDFS namenode and datanode. Open http://localhost:8080 once the containers are healthy.

## Configuration

Settings live in `config.properties`. The main switches:

| Property | Meaning |
|---|---|
| `store.class` | Where parsed documents go, e.g. `car.engine.store.MySqlStore` |
| `index.store.class` | Search index, e.g. `car.engine.store.SolrStore` |
| `fs.class` | File storage, e.g. `car.fs.CarSDFileSystem` |
| `app.processing.engine.class` | Processing engine, e.g. `car.processing.engine.SdfsClusterProcessor` |
| `processor.threadpool.size` | Threads for local processing |

The credentials in `config.properties` and `compose.yaml` are local development defaults. Change them before running anywhere shared.

## Build without Docker

```
./mvnw install -DskipTests
```

The front end is in `car-ui/src/main/ui` (`npm install && npm run build`).
