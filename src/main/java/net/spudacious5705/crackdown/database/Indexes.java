package net.spudacious5705.crackdown.database;

public enum Indexes {

    PLAYER_IDS("player_uuids", "players(uuid)", true),
    ENTITY_IDS("entity_uuids", "entity(uuid)", false),
    DIMENSIONS("dimension_names", "dimension(name)", true),
    SOURCES("source_types", "source(type)", true),
    BLOCK_STATES("block_states", "block_state(block)", false),
    BLOCKS("block_types", "block(name)", true),
    BLOCK_ENTITY_POS("block_entity_positions", "block_entity(dimension,x,y,z)", false),
    ACTION_TYPE_BLOCK_ENTITY("action_types_block_entity", "block_entity_action_types(action)", true),
    ACTION_TYPE_BLOCK("action_types_block", "block_action_types(action)", true),
    ACTION_TYPE_ENTITY("action_types_entity", "entity_action_types(action)", true),
    ACTION_TYPE_PLAYER("action_types_player", "player_action_types(action)", true),
    ENTITIES("entity_types", "entity_type(name)", true),
    BLOCK_ENTITIES("block_entity_types", "block_entity_type(name)", true);

    final String SQL;
    final String NAME;
    Indexes(String name, String requirements, Boolean unique) {
        NAME = name;
        SQL = String.format("CREATE %sINDEX IF NOT EXISTS %s ON %s;", unique ? "UNIQUE " : "", name, requirements);
    }
}
