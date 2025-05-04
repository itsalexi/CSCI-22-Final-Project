import java.util.Arrays;

public class WaterEdgeMatrix {

    private Boolean[][] waterPositions;

    public WaterEdgeMatrix(Boolean[][] wp) {
        waterPositions = wp;
    }

    @Override
    public boolean equals(Object another) {
        if (another == this) {
            return true;
        }

        if (another == null) {
            return false;
        }

        if (another.getClass() != this.getClass()) {
            return false;
        }

        WaterEdgeMatrix other = (WaterEdgeMatrix) another;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (waterPositions[i][j] == null || other.getWaterPositions()[i][j] == null) {
                    continue;
                }
                if (waterPositions[i][j] != other.getWaterPositions()[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        Boolean[][] normalizedWaterPositions = new Boolean[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (waterPositions[i][j] == null) {
                    normalizedWaterPositions[i][j] = true;
                } else {
                    normalizedWaterPositions[i][j] = waterPositions[i][j];
                }
            }
        }
        return Arrays.deepHashCode(normalizedWaterPositions);
    }

    public Boolean[][] getWaterPositions() {
        return waterPositions;
    }

    @Override
    public String toString() {
        String out = "";
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                out += waterPositions[i][j] ? "true " : "false ";
            }
            out += "\n";
        }
        return out;
    }
}
