package net.spudacious5705.crackdown.database;

public enum Tables {
    PLAYERS(
            "players",
            """
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            uuid TEXT NOT NULL UNIQUE,
            name TEXT NOT NULL
            """),

    PLAYER_CONNECTION(
            "connections",
            """
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            timestamp INTEGER NOT NULL,
            player INTEGER NOT NULL,
            joined BOOLEAN NOT NULL,
            FOREIGN KEY (player) REFERENCES players(id) ON DELETE CASCADE
            """),

    DIMENSION(
            "dimension",
            """
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL UNIQUE
                    """),

    BLOCK(
            "block",
            """
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name VARCHAR(191) NOT NULL UNIQUE
            """),

    BLOCK_STATE(
            "block_state",
            """
            block INTEGER NOT NULL,
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            state TEXT NOT NULL,
            UNIQUE (block, state),
            FOREIGN KEY (block) REFERENCES block(id) ON DELETE CASCADE
            """),

    BLOCK_INTERACTION(
            "block_interaction",
            """
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            timestamp INTEGER NOT NULL,
            x INTEGER NOT NULL,
            y INTEGER NOT NULL,
            z INTEGER NOT NULL,
            dimension INTEGER NOT NULL,
            rolled_back BOOLEAN NOT NULL DEFAULT FALSE,
            source INTEGER NOT NULL,
            player INTEGER,
            action INTEGER NOT NULL,
            block_new INTEGER NOT NULL,
            state_new INTEGER,
            block_old INTEGER,
            state_old INTEGER,
            nbt TEXT,
            
            FOREIGN KEY (dimension) REFERENCES dimension(id),
            
            FOREIGN KEY (player) REFERENCES players(id),
            FOREIGN KEY (source) REFERENCES source(id),
            FOREIGN KEY (action) REFERENCES block_action_types(id),
            
            FOREIGN KEY (block_new) REFERENCES block(id),
            FOREIGN KEY (state_new) REFERENCES block_state(id),
            FOREIGN KEY (block_old) REFERENCES block(id),
            FOREIGN KEY (state_old) REFERENCES block_state(id)
            """),

    SOURCE(
            "source",
            """
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            type TEXT NOT NULL UNIQUE
            """),

    BLOCK_ACTION_TYPES(
            "block_action_types",
            """
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            action VARCHAR(24) NOT NULL UNIQUE
            """),

    //todo make sure block entites are given DB_IDs
    BLOCK_ENTITY(
            "block_entity",
            """
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            x INTEGER NOT NULL,
            y INTEGER NOT NULL,
            z INTEGER NOT NULL,
            dimension INTEGER NOT NULL,
            type INTEGER NOT NULL,
            created_at INTEGER NOT NULL,
            destroyed_at INTEGER,
            last_backup_check_at INTEGER NOT NULL,
            FOREIGN KEY (type) REFERENCES block_entity_type(id),
            FOREIGN KEY (dimension) REFERENCES dimension(id)
            """),

    BLOCK_ENTITY_TYPE(
            "block_entity_type",
            """
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name VARCHAR(191) NOT NULL UNIQUE
            """),

    BLOCK_ENTITY_ACTION_TYPES(
            "block_entity_action_types",
            """
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            action VARCHAR(24) NOT NULL UNIQUE
            """),

    BLOCK_ENTITY_INTERACTION(
            "block_entity_interaction",
            """
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            timestamp INTEGER NOT NULL,
            block_entity INTEGER NOT NULL,
            source INTEGER NOT NULL,
            player INTEGER,
            action INTEGER NOT NULL,
            info TEXT,
            
            FOREIGN KEY (source) REFERENCES source(id),
            FOREIGN KEY (action) REFERENCES block_entity_action_types(id),
            FOREIGN KEY (block_entity) REFERENCES block_entity(id)
            """),

    BLOCK_ENTITY_BACKUP_RECORD(
            "block_backup_record",
            """
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            block_entity INTEGER NOT NULL,
            created_at INTEGER NOT NULL,
            compression INTEGER NOT NULL,
            checksum TEXT NOT NULL,
            FOREIGN KEY (block_entity) REFERENCES block_entity(id),
            FOREIGN KEY (compression) REFERENCES compression_type(id)
            """),

    BLOCK_ENTITY_BLOB(
            "block_nbt_blob",
            """
            record INTEGER PRIMARY KEY,
            data BLOB NOT NULL,
            FOREIGN KEY (record) REFERENCES block_backup_record(id) ON DELETE CASCADE
            """),

    ENTITY_ACTION_TYPES(
            "entity_action_types",
            """
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            action VARCHAR(24) NOT NULL UNIQUE
            """),

    ENTITY_TYPE(
            "entity_type",
            """
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name VARCHAR(191) NOT NULL UNIQUE
            """),

    ENTITY(
            "entity",
                    """
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            uuid TEXT NOT NULL,
            type INTEGER NOT NULL,
            created_at INTEGER NOT NULL,
            killed_at INTEGER,
            last_backup_check_at INTEGER NOT NULL,
            FOREIGN KEY (type) REFERENCES entity_type(id)
            """),

    ENTITY_INTERACTION(
            "entity_interaction",
                    """
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            timestamp INTEGER NOT NULL,
            x INTEGER NOT NULL,
            y INTEGER NOT NULL,
            z INTEGER NOT NULL,
            dimension INTEGER NOT NULL,
            entity INTEGER NOT NULL,
            source INTEGER NOT NULL,
            player INTEGER,
            action INTEGER NOT NULL,
            info TEXT,
            
            FOREIGN KEY (source) REFERENCES source(id),
            FOREIGN KEY (action) REFERENCES entity_action_types(id),
            FOREIGN KEY (entity) REFERENCES entity(id)
            """),

    ENTITY_BACKUP_RECORD(
            "entity_backup_record",
                    """
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            entity INTEGER NOT NULL,
            created_at INTEGER NOT NULL,
            checksum TEXT NOT NULL,
            compression INTEGER NOT NULL,
            FOREIGN KEY (entity) REFERENCES entity(id),
            FOREIGN KEY (compression) REFERENCES compression_type(id)
            """),

    ENTITY_BLOB(
            "entity_nbt_blob",
                    """
            record INTEGER PRIMARY KEY,
            data BLOB NOT NULL,
            FOREIGN KEY (record) REFERENCES entity_backup_record(id) ON DELETE CASCADE
            """),

    COMPRESSION_TYPE(
            "compression_type",
            """
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            version TEXT NOT NULL,
            UNIQUE(name,version)
            """);

    final String SQL;
    final String NAME;

    Tables(String name, String requirements) {
        NAME = name;
        SQL = String.format("CREATE TABLE IF NOT EXISTS %s (%s);", name, requirements);
    }
}

