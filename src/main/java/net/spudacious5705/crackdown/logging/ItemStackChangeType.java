package net.spudacious5705.crackdown.logging;

public enum ItemStackChangeType {
    REMOVED("removed",1),
    ADDED("added",3),
    SWAPPED("swapped",2);

    final String name;
    final int databaseID;
    ItemStackChangeType(String name, int databaseID) {
        this.name = name;
        this.databaseID = databaseID;
    }
}
