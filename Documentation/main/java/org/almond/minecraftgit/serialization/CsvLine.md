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

TODO: Add usage examples for `CsvLine`.

Minimal RFC-4180 CSV handling. Block data strings contain commas, so every
cell is quoted when needed rather than using a fragile split(',').

---

## CsvLine (Constructor)

### Signature
```java
private CsvLine()
```

### Description
TODO: Describe what `CsvLine` does.

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
TODO: Describe what `format` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `fields` | `List<String>` | TODO: describe |

### Returns
`String` — TODO: describe return value.

---

## escape

### Signature
```java
public static String escape(String value)
```

### Description
TODO: Describe what `escape` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `value` | `String` | TODO: describe |

### Returns
`String` — TODO: describe return value.

---

## parse

### Signature
```java
public static List<String> parse(String line)
```

### Description
TODO: Describe what `parse` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `line` | `String` | TODO: describe |

### Returns
`List<String>` — TODO: describe return value.

---

## See Also

- **Software Detailed Design:** [CsvLine_SDD.md](./CsvLine_SDD.md)
