package org.fabmars.console;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;

/**
 * Created by fabmars on 19/09/16.
 */
public class ConsoleTableTester {

  public static void main(String... args) throws IOException {
    Gadjo lisa = new Gadjo("Lisa T.", "Su", LocalDate.of(1969, 11, 15), true);
    Gadjo bill = new Gadjo("William Henry", "Gates III", LocalDate.of(1955, 10, 28), true);
    Gadjo steve = new Gadjo("Steven", "Jobs", LocalDate.of(1955, 2, 24), false);

    List<Gadjo> list = Arrays.asList(lisa, null, bill, steve);


    // Object list
    System.out.println( new SimpleConsoleTable<>(list));

    new SimpleConsoleTable<>(list, false, "(null)").stream(System.out);
    System.out.println();

    System.out.println(new GadjoConsoleTable(list));


    // Scalar list
    System.out.println( new SimpleConsoleTable<>("Hello", "how", "are", null, "you?"));
    System.out.println( new SimpleConsoleTable<>(null, 123, 456, 789));


    // Map
    Map<String, Integer> map = new LinkedHashMap<>();
    map.put("Lisa T.", Year.now().getValue() - lisa.getBirthDate().getYear());
    map.put("William Henry", Year.now().getValue() - bill.getBirthDate().getYear());
    map.put("Steven", null);
    System.out.println( new SimpleConsoleTable<>(map));


    // Array
    Object[][] data = {{"Lisa T.", "Su", 1969, true}, {"William Henry", "Gates III"}, {"Steven", "Jobs", 1955, false}};
    System.out.println( new SimpleConsoleTable<>(data, "N/A"));


    // Nothing
    System.out.println( new SimpleConsoleTable<>(Collections.emptyList()));
  }
}
