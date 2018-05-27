package org.fabmars.console;

public interface ConsoleHeaderRenderer<T> {

  /**
   * @param value
   * @param colNum
   * @return textual representation of the value
   */
  String render(T value, int colNum);
}
