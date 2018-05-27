package org.fabmars.console;

/**
 * Created by mars on 27/09/2016.
 */
public class DefaultConsoleRenderer implements ConsoleHeaderRenderer<Object>, ConsoleCellRenderer<Object> {

  public static final String EMPTY = "";
  public final static DefaultConsoleRenderer instance = new DefaultConsoleRenderer();


  private final String ifNull;        // string to apply in the cell if the value is null
  private final String ifInexistent;  // string to apply in the cell if the value doesn't exist (eg: out of bounds)

  public DefaultConsoleRenderer() {
    this(EMPTY, EMPTY);
  }

  public DefaultConsoleRenderer(String ifNullOrInexistent) {
    this(ifNullOrInexistent, ifNullOrInexistent);
  }

  public DefaultConsoleRenderer(String ifNull, String ifInexistent) {
    this.ifNull = ifNull;
    this.ifInexistent = ifInexistent;
  }

  public String getIfNull() {
    return ifNull;
  }

  public String getIfInexistent() {
    return ifInexistent;
  }

  @Override
  public String render(Object value, int column) {
    return render(value, -1, column);
  }

  @Override
  public String render(Object value, int row, int column) {
    if(NonExistent.isIt(value)) {
      return ifInexistent;
    }
    else {
      return value != null ? value.toString() : ifNull;
    }
  }
}
