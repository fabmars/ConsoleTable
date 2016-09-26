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


## TODO
- Ability to stream the output (for big tables)
- renderers per column
- renderers per type
- toggle column header or not
- configurable table borders
