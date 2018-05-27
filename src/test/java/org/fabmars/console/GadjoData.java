package org.fabmars.console;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public interface GadjoData {
  Gadjo lisa = new Gadjo("Lisa T.", "Su", LocalDate.of(1969, 11, 15), true);
  Gadjo bill = new Gadjo("William Henry", "Gates III", LocalDate.of(1955, 10, 28), true);
  Gadjo steve = new Gadjo("Steven", "Jobs", LocalDate.of(1955, 2, 24), false);
  List<Gadjo> list = Arrays.asList(lisa, null, bill, steve);
}
