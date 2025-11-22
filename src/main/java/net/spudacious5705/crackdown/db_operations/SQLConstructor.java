package net.spudacious5705.crackdown.db_operations;

public abstract class SQLConstructor<T extends SQLOperation> {
    abstract T create();
}
