package org.fabmars.console;

/**
 * Symbolizes a value that doesn't exist (eg: out of bounds)
 * as opposed to a null value
 */
public class NonExistent {
  public final static NonExistent instance = new NonExistent();

  private NonExistent() {/*nothing*/}

  public static boolean isIt(Object value) {
    return instance.equals(value); // it is null-safe
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
