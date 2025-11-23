## crackdown is not yet released.
THis mod is an experimental/bespoke piece of software currently for individual use.
# CRACKDOWN
crackdown is a player-interaction logging mod designed to keep your forge server protected

It logs in detail changes to blocks, block entities and entities.
See here for a [list of capabilities](#LOGGING-CAPABILITIES).

### future updates
- command based searches
- command based rollbacks
- config for various things
- visual highlighting of changed areas/blocks
- gui for searching
- gui for rollbacks

### pre-requisites
this mod uses an SQLite database and therefore requires [SQLite JDBC for Minecraft](https://modrinth.com/plugin/sqlite-jdbc).
Huge thanks to Kosmolot for saving me the headache of setting up JDBC.

### bug-reporting/contact
I don't always notice bug reports immediate so feel free to ping me on discord [@the_spud]() or find me in [Farwater: mod-dev](https://discord.gg/Gqgwahdfpp)


### LOGGING CAPABILITIES
- lazy intervaled backups for block-entities and entities
- container modification tracking
- block break/change/interact logging
- block-entity and entity NBT difference comparison after a player interaction
- entity kills
- entity ride tracking
- player join/leave
- player start raid
- player advancement
- player item pickup/drop