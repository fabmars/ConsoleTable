package org.fabmars.console;

import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class SimpleTest extends ConsoleTableTest implements GadjoData {

  /**
   * Object list rendering null lines
   */
  @Test
  public void testSimpleRenderNullLines() {
    ConsoleTable consoleTable = new SimpleConsoleTable<>(list);
    testOutput("test-simple-render-null-lines.txt", consoleTable);
  }


  /**
   * Object list NOT rendering null lines
   */
  @Test
  public void testSimpleNotRenderingNullLines() {
    ConsoleTable consoleTable = new SimpleConsoleTable(list) {
      @Override
      public boolean isRenderable(Object row, int r) {
        return row != null;
      }
    };
    testOutput("test-simple-not-rendering-null-lines.txt", consoleTable);
  }

  /**
   * Object list without headers and custom null value
   */
  @Test
  public void testSimpleNoHeadersCustomNull() {
    SimpleConsoleTable<Gadjo> consoleTable = new SimpleConsoleTable<>(list, false, "(null)");
    testOutput("test-simple-no-headers-custom-null.txt", consoleTable);
  }

  /**
   * Scalar list with Strings
   */
  @Test
  public void testSimpleScalarString() {
    SimpleConsoleTable<String> consoleTable = new SimpleConsoleTable<>("Hello", "how", "are", null, "you?");
    testOutput("test-simple-scalar-string.txt", consoleTable);
  }

  /**
   * Scalar list with ints
   */
  @Test
  public void testSimpleScalarInt() {
    SimpleConsoleTable<Integer> consoleTable = new SimpleConsoleTable<>(null, 123, 456, 789);
    testOutput("test-simple-scalar-int.txt", consoleTable);
  }

  /**
   * Map
   */
  @Test
  public void testSimpleMap() {
    Map<String, Integer> map = new LinkedHashMap<>();
    map.put("Lisa T.", Year.now().getValue() - lisa.getBirthDate().getYear());
    map.put("William Henry", Year.now().getValue() - bill.getBirthDate().getYear());
    map.put("Steven", null);
    SimpleConsoleTable<Object> consoleTable = new SimpleConsoleTable<>(map);

    testOutput("test-simple-map.txt", consoleTable);
  }

  /**
   * Array
   */
  @Test
  public void testSimpleArray() {
    Object[][] data = {{"Lisa T.", "Su", 1969, true}, {"William Henry", "Gates III"}, {"Steven", "Jobs", 1955, false}};
    SimpleConsoleTable<Object[]> consoleTable = new SimpleConsoleTable<>(data, "N/A");
    testOutput("test-simple-array.txt", consoleTable);
  }

  /**
   * Empty
   */
  @Test
  public void testSimpleEmpty() {
    SimpleConsoleTable<Object> consoleTable = new SimpleConsoleTable<>(Collections.emptyList());
    testOutput("test-simple-empty.txt", consoleTable);
  }
}
