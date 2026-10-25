package org.almond.minecraftgit.serialization;

/**
 * Quadrant of the relative XZ plane. The four files per Y level are named by
 * the signs of X and Z, in that order.
 */
public enum Quadrant {

    PP("pp", true, true),
    PN("pn", true, false),
    NP("np", false, true),
    NN("nn", false, false);

    private final String code;
    private final boolean positiveX;
    private final boolean positiveZ;

    Quadrant(String code, boolean positiveX, boolean positiveZ) {
        this.code = code;
        this.positiveX = positiveX;
        this.positiveZ = positiveZ;
    }

    public String code() {
        return code;
    }

    public boolean positiveX() {
        return positiveX;
    }

    public boolean positiveZ() {
        return positiveZ;
    }

    /** Row 0 is the origin-adjacent coordinate; rows grow away from the origin. */
    public int rowToX(int row) {
        return positiveX ? row : -row - 1;
    }

    public int columnToZ(int column) {
        return positiveZ ? column : -column - 1;
    }

    public int rowForX(int rx) {
        return positiveX ? rx : -rx - 1;
    }

    public int columnForZ(int rz) {
        return positiveZ ? rz : -rz - 1;
    }

    public static Quadrant fromOffsets(int rx, int rz) {
        if (rx >= 0) {
            return rz >= 0 ? PP : PN;
        }
        return rz >= 0 ? NP : NN;
    }

    public static Quadrant fromCode(String code) {
        for (Quadrant quadrant : values()) {
            if (quadrant.code.equalsIgnoreCase(code)) {
                return quadrant;
            }
        }
        throw new SerializationException("Unknown CSV quadrant '" + code + "'.");
    }
}
