# ConsoleTable

Sometimes one needs to display tabular data from a console java app.
The Clojure guys have print-table (https://clojuredocs.org/clojure.pprint/print-table). Now you have one in Java too.

As of today the class supports: lists or arrays of scalars (Strings, ints...), Maps, Lists or arrays of Objects
It was developed in a couple of hours for a q&d project, reusing old code of mine, take it as-is.

It requires Java 8 but could easily be retrofitted for Java 7 or 6.


## How it works
ConsoleTable is a simple class remotely inspired by Swing's Tables which enables you to display the contents of tabular data in a log or on the console.

SimpleConsoleTable is one implementation that uses introspection on the input data.
You may add more implementations.

The #toString() method triggers rendering, so you may directly use a ConsoleTable instance in a Logger or System.out/err

## Output

### List of Objects discovered via introspection.
Column headers are the objects actual property names.  
Default renderer is String.ValueOf()  
Default alignemnt is right and null values are printed as empty Strings.

    |=======================================================|
    |  BirthDate |     FirstName |  LastName | UsingWindows |
    |=======================================================|
    | 1969-11-15 |       Lisa T. |        Su |         true |
    |-------------------------------------------------------|
    |            |               |           |              |
    |-------------------------------------------------------|
    | 1955-10-28 | William Henry | Gates III |         true |
    |-------------------------------------------------------|
    | 1955-02-24 |        Steven |      Jobs |        false |
    |=======================================================|

### Custom table based on above objects
"First Name "and "Last Name" are right-aligned and bnull values should be printed as "-"  
"Birth Date" is left-aligned, format is dd MMM yyyy and null values should be printed as "N/A" 
"Born during Fall" is centered, format is "Yes"/"No" and null values become "?"

    |==============================================================|
    |    First Name | Last Name | Birth Date    | Born during Fall |
    |==============================================================|
    |       Lisa T. |        Su | 15 nov. 1969  |       Yes        |
    |--------------------------------------------------------------|
    |             - |         - | N/A           |        ?         |
    |--------------------------------------------------------------|
    | William Henry | Gates III | 28 oct. 1955  |       Yes        |
    |--------------------------------------------------------------|
    |        Steven |      Jobs | 24 févr. 1955 |        No        |
    |==============================================================|

### Array of Strings
{"Hello", "how", "are", null, "you"}  
Default column header is the scalar's simple class name

    |========|
    | String |
    |========|
    |  Hello |
    |--------|
    |    how |
    |--------|
    |    are |
    |--------|
    |        |
    |--------|
    |   you? |
    |========|

### Array of int/Integers
{null, 123, 456, 789}

    |=========|
    | Integer |
    |=========|
    |         |
    |---------|
    |     123 |
    |---------|
    |     456 |
    |---------|
    |     789 |
    |=========|

### Map<String, Integer>
Default column headers are found via introspection.

    |=======================|
    |           Key | Value |
    |=======================|
    |       Lisa T. |    47 |
    |-----------------------|
    | William Henry |    61 |
    |-----------------------|
    |        Steven |       |
    |=======================|

### Object[][]
No header to infer.  
"N/A" should be printed for null values.

    |==========================================|
    |       Lisa T. |        Su | 1969 |  true |
    |------------------------------------------|
    | William Henry | Gates III |  N/A |   N/A |
    |------------------------------------------|
    |        Steven |      Jobs | 1955 | false |
    |==========================================|

Note the table can manage with arrays of different sizes.

## TODO
- configurable table borders
