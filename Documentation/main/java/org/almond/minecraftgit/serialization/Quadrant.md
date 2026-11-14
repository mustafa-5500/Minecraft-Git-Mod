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

`Quadrant` enumerates the four quadrants of the relative XZ plane used by the per-Y CSV files; there is no public constructor, so callers use the four constants, `fromOffsets` to bucket a block and `fromCode` to parse a header. For example, a block at relative `rx = -3, rz = 5` belongs to `NP`: `NP.rowForX(-3)` is `2`, `NP.columnForZ(5)` is `5`, and `NP.rowToX(2)` / `NP.columnToZ(5)` recover `-3` / `5`.

Quadrant of the relative XZ plane. The four files per Y level are named by
the signs of X and Z, in that order.

---

## Fields

- `code` (`String`) — Two-letter lowercase code (`pp`, `pn`, `np`, `nn`) that names the quadrant's CSV file and appears in its header.
- `positiveX` (`boolean`) — Whether the quadrant covers non-negative relative X, i.e. whether a row index equals the X offset.
- `positiveZ` (`boolean`) — Whether the quadrant covers non-negative relative Z, i.e. whether a column index equals the Z offset.

---

## code

### Signature
```java
public String code()
```

### Description
Returns the two-letter code that names the quadrant file (`<code>.csv`) and is written into the header as `quadrant=<code>`.

### Parameters
None.

### Returns
`String` — The lowercase code, never `null`.

---

## positiveX

### Signature
```java
public boolean positiveX()
```

### Description
Returns whether the quadrant lies on the positive X side of the origin; it is `true` for `PP` and `PN` and selects the row mapping used by `rowToX`/`rowForX`.

### Parameters
None.

### Returns
`boolean` — `true` when rows map directly to non-negative relative X offsets.

---

## positiveZ

### Signature
```java
public boolean positiveZ()
```

### Description
Returns whether the quadrant lies on the positive Z side of the origin; it is `true` for `PP` and `NP` and selects the column mapping used by `columnToZ`/`columnForZ`.

### Parameters
None.

### Returns
`boolean` — `true` when columns map directly to non-negative relative Z offsets.

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
| `row` | `int` | Zero-based CSV row index, counted from the row next to the origin. |

### Returns
`int` — Relative X stored in that row: `row` for positive-X quadrants, otherwise `-row - 1`.

---

## columnToZ

### Signature
```java
public int columnToZ(int column)
```

### Description
Maps a zero-based CSV column index to the relative Z coordinate stored in that column; it is the inverse of `columnForZ`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `column` | `int` | Zero-based CSV column index, counted from the column next to the origin. |

### Returns
`int` — Relative Z for that column: `column` for positive-Z quadrants, otherwise `-column - 1`.

---

## rowForX

### Signature
```java
public int rowForX(int rx)
```

### Description
Maps a relative X offset to the CSV row index that stores it; it is the inverse of `rowToX`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `rx` | `int` | Relative X offset from the selection origin. |

### Returns
`int` — Zero-based row index: `rx` when X is positive, otherwise `-rx - 1`.

---

## columnForZ

### Signature
```java
public int columnForZ(int rz)
```

### Description
Maps a relative Z offset to the CSV column index that stores it; it is the inverse of `columnToZ`.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `rz` | `int` | Relative Z offset from the selection origin. |

### Returns
`int` — Zero-based column index: `rz` when Z is positive, otherwise `-rz - 1`.

---

## fromOffsets

### Signature
```java
public static Quadrant fromOffsets(int rx, int rz)
```

### Description
Selects the quadrant that contains a relative XZ offset, treating coordinate `0` on either axis as belonging to the positive side.

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `rx` | `int` | Relative X offset from the selection origin. |
| `rz` | `int` | Relative Z offset from the selection origin. |

### Returns
`Quadrant` — `PP` for non-negative `rx` and `rz`, `PN` for non-negative X and negative Z, `NP` for negative X and non-negative Z, and `NN` when both are negative.

---

## fromCode

### Signature
```java
public static Quadrant fromCode(String code)
```

### Description
Resolves the quadrant named by a CSV header code, accepting any letter case; an unknown code (including `null`) raises `SerializationException` with the message `Unknown CSV quadrant '<code>'.`

### Parameters
| Name | Type | Description |
|------|------|-------------|
| `code` | `String` | Two-letter code such as `pp`, `PN` or `nn`. |

### Returns
`Quadrant` — The matching constant.

---

## See Also

- **Software Detailed Design:** [Quadrant_SDD.md](./Quadrant_SDD.md)
