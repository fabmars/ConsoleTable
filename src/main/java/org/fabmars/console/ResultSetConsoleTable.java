package org.fabmars.console;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ResultSetConsoleTable extends ConsoleTable<ResultSet> {

  private final ResultSet rs;
  private int initialResultSetRow; // initial 0-based row
  private final int rows, cols;
  private final String[] headers;

  public ResultSetConsoleTable(ResultSet rs) throws SQLException {
    super(true);
    this.rs = rs;
    this.initialResultSetRow = rs.getRow(); //1-based

    ResultSetMetaData rsMetaData = rs.getMetaData();
    this.cols = rsMetaData.getColumnCount();
    this.rows = rs.last() ? rs.getRow() : 0;
    rs.beforeFirst(); // In all likelihood we'll scroll the table top to bottom.

    this.headers = new String[cols];
    for(int c = 0; c < cols; c++) {
      this.headers[c] = rsMetaData.getColumnLabel(c+1);
    }
  }


  @Override
  public int getColumnCount() {
    return cols;
  }

  @Override
  public int getRowCount() {
    return rows;
  }

  @Override
  public String getHeader(int c) {
    return headers[c];
  }

  /**
   * @param r 0-based row number (whereas ResultSet is 1-based)
   * @return the ResultSet positioned on the requested row
   */
  @Override
  protected ResultSet getRow(int r) {
    try {
      int current = rs.getRow() - 1; //to 0-based, will be -1 if we're positioned beforeFirst
      for(; r < current; current--) {
        rs.previous();
      }
      for(; r > current; current++) {
        rs.next();
      }
    }
    catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return rs;
  }

  @Override
  public Object getCell(ResultSet rs, int column) {
    try {
      return rs.getObject(column+1);
    }
    catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public ResultSet restoreInitialRow() {
    return getRow(initialResultSetRow -1); // -1: to 0-based
  }
}
