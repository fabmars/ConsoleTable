package org.fabmars.console;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ResultSetConsoleTable extends ConsoleTable<ResultSet> {

  private final ResultSet rs;
  private int initialResultSetRow; // initial 0-based row
  private final int rowCount, columnCount;
  private final String[] headers;

  public ResultSetConsoleTable(ResultSet rs) throws SQLException {
    super(true);
    this.rs = rs;
    this.initialResultSetRow = rs.getRow(); //1-based

    ResultSetMetaData rsMetaData = rs.getMetaData();
    this.columnCount = rsMetaData.getColumnCount();
    this.rowCount = rs.last() ? rs.getRow() : 0;
    rs.beforeFirst(); // In all likelihood we'll scroll the table top to bottom.

    this.headers = new String[columnCount];
    for(int colNum = 0; colNum < columnCount; colNum++) {
      this.headers[colNum] = rsMetaData.getColumnLabel(colNum+1);
    }
  }


  @Override
  public int getColumnCount() {
    return columnCount;
  }

  @Override
  public int getRowCount() {
    return rowCount;
  }

  @Override
  public String getHeader(int colNum) {
    return headers[colNum];
  }

  /**
   * @param rowNum 0-based row number (whereas ResultSet is 1-based)
   * @return the ResultSet positioned on the requested row
   */
  @Override
  public ResultSet getRow(int rowNum) {
    try {
      int current = rs.getRow() - 1; //to 0-based, will be -1 if we're positioned beforeFirst
      for(; rowNum < current; current--) {
        rs.previous();
      }
      for(; rowNum > current; current++) {
        rs.next();
      }
    }
    catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return rs;
  }

  @Override
  public Object getCell(ResultSet rs, int colNum) {
    try {
      return rs.getObject(colNum+1);
    }
    catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public ResultSet restoreInitialRow() {
    return getRow(initialResultSetRow -1); // -1: to 0-based
  }
}
