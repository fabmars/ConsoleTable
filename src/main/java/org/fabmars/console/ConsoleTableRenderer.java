package org.fabmars.console;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.fabmars.console.Utils.padCenter;
import static org.fabmars.console.Utils.padLeft;
import static org.fabmars.console.Utils.padRight;


/**
 * Created by fabmars on 17/09/16.
 */
public abstract class ConsoleTableRenderer<T> {

  public static final String BORDER_LEFT = "| ";
  public static final String COLUMN_SEPARATOR = " | ";
  public static final String BORDER_RIGHT = " |\n";
  public static final char BORDER_TOP_BOTTOM = '=';
  public static final char BORDER_LINE = '-';

  public static final String DEFAULT_IF_NULL = "";
  protected final String ifNull;

  protected final List<T> list;
  private int[] widths;


  public ConsoleTableRenderer(Collection<T> col, String ifNull) {
    this.ifNull = ifNull != null ? ifNull : DEFAULT_IF_NULL;
    list = new ArrayList<>(col);
  }

  public abstract int getColumns();

  public final int getRows() {
    return list.size();
  }
  protected final T getRow(int row) {
    return list.get(row);
  }

  public abstract boolean isHeaders();
  public abstract String getHeader(int column);

  public final int getWidth(int column) {
    if(widths == null) {
      widths = calcWidths();
    }
    return widths[column];
  }

  public Align getAlignment(int column) {
    return Align.RIGHT;
  }

  protected int[] calcWidths() {
    int columns = getColumns();
    int[] widths = new int[columns];

    if(isHeaders()) {
      for (int c = 0; c < columns; c++) {
        widths[c] = toStringOrIfNull(getHeader(c)).length();
      }
    }

    int rows = getRows();
    for(int r = 0; r < rows; r++) {
      for(int c = 0; c < columns; c++) {
        Object cellValue = getCell(r, c);
        int len = toStringOrIfNull(cellValue).length();
        if(widths[c] < len) {
          widths[c] = len;
        }
      }
    }

    return widths;
  }

  public abstract Object getCell(int row, int column);


  @Override
  public String toString() {
    int rows = getRows();
    int columns = getColumns();

    int borderlessWidth = 3 * (columns-1);
    for(int c = 0; c < columns; c++) {
      borderlessWidth += getWidth(c);
    }

    List<String> rowValues;

    StringBuilder sb = new StringBuilder();
    //top
    separator(sb, BORDER_TOP_BOTTOM, borderlessWidth);


    //headers
    rowValues = new ArrayList<>(columns);

    if(isHeaders()) {
      sb.append(BORDER_LEFT);
      for (int c = 0; c < columns; c++) {
        String cellString = toStringOrIfNull(getHeader(c));
        rowValues.add(pad(cellString, getWidth(c), getAlignment(c)));
      }
      sb.append(String.join(COLUMN_SEPARATOR, rowValues));
      sb.append(BORDER_RIGHT);

      //separator
      separator(sb, BORDER_TOP_BOTTOM, borderlessWidth);
    }

    //table data
    for(int r = 0; r < rows; r++) {
      rowValues = new ArrayList<>(columns);
      sb.append(BORDER_LEFT);
      for (int c = 0; c < columns; c++) {
        Object cellValue = getCell(r, c);
        String cellString = toStringOrIfNull(cellValue);
        rowValues.add(pad(cellString, getWidth(c), getAlignment(c)));
      }
      sb.append(String.join(COLUMN_SEPARATOR, rowValues));
      sb.append(BORDER_RIGHT);

      //separator
      if(r < rows-1) {
        separator(sb, BORDER_LINE, borderlessWidth);
      }
    }

    //bottom
    separator(sb, BORDER_TOP_BOTTOM, borderlessWidth);

    return sb.toString();
  }

  private void separator(StringBuilder sb, char c, int borderlessWidth) {
    sb.append('|').append(c);
    for(int t = 0; t < borderlessWidth; t++) {
      sb.append(c);
    }
    sb.append(c).append("|\n");
  }

  protected String toStringOrIfNull(Object o) {
    return o != null ? o.toString() : ifNull;
  }

  protected String pad(String txt, int length, Align align) {
    char compChar = ' ';
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
}
