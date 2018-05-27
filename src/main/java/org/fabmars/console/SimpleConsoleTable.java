package org.fabmars.console;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by fabmars on 17/09/16.
 *
 * Introspected ConsoleTable
 * @param <R>
 */
public class SimpleConsoleTable<R> extends GenericConsoleTable<R> {

  private final int columns;
  private final List<String> headers;
  private final List<Method> getters;
  private final DefaultConsoleRenderer cellRenderer;

  public SimpleConsoleTable(Collection<R> col) {
    this(col, true);
  }

  public SimpleConsoleTable(Collection<R> collection, boolean headers) {
    this(collection, headers, DefaultConsoleRenderer.EMPTY);
  }

  public SimpleConsoleTable(Collection<R> collection, String ifNullOrInexistent) {
    this(collection, true, ifNullOrInexistent);
  }

  public SimpleConsoleTable(Collection<R> collection, boolean headers, String ifNullOrInexistent) {
    super(collection, headers);
    this.cellRenderer = new DefaultConsoleRenderer(ifNullOrInexistent);

    boolean isArrayElements = collection.isEmpty() || collection.stream().filter(Objects::nonNull).anyMatch(o -> o.getClass().isArray());
    if (isArrayElements) {
      this.getters = Collections.emptyList();
      this.headers = Collections.emptyList();
      this.columns = collection.stream().filter(Objects::nonNull).mapToInt(o -> ((Object[]) o).length).max().orElse(0);
    }
    else {
      Optional<R> opt = collection.stream().filter(Objects::nonNull).findAny();
      if (opt.isPresent()) {
        try {
          Map<String, Method> propertyMap = inspect(opt.get().getClass());
          this.getters = new ArrayList<>(propertyMap.values());
          this.headers = propertyMap.keySet().stream().map(h -> Utils.toUpperCaseFirstChar(h)).collect(Collectors.toList());
          this.columns = this.headers.size();
        }
        catch (IntrospectionException | NoSuchMethodException e) {
          throw new RuntimeException(e);
        }
      } else { // no non-null object in the collection
        this.getters = Collections.emptyList();
        this.headers = Collections.emptyList();
        this.columns = 0;
      }
    }
  }

  public SimpleConsoleTable(R... array) {
    this(Arrays.asList(array));
  }

  public SimpleConsoleTable(R[] array, boolean headers) {
    this(Arrays.asList(array), headers);
  }

  public SimpleConsoleTable(R[] array, String ifNullOrInexistent) {
    this(Arrays.asList(array), ifNullOrInexistent);
  }

  public SimpleConsoleTable(R[] array, boolean headers, String ifNullOrInexistent) {
    this(Arrays.asList(array), headers, ifNullOrInexistent);
  }

  public SimpleConsoleTable(Map map) {
    this(map.entrySet());
  }

  public SimpleConsoleTable(Map map, boolean headers) {
    this(map.entrySet(), headers);
  }

  public SimpleConsoleTable(Map map, String ifNullOrInexistent) {
    this(map.entrySet(), ifNullOrInexistent);
  }

  public SimpleConsoleTable(Map map, boolean headers, String ifNullOrInexistent) {
    this(map.entrySet(), headers, ifNullOrInexistent);
  }

  public SimpleConsoleTable(Stream<R> stream) {
    this(stream.collect(Collectors.toSet()));
  }

  public SimpleConsoleTable(Stream<R> stream, boolean headers) {
    this(stream.collect(Collectors.toSet()), headers);
  }

  public SimpleConsoleTable(Stream<R> stream, String ifNullOrInexistent) {
    this(stream.collect(Collectors.toSet()), ifNullOrInexistent);
  }

  public SimpleConsoleTable(Stream<R> stream, boolean headers, String ifNullOrInexistent) {
    this(stream.collect(Collectors.toSet()), headers, ifNullOrInexistent);
  }


  protected static Map<String, Method> inspect(Class<?> clazz) throws IntrospectionException, NoSuchMethodException {
    Map<String, Method> map = new LinkedHashMap<>(); //Linked to keep the order

    if(clazz.isArray()) {
      // Can't call any method on that; useless test in this class' case (see ctor)
    }
    else if(String.class.getPackage().equals(clazz.getPackage())) {
      map.put(clazz.getSimpleName(), clazz.getMethod("toString")); // so only #toString is called on java.lang classes (like String, Integer...)
    }
    else {
      BeanInfo beanInfo = Introspector.getBeanInfo(clazz, Object.class);
      PropertyDescriptor pds[] = beanInfo.getPropertyDescriptors();
      for (PropertyDescriptor pd : pds) {
        Method meth = pd.getReadMethod();
        if (meth != null) { //there is a getter
          String name = pd.getName();
          map.put(name, meth);
        }
      }
    }
    return map;
  }

  @Override
  public int getColumnCount() {
    return columns;
  }

  @Override
  public boolean isHeaders() {
    return super.isHeaders() && !headers.isEmpty();
  }

  @Override
  public String getHeader(int c) {
    return headers.get(c);
  }

  @Override
  public Object getCell(R row, int column) {
    if(row.getClass().isArray()) {
      Object[] array = (Object[])row;
      // Testing length because different rows (which are arrays) may have different widths
      return column < array.length ? array[column] : NonExistent.instance;
    }
    else try {
      return getters.get(column).invoke(row);
    }
    catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public ConsoleHeaderRenderer getDefaultHeaderRenderer(Class<?> clazz) {
    return cellRenderer;
  }

  @Override
  public ConsoleCellRenderer getDefaultCellRenderer(Class<?> clazz) {
    return cellRenderer;
  }
}