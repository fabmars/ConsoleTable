package org.fabmars.console;

public interface ConsoleHeaderRenderer<T> {

  /**
   * @param value
   * @param column
   * @return textual representation of the value
   */
  String render(T value, int column);
}
