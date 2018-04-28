package org.fabmars.console;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ResultSetConsoleTable extends ConsoleTable<ResultSet> {

  private final ResultSet rs;
  private int initialResultSetRow; // initial 0-based row
  private final int rows, cols;
  private final String[] headers;
  private final DefaultConsoleCellRenderer defaultCellRenderer;

  public ResultSetConsoleTable(ResultSet rs) throws SQLException {
    this(rs, DefaultConsoleCellRenderer.EMPTY);
  }

  public ResultSetConsoleTable(ResultSet rs, String ifNull) throws SQLException {
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
    this.defaultCellRenderer = new DefaultConsoleCellRenderer(ifNull);
  }


  @Override
  public int getColumnCount() {
    return cols;
  }

  @Override
  public int getRowCount() {
    return rows;
  }

  /**
   * @param row 0-based row number (whereas ResultSet is 1-based)
   * @return the ResultSet positioned on the requested row
   */
  @Override
  protected ResultSet getRow(int row) {
    try {
      int current = rs.getRow() - 1; //to 0-based, will be -1 if we're positioned beforeFirst
      for(;row < current; current--) {
        rs.previous();
      }
      for(;row > current; current++) {
        rs.next();
      }
    }
    catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return rs;
  }

  @Override
  public String getHeader(int column) {
    return headers[column];
  }

  @Override
  public Object getCell(int row, int column) {
    try {
      return getRow(row).getObject(column+1);
    }
    catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public ConsoleCellRenderer getDefaultCellRenderer(Class<?> clazz) {
    return defaultCellRenderer;
  }

  public ResultSet restoreInitialRow() {
    return getRow(initialResultSetRow -1); // -1: to 0-based
  }
}
