package org.fabmars.console;

public class ToStringConsoleRenderer implements ConsoleHeaderRenderer<Object>, ConsoleCellRenderer<Object> {
  public static ToStringConsoleRenderer instance = new ToStringConsoleRenderer();

  private ToStringConsoleRenderer() {
    // nothing
  }

  @Override
  public String render(Object value, int colNum) {
    return render(value, -1, colNum);
  }

  @Override
  public String render(Object value, int rowNum, int colNum) {
    return String.valueOf(value);
  }
}
