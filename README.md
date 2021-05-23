# log-parser
[![unit-tests](https://github.com/adobe/log-parser/actions/workflows/onPushSimpleTest.yml/badge.svg)](https://github.com/adobe/log-parser/actions/workflows/onPushSimpleTest.yml) 
[![codecov](https://codecov.io/gh/adobe/log-parser/branch/master/graph/badge.svg?token=T94S3VFEUU)](https://codecov.io/gh/adobe/log-parser)

This project was created to allow us to parse and analyze log files in order to gather relevant data. It can be used as is or as an SDK. Where you can define your own parsing.

The basic method for using this library is, that you create a definition for your parsing. This definition allows you to parse a set of log files and extract all entries that match this pattern.

![The Processes](diagrams/Log_Parser-Processes.png)


## Parse Definitions
In order to parse logs you need to define a ParseDefinition. A ParseDefinition contains a set of ordered ParseDefinition Entries. While parsing a line of logs, the LogParser will see if all entries can be found in the line of logs. If that is the case, the line is stored according to the definitions.

### Defining a Parsing
Each Parse Definition consists of :
- Title
- A set of entries
- A Padding allowing us to create a legible key
- A key Order which is used for defining the Key

### Defining an entry
Each entry for a Parse Definition allows us to define:
- A title for the value which will be found.
- The start pattern of the string that will contain the value (null if in the start of a line)
- The end pattern of the string that will contain the value (null if in the end of a line)
- Case Sensitive search
- Is to be kept. In some cases we just need to find a line with certain particularities, but we don't actually want to store the value.

### How parsing works
When you have defined your parsing you use the LogDataFactory by passing it:
1. The log files it should parse
2. The ParseDefinition

By using the StringParseFactory we get a LogData object with allows us to manage the logs data you have found.

![Parsing a log line](diagrams/Log_Parser-log-parsing.png)

### Import and Export
You can import or store a Parse Definition to or from a JSON file.

## Using the Standard Method
By default each entry for your lag parsing will be stored as a Generic entry. This means that all values will be stored as Strings. Each entry will have a :
- Key
- A set of values
- The frequence of the key as found in the logs

## Using the SDK
Using the log parser as an SDK allow you to define your own transformations and also to override many of the behaviors.

In order to use this feature you need to define a class that extends the class StdLogEntry

## Code Structure
Below is a diagram representing the class structure:

![The Class relationship](diagrams/Log_Parser-Classes.png)

## Release Notes
- 1.0.4
  - #6 We Can now import a definition from a JSON file. You can also export a ParseDefinition into a JSON file.
  - #8 & #18  Added the filter function.
  - #13 Added copy constructors.
  - #13 Added a copy method in the StdLogEntry (#13).
  - #14 Added a set method to LogData. This allows you to change a Log data given a key value and ParseDefinition entry title
  - Renamed exception IncorrectParseDefinitionTitleException to IncorrectParseDefinitionException.
  - 
- 1.0.3
  - Introduced the LogData Top Class. This encapsulates all results.
  - Introduced the LogDataFactory
  - Added the groupBy method to extract data from the results
- 1.0.1
  - Open source release.
