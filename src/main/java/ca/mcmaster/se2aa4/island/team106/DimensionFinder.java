package ca.mcmaster.se2aa4.island.team106;

import org.json.JSONObject;

public interface DimensionFinder {
    void getDimension(BaseDrone drone, JSONObject decision, JSONObject parameters);
}
