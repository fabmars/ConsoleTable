package org.fabmars.console;

import java.time.LocalDate;

/**
 * Created by mars on 27/09/2016.
 */
public class Gadjo {
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