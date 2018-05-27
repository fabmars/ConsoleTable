package org.fabmars.console;

/**
 * Created by mars on 27/09/2016.
 */
public interface ConsoleCellRenderer<T> {

  /**
   * @param value
   * @param row
   * @param column
   * @return textual representation of the value
   */
  String render(T value, int row, int column);
}
