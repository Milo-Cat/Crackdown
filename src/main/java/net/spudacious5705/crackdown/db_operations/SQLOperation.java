package net.spudacious5705.crackdown.db_operations;

import java.sql.Connection;
import java.util.function.Consumer;

public abstract class SQLOperation implements Consumer<Connection> {}
