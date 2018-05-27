package org.fabmars.console;

import org.junit.jupiter.api.Test;

public class CustomTest extends ConsoleTableTest implements GadjoData {

  /**
   * Object list with custom ConsoleTable
   */
  @Test
  public void testGadjoCustom() {
    GadjoConsoleTable consoleTable = new GadjoConsoleTable(list);
    testOutput("test-gadjo-custom.txt", consoleTable);
  }
}
