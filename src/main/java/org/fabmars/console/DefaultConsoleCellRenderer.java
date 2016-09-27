package org.fabmars.console;

/**
 * Created by mars on 27/09/2016.
 */
public class DefaultConsoleCellRenderer implements ConsoleCellRenderer<Object> {

  public static final String EMPTY = "";
  public final static DefaultConsoleCellRenderer instance = new DefaultConsoleCellRenderer();


  private final String ifNull;

  public DefaultConsoleCellRenderer() {
    this(EMPTY);
  }

  public DefaultConsoleCellRenderer(String ifNull) {
    this.ifNull = ifNull;
  }

  @Override
  public String render(Object value, int row, int column) {
    return value != null ? value.toString() : ifNull;
  }
}
