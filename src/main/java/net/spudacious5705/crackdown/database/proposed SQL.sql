"""
SQLite’s JSON1 extension provides JSON functions (json_extract, json_set, json_each, etc.)
so you can store semi‑structured data in TEXT and query parts of it inside SQL.
It’s handy for NBT/JSON snippets or storing small metadata without separate columns.

Recommended PRAGMA settings
PRAGMA foreign_keys = ON — enable on every JDBC Connection right after opening it to enforce referential integrity and catch logic bugs early.

PRAGMA journal_mode = WAL — switch to WAL to reduce writer pause time and avoid rollback‑journal overhead; it also helps if you later add readers while writing.

PRAGMA synchronous = NORMAL — keeps good durability while avoiding the extra fsync cost of FULL; it’s a common balance for logging workloads.

PRAGMA busy_timeout = 5000 (ms) — prevents transient SQLITE_BUSY errors by waiting a short time for locks instead of failing immediately.

Optional: PRAGMA wal_autocheckpoint = 1000 to control WAL growth and checkpoint frequency, or run periodic checkpoints from a background task.

These are applied per connection (except journal_mode which persists in the DB file), so run them in your connection initialization routine."""

CREATE TABLE IF NOT EXISTS tal(
id INTEGER PRIMARY KEY AUTOINCREMENT,
back BOOLEAN NOT NULL DEFAULT VALUE FALSE,

)