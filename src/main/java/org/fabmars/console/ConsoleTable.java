package org.fabmars.console;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
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

  Boolean preProcessed = false;
  private int[] widthsCache;
  private String[] headerRenderCache;
  private List<String[]> cellRenderCache;


  public ConsoleTable(boolean headers) {
    this.headers = headers;
  }

  public abstract int getColumnCount();
  public abstract int getRowCount();

  public boolean isHeaders() {
    return headers;
  }
  public abstract Object getHeader(int column);

  public abstract R getRow(int rowNum);

  /**
   * Tells whether a given row should be rendered.
   * For instance you might not want to render null rows
   * @param rowObject
   * @param rowNum
   * @return
   */
  public boolean isRenderable(R rowObject, int rowNum) {
    return true;
  }

  /**
   * gets a property of an object aimed at one column of the table
   * @param row a non-null row object
   * @param colNum the target table column
   * @return the cell value
   */
  public abstract Object getCell(R row, int colNum);


  public final int getRenderedWidth(int colNum) {
    ensurePreProcessed(false); // Not necessary per se, just in case someone calls this method independently of stream()
    return widthsCache[colNum];
  }

  public final String getRenderedHeader(int colNum) {
    ensurePreProcessed(false); // Not necessary per se, just in case someone calls this method independently of stream()
    String headerString = headerRenderCache[colNum];
    return pad(headerString, getRenderedWidth(colNum), getAlignment(colNum));
  }

  private String getRenderedCell(int rowNum, int colNum) {
    ensurePreProcessed(false); // Not necessary per se, just in case someone calls this method independently of stream()
    String cellString = cellRenderCache.get(rowNum)[colNum];
    return pad(cellString, getRenderedWidth(colNum), getAlignment(colNum));
  }

  protected synchronized final void ensurePreProcessed(boolean force) {
    if(!preProcessed || force) {
      int columnCount = getColumnCount();
      widthsCache = new int[columnCount];
      headerRenderCache = new String[columnCount];

      if (isHeaders()) {
        for (int colNum = 0; colNum < columnCount; colNum++) {
          String renderedHeader = safeRenderHeader(getHeader(colNum), colNum);
          headerRenderCache[colNum] = renderedHeader;
          widthsCache[colNum] = renderedHeader.length();
        }
      }

      int rowCount = getRowCount();
      cellRenderCache = new LinkedList<>();
      for (int rowNum = 0; rowNum < rowCount; rowNum++) {
        R row = getRow(rowNum);
        if (isRenderable(row, rowNum)) {
          String[] rowCache = new String[columnCount];
          for (int colNum = 0; colNum < columnCount; colNum++) {
            Object cellValue = row != null ? getCell(row, colNum) : NonExistent.instance;
            String renderedCell = safeRenderCell(cellValue, rowNum, colNum);
            rowCache[colNum] = renderedCell;

            int len = renderedCell.length();
            if (widthsCache[colNum] < len) {
              widthsCache[colNum] = len;
            }
          }

          cellRenderCache.add(rowCache);
        }
      }
      preProcessed = true;
    }
  }


  public Align getAlignment(int colNum) {
    return Align.RIGHT;
  }

  public ConsoleHeaderRenderer getHeaderRenderer(int colNum) {
    return null;
  }

  public ConsoleCellRenderer getCellRenderer(int rowNum, int colNum) {
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

  public String toString(String charsetName) {
    try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      stream(baos);
      return baos.toString(charsetName);
    }
    catch(IOException e) {
      throw new RuntimeException(e); // Not likely to happen
    }
  }

  public void stream(OutputStream os) {
    ensurePreProcessed(true); //forcing at each rendering in case the underlying datas have changed

    PrintStream ps = new PrintStream(os);
    int columnCount = getColumnCount();

    int borderlessWidth = 3 * (columnCount-1);
    for(int colNum = 0; colNum < columnCount; colNum++) {
      borderlessWidth += getRenderedWidth(colNum); // calls preProcess
    }

    // Top line
    separator(ps, BORDER_TOP_BOTTOM, borderlessWidth);

    // Headers
    if(isHeaders()) {
      List<String> paddedHeaderValues = new ArrayList<>(columnCount);

      ps.append(BORDER_LEFT).append(BORDER_PADDING);
      for (int colNum = 0; colNum < columnCount; colNum++) {
        String renderedHeader = getRenderedHeader(colNum);
        paddedHeaderValues.add(renderedHeader);
      }
      ps.append(String.join(BORDER_PADDING + COLUMN_SEPARATOR + BORDER_PADDING, paddedHeaderValues));
      ps.append(BORDER_PADDING).println(BORDER_RIGHT);

      // Separator
      separator(ps, BORDER_TOP_BOTTOM, borderlessWidth);
    }

    boolean canDrawLine = false;

    // Table data
    int rowCount = cellRenderCache.size();
    for(int rowNum = 0; rowNum < rowCount; rowNum++) {
      // Separator. It's easier to test like this than after an actual line.
      if(canDrawLine) {
        separator(ps, BORDER_LINE, borderlessWidth);
      }
      canDrawLine = true;

      List<String> paddedRowValues = new ArrayList<>(columnCount);
      ps.append(BORDER_LEFT).append(BORDER_PADDING);
      for (int colNum = 0; colNum < columnCount; colNum++) {
        String renderedCell = getRenderedCell(rowNum, colNum);
        paddedRowValues.add(renderedCell);
      }
      ps.append(String.join(BORDER_PADDING + COLUMN_SEPARATOR + BORDER_PADDING, paddedRowValues));
      ps.append(BORDER_PADDING).println(BORDER_RIGHT);
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
   * @param colNum
   * @return textual representation of the header cell. MUST NOT return null
   */
  private final String safeRenderHeader(Object value, int colNum) {
    String result = renderHeader(value, colNum);
    if(result == null) {
      result = ToStringConsoleRenderer.instance.render(value, colNum);
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

  private final String safeRenderCell(Object value, int rowNum, int colNum) {
    String result = renderCell(value, rowNum, colNum);
    if(result == null) {
      result = ToStringConsoleRenderer.instance.render(value, rowNum, colNum);
    }
    return result;
  }

  protected String renderCell(Object value, int rowNum, int colNum) {
    ConsoleCellRenderer cellRenderer;
    if(NonExistent.isIt(value)) {
      cellRenderer = getDefaultCellRenderer(NonExistent.class);
    }
    else {
      cellRenderer = getCellRenderer(rowNum, colNum);
      if(cellRenderer == null) {
        Class<?> columnClass = getColumnClass(colNum);
        cellRenderer = getDefaultCellRenderer(columnClass);
      }
    }

    String result = null;
    if(cellRenderer != null) {
      result = cellRenderer.render(value, rowNum, colNum);
    }
    return result;
  }
}
