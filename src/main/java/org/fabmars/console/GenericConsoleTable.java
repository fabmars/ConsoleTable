package org.fabmars.console;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class GenericConsoleTable<R> extends ConsoleTable<R> {

  private final List<R> list;
  private final DefaultConsoleCellRenderer defaultCellRenderer;


  public GenericConsoleTable(Collection<R> col) {
    this(col, true);
  }

  public GenericConsoleTable(Collection<R> collection, boolean headers) {
    this(collection, headers, DefaultConsoleCellRenderer.EMPTY);
  }

  public GenericConsoleTable(Collection<R> collection, String ifNull) {
    this(collection, true, ifNull);
  }

  public GenericConsoleTable(Collection<R> collection, boolean headers, String ifNull) {
    super(headers);
    this.list = new ArrayList<>(collection);
    this.defaultCellRenderer = new DefaultConsoleCellRenderer(ifNull);
  }

  public GenericConsoleTable(R... array) {
    this(Arrays.asList(array));
  }

  public GenericConsoleTable(R[] array, boolean headers) {
    this(Arrays.asList(array), headers);
  }

  public GenericConsoleTable(R[] array, String ifNull) {
    this(Arrays.asList(array), ifNull);
  }

  public GenericConsoleTable(R[] array, boolean headers, String ifNull) {
    this(Arrays.asList(array), headers, ifNull);
  }

  public GenericConsoleTable(Map map) {
    this(map.entrySet());
  }

  public GenericConsoleTable(Map map, boolean headers) {
    this(map.entrySet(), headers);
  }
  public GenericConsoleTable(Map map, String ifNull) {
    this(map.entrySet(), ifNull);
  }

  public GenericConsoleTable(Map map, boolean headers, String ifNull) {
    this(map.entrySet(), headers, ifNull);
  }

  public GenericConsoleTable(Stream<R> stream) {
    this(stream.collect(Collectors.toSet()));
  }

  public GenericConsoleTable(Stream<R> stream, boolean headers) {
    this(stream.collect(Collectors.toSet()), headers);
  }
  public GenericConsoleTable(Stream<R> stream, String ifNull) {
    this(stream.collect(Collectors.toSet()), ifNull);
  }

  public GenericConsoleTable(Stream<R> stream, boolean headers, String ifNull) {
    this(stream.collect(Collectors.toSet()), headers, ifNull);
  }


  @Override
  public final int getRowCount() {
    return list.size();
  }
  @Override
  protected final R getRow(int row) {
    return list.get(row);
  }

  @Override
  public ConsoleCellRenderer getDefaultCellRenderer(Class<?> clazz) {
    return defaultCellRenderer;
  }
}
