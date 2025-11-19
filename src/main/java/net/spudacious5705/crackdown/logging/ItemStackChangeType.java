package net.spudacious5705.crackdown.logging;

public enum ItemStackChangeType {
    REMOVED("removed",1),
    ADDED("added",3),
    SWAPPED("swapped",2);//todo should count as removed and added in search. SEARCH >1 removed. SEARCH <3 added.

    final String name;
    final int databaseID;
    ItemStackChangeType(String name, int databaseID) {
        this.name = name;
        this.databaseID = databaseID;
    }
}
