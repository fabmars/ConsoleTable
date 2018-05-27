package org.fabmars.console;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class GenericConsoleTable<R> extends ConsoleTable<R> {

  private final List<R> list;


  public GenericConsoleTable(Collection<R> collection, boolean headers) {
    super(headers);
    this.list = new ArrayList<>(collection);
  }

  @Override
  public final int getRowCount() {
    return list.size();
  }
  @Override
  protected final R getRow(int r) {
    return list.get(r);
  }


  @Override
  public ConsoleHeaderRenderer getDefaultHeaderRenderer(Class<?> clazz) {
    return DefaultConsoleRenderer.instance;
  }

  @Override
  public ConsoleCellRenderer getDefaultCellRenderer(Class<?> clazz) {
    return DefaultConsoleRenderer.instance;
  }
}
