package org.fabmars.console;

/**
 * Created by mars on 27/09/2016.
 */
public interface ConsoleCellRenderer<T> {

  /**
   * @param value
   * @param rowNum
   * @param colNum
   * @return textual representation of the value
   */
  String render(T value, int rowNum, int colNum);
}
