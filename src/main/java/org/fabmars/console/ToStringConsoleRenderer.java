package org.fabmars.console;

public class ToStringConsoleRenderer implements ConsoleHeaderRenderer<Object>, ConsoleCellRenderer<Object> {
  public static ToStringConsoleRenderer instance = new ToStringConsoleRenderer();

  private ToStringConsoleRenderer() {
    // nothing
  }

  @Override
  public String render(Object value, int column) {
    return render(value, -1, column);
  }

  @Override
  public String render(Object value, int row, int column) {
    return String.valueOf(value);
  }
}
