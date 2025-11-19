package net.spudacious5705.crackdown.DBOperations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Consumer;

public abstract class SQLOperation implements Consumer<Connection> {}
