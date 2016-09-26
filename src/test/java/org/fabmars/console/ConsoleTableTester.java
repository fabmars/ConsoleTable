package org.fabmars.console;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fabmars on 19/09/16.
 */
public class ConsoleTableTester {

  public static void main(String... args) {
    Gadjo lisa = new Gadjo("Lisa T.", "Su", LocalDate.of(1969, 11, 15), true);
    Gadjo bill = new Gadjo("William Henry", "Gates III", LocalDate.of(1955, 10, 28), true);
    Gadjo steve = new Gadjo("Steve", "Jobs", LocalDate.of(1955, 2, 24), false);

    List<Gadjo> list = new ArrayList<>();
    list.add(lisa);
    list.add(null);
    list.add(bill);
    list.add(steve);

    // Object list
    System.out.println( new SimpleConsoleTable<>(list));
    System.out.println( new SimpleConsoleTable<>(list, "(null)"));

    // Scalar list
    System.out.println( new SimpleConsoleTable<>("Hello", "how", "are", null, "you?"));
    System.out.println( new SimpleConsoleTable<>(null, 123, 456, 789));

    // Map
    Map<String, Integer> map = new LinkedHashMap<>();
    map.put("Lisa T.", Year.now().getValue() - lisa.getBirthDate().getYear());
    map.put("William Henry", Year.now().getValue() - bill.getBirthDate().getYear());
    map.put("Steve", null);
    System.out.println( new SimpleConsoleTable<>(map));

    // Array
    Object[][] data = {{"Lisa T.", "Su", 1969, true}, {"William Henry", "Gates III"}, {"Steve", "Jobs", 1955, false}};
    System.out.println( new SimpleConsoleTable<>(data, "N/A"));
  }



  private final static class Gadjo {
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private boolean usingWindows;

    public Gadjo(String firstName, String lastName, LocalDate birthDate, boolean usingWindows) {
      this.firstName = firstName;
      this.lastName = lastName;
      this.birthDate = birthDate;
      this.usingWindows = usingWindows;
    }

    public String getFirstName() {
      return firstName;
    }

    public String getLastName() {
      return lastName;
    }

    public LocalDate getBirthDate() {
      return birthDate;
    }

    public boolean isUsingWindows() {
      return usingWindows;
    }
  }
}
