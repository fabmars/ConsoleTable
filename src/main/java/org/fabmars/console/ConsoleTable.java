package org.fabmars.console;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.fabmars.console.Utils.*;


/**
 * Created by fabmars on 17/09/16.
 */
public abstract class ConsoleTable<R> {

  public static final String BORDER_LEFT = "|";
  public static final String COLUMN_SEPARATOR = BORDER_LEFT;
  public static final String BORDER_RIGHT = BORDER_LEFT;
  public static final char BORDER_PADDING = ' ';
  public static final char BORDER_TOP_BOTTOM = '=';
  public static final char BORDER_LINE = '-';

  private boolean headers;
  private int[] widths;


  public ConsoleTable(boolean headers) {
    this.headers = headers;
  }

  public abstract int getColumnCount();

  public abstract int getRowCount();
  protected abstract R getRow(int r);

  /**
   * Tells whether a given row should be rendered.
   * For instance you might not want to render null rows
   * @param row
   * @return
   */
  public boolean isRenderable(R row) {
    return true;
  }

  public boolean isHeaders() {
    return headers;
  }

  public abstract Object getHeader(int c);

  /**
   * gets a property of an object aimed at one column of the table
   * @param row a non-null row object
   * @param column the target table column
   * @return the cell value
   */
  public abstract Object getCell(R row, int column);


  public final int getWidth(int column) {
    if(widths == null) {
      widths = calcWidths();
    }
    return widths[column];
  }

  protected final int[] calcWidths() {
    int columnCount = getColumnCount();
    int[] widths = new int[columnCount];

    if(isHeaders()) {
      for (int c = 0; c < columnCount; c++) {
        widths[c] = safeRenderHeader(getHeader(c), c).length();
      }
    }

    int rowCount = getRowCount();
    for(int r = 0; r < rowCount; r++) {
      R row = getRow(r);
      if(isRenderable(row)) {
        for(int c = 0; c < columnCount; c++) {
          Object cellValue = row != null ? getCell(row, c) : NonExistent.instance;
          int len = safeRenderCell(cellValue, r, c).length();
          if (widths[c] < len) {
            widths[c] = len;
          }
        }
      }
    }

    return widths;
  }


  public Align getAlignment(int column) {
    return Align.RIGHT;
  }

  public ConsoleHeaderRenderer getHeaderRenderer(int column) {
    return null;
  }

  public ConsoleCellRenderer getCellRenderer(int row, int column) {
    return null;
  }

  public Class<?> getColumnClass(int column) {
    return Object.class;
  }

  public ConsoleHeaderRenderer getDefaultHeaderRenderer(Class<?> clazz) {
    return null;
  }

  public ConsoleCellRenderer getDefaultCellRenderer(Class<?> clazz) {
    return null;
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

  public void stream(OutputStream os) {
    PrintStream ps = new PrintStream(os);
    int rowCount = getRowCount();
    int columnCount = getColumnCount();

    int borderlessWidth = 3 * (columnCount-1);
    for(int c = 0; c < columnCount; c++) {
      borderlessWidth += getWidth(c);
    }

    List<String> rowValues;

    // Top line
    separator(ps, BORDER_TOP_BOTTOM, borderlessWidth);

    // Headers
    rowValues = new ArrayList<>(columnCount);

    if(isHeaders()) {
      ps.append(BORDER_LEFT).append(BORDER_PADDING);
      for (int c = 0; c < columnCount; c++) {
        Object headerValue = getHeader(c);
        String headerString = safeRenderHeader(headerValue, c);
        rowValues.add(pad(headerString, getWidth(c), getAlignment(c)));
      }
      ps.append(String.join(BORDER_PADDING + COLUMN_SEPARATOR + BORDER_PADDING, rowValues));
      ps.append(BORDER_PADDING).println(BORDER_RIGHT);

      // Separator
      separator(ps, BORDER_TOP_BOTTOM, borderlessWidth);
    }

    boolean canDrawLine = false;

    // Table data
    for(int r = 0; r < rowCount; r++) {
      R row = getRow(r);
      if(isRenderable(row)) {

        // Separator. It's more efficient to do it now than after an actual line.
        if(canDrawLine) {
          separator(ps, BORDER_LINE, borderlessWidth);
        }
        canDrawLine = true;

        rowValues = new ArrayList<>(columnCount);
        ps.append(BORDER_LEFT).append(BORDER_PADDING);
        for (int c = 0; c < columnCount; c++) {
          Object cellValue = row != null ? getCell(row, c) : NonExistent.instance;
          String cellString = safeRenderCell(cellValue, r, c);
          rowValues.add(pad(cellString, getWidth(c), getAlignment(c)));
        }
        ps.append(String.join(BORDER_PADDING + COLUMN_SEPARATOR + BORDER_PADDING, rowValues));
        ps.append(BORDER_PADDING).println(BORDER_RIGHT);
      }
    }

    // Bottom
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


  private void separator(PrintStream ps, char c, int borderlessWidth) {
    ps.append(BORDER_LEFT).append(c);
    for(int t = 0; t < borderlessWidth; t++) {
      ps.append(c);
    }
    ps.append(c).println(BORDER_RIGHT);
  }


  /**
   * Renderes a textual representation of the header cell
   * @param value
   * @param column
   * @return textual representation of the header cell. MUST NOT return null
   */
  private final String safeRenderHeader(Object value, int column) {
    String result = renderHeader(value, column);
    if(result == null) {
      result = ToStringConsoleRenderer.instance.render(value, column);
    }
    return result;
  }

  protected String renderHeader(Object value, int column) {
    ConsoleHeaderRenderer headerRenderer = getHeaderRenderer(column);
    if(headerRenderer == null) {
      Class<?> headerClass = value != null ? value.getClass() : null;
      headerRenderer = getDefaultHeaderRenderer(headerClass);
    }

    String result = null;
    if(headerRenderer != null) {
      result = headerRenderer.render(value, column);
    }
    return result;
  }

  private final String safeRenderCell(Object value, int row, int column) {
    String result = renderCell(value, row, column);
    if(result == null) {
      result = ToStringConsoleRenderer.instance.render(value, row, column);
    }
    return result;
  }

  protected String renderCell(Object value, int row, int column) {
    ConsoleCellRenderer cellRenderer;
    if(NonExistent.isIt(value)) {
      cellRenderer = getDefaultCellRenderer(NonExistent.class);
    }
    else {
      cellRenderer = getCellRenderer(row, column);
      if(cellRenderer == null) {
        Class<?> columnClass = getColumnClass(column);
        cellRenderer = getDefaultCellRenderer(columnClass);
      }
    }

    String result = null;
    if(cellRenderer != null) {
      result = cellRenderer.render(value, row, column);
    }
    return result;
  }
}
