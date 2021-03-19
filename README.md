# log-parser
[![unit-tests](https://github.com/adobe/log-parser/actions/workflows/onPushSimpleTest.yml/badge.svg)](https://github.com/adobe/log-parser/actions/workflows/onPushSimpleTest.yml)

This project was created to allow us to parse and analyze log files in order to gather relevant data. It can be used as is or as an SDK. Where you can define your own parsing.

The basic method for using this library is, that you create a definition for your parsing. This definition allows you to parse a set of log files and extract all entries that match this pattern.

## Defining a Parsing
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
- Is to be kept. In some cases we just need to find a line with certain particularities, but we don't actually want to store the value




## Using the Standard Method
By default each entry for your lag parsing will be stored as a Generic entry. This means that all values will be stored as Strings. Each entry will have a :
- Key
- A set of values
- The frequence of the key as found in the logs

## Using the SDK
Using the log parser as an SDK allow you to define your own transformations and also to override many of the behaviors.

In order to use this feature you need to define a class that extends the class StdLogEntry
