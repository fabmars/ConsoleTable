package org.fabmars.console;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.lang.Boolean.TRUE;

/**
 * Created by mars on 27/09/2016.
 */
public class GadjoConsoleTable extends GenericConsoleTable<Gadjo> {

  public GadjoConsoleTable(List<Gadjo> gadjos) {
    super(gadjos);
  }

  @Override
  public int getColumnCount() {
    return 4;
  }

  @Override
  public Object getHeader(int column) {
    switch (column) {
      case 0:
        return "First Name";
      case 1:
        return "Last Name";
      case 2:
        return "Birth Date";
      case 3:
        return "Born during Fall";
      default:
        return null;
    }
  }

  @Override
  public Object getCell(int row, int column) {
    Gadjo gadjo = getRow(row);
    if(gadjo != null) {
      switch (column) {
        case 0:
          return gadjo.getFirstName();
        case 1:
          return gadjo.getLastName();
        case 2:
          return gadjo.getBirthDate();
        case 3:
          LocalDate birthDate = gadjo.getBirthDate();
          if(birthDate != null) {
            LocalDate start = birthDate.withMonth(9).withDayOfMonth(23), end = birthDate.withMonth(12).withDayOfMonth(20); //inclusive
            return birthDate.compareTo(start) >= 0 && birthDate.compareTo(end) <= 0;
          } else {
            return null;
          }
        default:
          return null;
      }
    }
    else {
      return null;
    }
  }


  @Override
  public Align getAlignment(int column) {
    switch (column) {
      case 0:
      case 1:
        return Align.RIGHT;
      case 2:
        return Align.LEFT;
      default:
        return Align.CENTER;
    }
  }

  @Override
  public Class<?> getColumnClass(int column) {
    switch (column) {
      case 0:
      case 1:
        return String.class;
      case 2:
        return LocalDate.class;
      case 3:
        return Boolean.class;
      default:
        return null;
    }
  }

  @Override
  public ConsoleCellRenderer getDefaultCellRenderer(Class<?> clazz) {
    if(LocalDate.class.equals(clazz)) {
      return localDateCellRenderer;
    }
    else if(Boolean.class.equals(clazz)) {
      return booleanCellRenderer;
    }
    else {
      return new DefaultConsoleCellRenderer("-");
    }
  }

  private final static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
  private final static ConsoleCellRenderer localDateCellRenderer = new ConsoleCellRenderer<LocalDate>() {
    @Override
    public String render(LocalDate value, int row, int column) {
      if (value != null) {
        return dateTimeFormatter.format(value);
      } else {
        return "N/A";
      }
    }
  };

  private final static ConsoleCellRenderer booleanCellRenderer = new ConsoleCellRenderer<Boolean>() {
    @Override
    public String render(Boolean value, int row, int column) {
      if (value != null) {
        return TRUE.equals(value) ? "Yes" : "No";
      } else {
        return "?";
      }
    }
  };
}
