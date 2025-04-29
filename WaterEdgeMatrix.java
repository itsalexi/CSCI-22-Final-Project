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
        return Arrays.deepHashCode(waterPositions);
    }

    public Boolean[][] getWaterPositions(){
        return waterPositions;
    }
}
