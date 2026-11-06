# Quadrant

> **Software Detailed Documentation:** [Quadrant_SDD.md](./Quadrant_SDD.md)  
> **Source File:** [Quadrant.java](../../../../../../../src/main/java/org/almond/minecraftgit/serialization/Quadrant.java)

## Table of Contents

- [Usage](#usage)
- [Fields](#fields)
- [code](#code)
- [positiveX](#positivex)
- [positiveZ](#positivez)
- [rowToX](#rowtox)
- [columnToZ](#columntoz)
- [rowForX](#rowforx)
- [columnForZ](#columnforz)
- [fromOffsets](#fromoffsets)
- [fromCode](#fromcode)
- [See Also](#see-also)

## Usage

TODO: Add usage examples for `Quadrant`.

Quadrant of the relative XZ plane. The four files per Y level are named by
the signs of X and Z, in that order.

---

## Fields

- `code` (`String`) — TODO: describe field
- `positiveX` (`boolean`) — TODO: describe field
- `positiveZ` (`boolean`) — TODO: describe field

---

## code

### Signature
```java
public String code()
```

### Description
TODO: Describe what `code` does.

### Parameters
None.

### Returns
`String` — TODO: describe return value.

---

## positiveX

### Signature
```java
public boolean positiveX()
```

### Description
TODO: Describe what `positiveX` does.

### Parameters
None.

### Returns
`boolean` — TODO: describe return value.

---

## positiveZ

### Signature
```java
public boolean positiveZ()
```

### Description
TODO: Describe what `positiveZ` does.

### Parameters
None.

### Returns
`boolean` — TODO: describe return value.

---

## rowToX

### Signature
```java
public int rowToX(int row)
```

### Description
Row 0 is the origin-adjacent coordinate; rows grow away from the origin.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `row` | `int` | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## columnToZ

### Signature
```java
public int columnToZ(int column)
```

### Description
TODO: Describe what `columnToZ` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `column` | `int` | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## rowForX

### Signature
```java
public int rowForX(int rx)
```

### Description
TODO: Describe what `rowForX` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `rx` | `int` | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## columnForZ

### Signature
```java
public int columnForZ(int rz)
```

### Description
TODO: Describe what `columnForZ` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `rz` | `int` | TODO: describe |

### Returns
`int` — TODO: describe return value.

---

## fromOffsets

### Signature
```java
public static Quadrant fromOffsets(int rx, int rz)
```

### Description
TODO: Describe what `fromOffsets` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `rx` | `int` | TODO: describe |
| `rz` | `int` | TODO: describe |

### Returns
`Quadrant` — TODO: describe return value.

---

## fromCode

### Signature
```java
public static Quadrant fromCode(String code)
```

### Description
TODO: Describe what `fromCode` does.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `code` | `String` | TODO: describe |

### Returns
`Quadrant` — TODO: describe return value.

---

## See Also

- **Software Detailed Design:** [Quadrant_SDD.md](./Quadrant_SDD.md)
