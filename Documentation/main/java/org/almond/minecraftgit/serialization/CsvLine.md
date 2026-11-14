# CsvLine

> **Software Detailed Documentation:** [CsvLine_SDD.md](./CsvLine_SDD.md)  
> **Source File:** [CsvLine.java](../../../../../../../src/main/java/org/almond/minecraftgit/serialization/CsvLine.java)

## Table of Contents

- [Usage](#usage)
- [CsvLine (Constructor)](#csvline-constructor)
- [format](#format)
- [escape](#escape)
- [parse](#parse)
- [See Also](#see-also)

## Usage

`CsvLine` is a stateless utility used by `QuadrantCsvWriter` to emit a row and by `QuadrantCsvReader` to split one back into cells. For example, `CsvLine.format(List.of("minecraft:oak_planks", "", "minecraft:stone"))` returns `minecraft:oak_planks,,minecraft:stone`, and `CsvLine.parse` on that line returns the same three fields.

Minimal RFC-4180 CSV handling. Block data strings contain commas, so every
cell is quoted when needed rather than using a fragile split(',').

---

## CsvLine (Constructor)

### Signature
```java
private CsvLine()
```

### Description
Private and empty: the class is a namespace for static helpers and cannot be instantiated.

### Parameters
None.

### Returns
`CsvLine` instance.

---

## format

### Signature
```java
public static String format(List<String> fields)
```

### Description
Joins the fields with commas in list order, escaping each one with `escape`; the result is a single CSV line without a line terminator.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `fields` | `List<String>` | Cell values in column order; `null` entries are written as empty cells. |

### Returns
`String` — The comma-separated, RFC-4180-escaped line.

---

## escape

### Signature
```java
public static String escape(String value)
```

### Description
Returns `value` unchanged when it contains no comma, double quote, carriage return or line feed; otherwise wraps it in double quotes and doubles every internal quote.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `value` | `String` | A single cell value, which may be `null` or empty. |

### Returns
`String` — The escaped value; `null` and empty both become the empty string.

---

## parse

### Signature
```java
public static List<String> parse(String line)
```

### Description
Splits one CSV line into fields with a small quote-aware state machine: commas outside quotes end a cell, a quote at the start of a cell opens a quoted section, and `""` inside that section is one literal quote.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `line` | `String` | One CSV line without its terminator; may be empty. |

### Returns
`List<String>` — Mutable list of decoded fields; a quoted cell keeps its commas, an empty line yields one empty field, and malformed quoting is tolerated rather than rejected.

---

## See Also

- **Software Detailed Design:** [CsvLine_SDD.md](./CsvLine_SDD.md)
