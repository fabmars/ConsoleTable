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

  private int columns;
  private List<String> headers;
  private List<Method> getters;


  public SimpleConsoleTable(Collection<R> col) {
    this(col, true);
  }

  public SimpleConsoleTable(Collection<R> collection, boolean headers) {
    this(collection, headers, DefaultConsoleCellRenderer.EMPTY);
  }

  public SimpleConsoleTable(Collection<R> collection, String ifNull) {
    this(collection, true, ifNull);
  }

  public SimpleConsoleTable(Collection<R> collection, boolean headers, String ifNull) {
    super(collection, headers, ifNull);

    boolean isArrayElements = collection.isEmpty() || collection.stream().filter(Objects::nonNull).anyMatch(o -> o.getClass().isArray());
    if (isArrayElements) {
      this.getters = Collections.emptyList();
      this.headers = Collections.emptyList();
      this.columns = collection.stream().filter(Objects::nonNull).mapToInt(o -> ((Object[]) o).length).max().orElse(0);
    } else try {
      for (R object : collection) {
        if (object != null) {
          Map<String, Method> propertyMap = inspect(object.getClass());
          this.getters = new ArrayList<>(propertyMap.values());
          this.headers = propertyMap.keySet().stream().map(h -> Utils.toUpperCaseFirstChar(h)).collect(Collectors.toList());
          this.columns = this.headers.size();
          break;
        }
      }
    } catch (IntrospectionException | NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  public SimpleConsoleTable(R... array) {
    this(Arrays.asList(array));
  }

  public SimpleConsoleTable(R[] array, boolean headers) {
    this(Arrays.asList(array), headers);
  }

  public SimpleConsoleTable(R[] array, String ifNull) {
    this(Arrays.asList(array), ifNull);
  }

  public SimpleConsoleTable(R[] array, boolean headers, String ifNull) {
    this(Arrays.asList(array), headers, ifNull);
  }

  public SimpleConsoleTable(Map map) {
    this(map.entrySet());
  }

  public SimpleConsoleTable(Map map, boolean headers) {
    this(map.entrySet(), headers);
  }
  public SimpleConsoleTable(Map map, String ifNull) {
    this(map.entrySet(), ifNull);
  }

  public SimpleConsoleTable(Map map, boolean headers, String ifNull) {
    this(map.entrySet(), headers, ifNull);
  }

  public SimpleConsoleTable(Stream<R> stream) {
    this(stream.collect(Collectors.toSet()));
  }

  public SimpleConsoleTable(Stream<R> stream, boolean headers) {
    this(stream.collect(Collectors.toSet()), headers);
  }
  public SimpleConsoleTable(Stream<R> stream, String ifNull) {
    this(stream.collect(Collectors.toSet()), ifNull);
  }

  public SimpleConsoleTable(Stream<R> stream, boolean headers, String ifNull) {
    this(stream.collect(Collectors.toSet()), headers, ifNull);
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
  public String getHeader(int column) {
    return headers.get(column);
  }


  @Override
  public Object getCell(int row, int column) {
    R object = getRow(row);

    Object cellValue;
    if(object != null) {
      if(object.getClass().isArray()) {
        Object[] array = (Object[])object;
        cellValue = column < array.length ? array[column] : null;
      }
      else try {
        cellValue = getters.get(column).invoke(object);
      }
      catch (IllegalAccessException | InvocationTargetException e) {
        throw new RuntimeException(e);
      }
    }
    else {
      cellValue = null;
    }
    return cellValue;
  }
}