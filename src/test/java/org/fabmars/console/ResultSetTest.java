package org.fabmars.console;

import org.apache.derby.jdbc.EmbeddedDriver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static java.sql.ResultSet.CONCUR_READ_ONLY;
import static java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE;

/**
 * See http://db.apache.org/derby/docs/10.14/devguide/cdevdvlpinmemdb.html
 */
public class ResultSetTest extends ConsoleTableTest implements GadjoData {

  public static final String EMBEDDED_URL = "jdbc:derby:memory:console-table";
  public static final String SQL_CREATE = "CREATE TABLE gadjo (first_name VARCHAR(30) NOT NULL, last_name VARCHAR(30) NOT NULL, birth_date DATE, uses_windows BOOLEAN NOT NULL)";
  public static final String SQL_INSERT = "INSERT INTO gadjo (first_name, last_name, birth_date, uses_windows) VALUES(?, ?, ?, ?)";
  public static final String SQL_SELECT = "SELECT * FROM gadjo";

  private static Connection connection;

  @BeforeAll
  public static void prepare() throws SQLException {
    DriverManager.registerDriver(new EmbeddedDriver());
    connection = DriverManager.getConnection(EMBEDDED_URL + ";create=true");
    connection.createStatement().executeUpdate(SQL_CREATE);

    for(Gadjo gadjo : list) {
      if(gadjo != null) {
        PreparedStatement ps = connection.prepareStatement(SQL_INSERT);
        ps.setString(1, gadjo.getFirstName());
        ps.setString(2, gadjo.getLastName());
        ps.setDate(3, Date.valueOf(gadjo.getBirthDate()));
        ps.setBoolean(4, gadjo.isUsingWindows());
        int updates = ps.executeUpdate();
        Assertions.assertEquals(1, updates);
      }
    }
  }

  /**
   * shutdown=true will generate errorCode 50000 with state XJ015
   * drop=true will generate errorCode 45000 with state 08006
   * see http://zetcode.com/db/apachederbytutorial/jdbc/
   * @throws SQLException
   */
  @AfterAll
  public static void finish() throws SQLException {
    try {
      //DriverManager.getConnection(EMBEDDED_URL + ";drop=true");
      DriverManager.getConnection("jdbc:derby:;shutdown=true");
    }
    catch(SQLException e) {
      if (e.getErrorCode() != 50000 || !"XJ015".equals(e.getSQLState())) {
        throw e;
      }
    }
  }

  @Test
  public void testResultSet() throws SQLException {
    ResultSet rs = connection.createStatement(TYPE_SCROLL_INSENSITIVE, CONCUR_READ_ONLY).executeQuery(SQL_SELECT);
    ConsoleTable<ResultSet> consoleTable = new ResultSetConsoleTable(rs);
    testOutput("test-simple-resultset.txt", consoleTable);
  }
}
