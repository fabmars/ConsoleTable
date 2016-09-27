package org.fabmars.console;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.fabmars.console.Utils.padCenter;
import static org.fabmars.console.Utils.padLeft;
import static org.fabmars.console.Utils.padRight;


/**
 * Created by fabmars on 17/09/16.
 */
public abstract class ConsoleTable<T> {

  public static final String BORDER_LEFT = "|";
  public static final String COLUMN_SEPARATOR = BORDER_LEFT;
  public static final String BORDER_RIGHT = BORDER_LEFT;
  public static final char BORDER_PADDING = ' ';
  public static final char BORDER_TOP_BOTTOM = '=';
  public static final char BORDER_LINE = '-';

  protected final List<T> list;
  private boolean headers;
  private int[] widths;


  public ConsoleTable(Collection<T> col) {
    this(col, true);
  }

  public ConsoleTable(Collection<T> col, boolean headers) {
    this.list = new ArrayList<>(col);
    this.headers = headers;
  }

  public abstract int getColumnCount();

  public final int getRowCount() {
    return list.size();
  }
  protected final T getRow(int row) {
    return list.get(row);
  }

  public boolean isHeaders() {
    return headers;
  }

  public abstract Object getHeader(int column);

  public abstract Object getCell(int row, int column);


  public final int getWidth(int column) {
    if(widths == null) {
      widths = calcWidths();
    }
    return widths[column];
  }

  protected int[] calcWidths() {
    int columns = getColumnCount();
    int[] widths = new int[columns];

    if(isHeaders()) {
      for (int c = 0; c < columns; c++) {
        widths[c] = String.valueOf(renderHeader(getHeader(c), c)).length();
      }
    }

    int rows = getRowCount();
    for(int r = 0; r < rows; r++) {
      for(int c = 0; c < columns; c++) {
        Object cellValue = getCell(r, c);
        int len = String.valueOf(renderCell(cellValue, r, c)).length();
        if(widths[c] < len) {
          widths[c] = len;
        }
      }
    }

    return widths;
  }


  public Align getAlignment(int column) {
    return Align.RIGHT;
  }

  public ConsoleCellRenderer getHeaderRenderer(int column) {
    return null;
  }

  public ConsoleCellRenderer getCellRenderer(int row, int column) {
    return null;
  }

  public Class<?> getColumnClass(int column) {
    return Object.class;
  }

  public ConsoleCellRenderer getDefaultCellRenderer(Class<?> clazz) {
    return DefaultConsoleCellRenderer.instance;
  }



  public void stream(OutputStream os) throws IOException {
    PrintStream ps = new PrintStream(os);
    int rows = getRowCount();
    int columns = getColumnCount();

    int borderlessWidth = 3 * (columns-1);
    for(int c = 0; c < columns; c++) {
      borderlessWidth += getWidth(c);
    }

    List<String> rowValues;

    //top
    separator(ps, BORDER_TOP_BOTTOM, borderlessWidth);

    //headers
    rowValues = new ArrayList<>(columns);

    if(isHeaders()) {
      ps.append(BORDER_LEFT).append(BORDER_PADDING);
      for (int c = 0; c < columns; c++) {
        Object headerValue = getHeader(c);
        String headerString = String.valueOf(renderHeader(headerValue, c));
        rowValues.add(pad(headerString, getWidth(c), getAlignment(c)));
      }
      ps.append(String.join(BORDER_PADDING + COLUMN_SEPARATOR + BORDER_PADDING, rowValues));
      ps.append(BORDER_PADDING).println(BORDER_RIGHT);

      //separator
      separator(ps, BORDER_TOP_BOTTOM, borderlessWidth);
    }

    //table data
    for(int r = 0; r < rows; r++) {
      rowValues = new ArrayList<>(columns);
      ps.append(BORDER_LEFT).append(BORDER_PADDING);
      for (int c = 0; c < columns; c++) {
        Object cellValue = getCell(r, c);
        String cellString = String.valueOf(renderCell(cellValue, r, c));
        rowValues.add(pad(cellString, getWidth(c), getAlignment(c)));
      }
      ps.append(String.join(BORDER_PADDING + COLUMN_SEPARATOR + BORDER_PADDING, rowValues));
      ps.append(BORDER_PADDING).println(BORDER_RIGHT);

      //separator
      if(r < rows-1) {
        separator(ps, BORDER_LINE, borderlessWidth);
      }
    }

    //bottom
    separator(ps, BORDER_TOP_BOTTOM, borderlessWidth);

    ps.flush();
  }

  protected String pad(String txt, int length, Align align) {
    char compChar = BORDER_PADDING;
    switch(align) {
      case RIGHT:
        return padLeft(txt, compChar, length);

      case CENTER:
        return padCenter(txt, compChar, length);

      case LEFT:
        return padRight(txt, compChar, length);

      default:
        throw new UnsupportedOperationException(align.name());
    }
  }

  @Override
  public String toString() {
    try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      stream(baos);
      return baos.toString();
    }
    catch(IOException e) {
      throw new RuntimeException(e); // Not likely to happen
    }
  }

  private void separator(PrintStream ps, char c, int borderlessWidth) {
    ps.append(BORDER_LEFT).append(c);
    for(int t = 0; t < borderlessWidth; t++) {
      ps.append(c);
    }
    ps.append(c).println(BORDER_RIGHT);
  }

  private String renderHeader(Object value, int column) {
    ConsoleCellRenderer ccr = getHeaderRenderer(column);
    if(ccr == null) {
      Class<?> headerClass = value != null ? value.getClass() : null;
      ccr = getDefaultCellRenderer(headerClass);
    }
    return ccr.render(value, -1, column);
  }

  private String renderCell(Object value, int row, int column) {
    ConsoleCellRenderer ccr = getCellRenderer(row, column);
    if(ccr == null) {
      Class<?> columnClass = getColumnClass(column);
      ccr = getDefaultCellRenderer(columnClass);
    }
    return ccr.render(value, row, column);
  }
}
