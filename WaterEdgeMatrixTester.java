import java.util.HashMap;
import java.util.Map;

public class WaterEdgeMatrixTester {
    public static void main(String[] args) {
        Map<WaterEdgeMatrix, Integer> map = new HashMap<>();
        map.put(new WaterEdgeMatrix(
            new Boolean[][] {
            {null, false, false},
            {true, false, false},
            {null, false, false}}
        ), 100);

        WaterEdgeMatrix test = new WaterEdgeMatrix(
            new Boolean[][] {
                {true, false, false},
                {true, false, false},
                {true, false, false}
            }
        );

        System.out.println(map.containsKey(test));
    }
}
