package ca.mcmaster.se2aa4.island.team106;

import org.json.JSONObject;

public class LengthFinder implements DimensionFinder, State{
    private MapArea mapArea; 
    private int counts = 1; 


    public LengthFinder(MapArea mapArea){
        this.mapArea = mapArea; 
    }


    @Override
    public void handle(BaseDrone drone, JSONObject decision, JSONObject parameters){
        this.getDimension(drone, decision, parameters);
    }


    @Override
    public void getDimension(BaseDrone drone, JSONObject decision, JSONObject parameters){
        Direction groundDirection = mapArea.getGroundEchoDirection(); // Guaranteed to be East or West

        if (mapArea.hasObtainedWidth() && !mapArea.getIsAbove() && !mapArea.hasObtainedLength()){
            if (mapArea.getHeading() == Direction.S && mapArea.getSouthDistance() > 0)
            {
                drone.fly(decision);
                int newSouthDistance = mapArea.getSouthDistance() -1;
                mapArea.setSouthDistance(newSouthDistance);

                if (mapArea.getSouthDistance() == 0){
                    mapArea.setIsAbove(true);
                    int startPoint = mapArea.getDroneY();
                    mapArea.setLengthStartPoint(startPoint);
                } 
            }
            else if (mapArea.getHeading() == Direction.N && mapArea.getNorthDistance() > 0){
                drone.fly(decision);
                int newNorthDistance = mapArea.getNorthDistance() -1;
                mapArea.setNorthDistance(newNorthDistance);

                if (mapArea.getNorthDistance() == 0){
                    mapArea.setIsAbove(true);
                    int startPoint = mapArea.getDroneY();
                    mapArea.setLengthStartPoint(startPoint);
                }
            }
            else {
                mapArea.setLengthEndPoint(mapArea.getDroneY());
                
                mapArea.getLengthOfIsland(); //internal mapArea memory we dont need to return this no relevance as its gonna be reffered to later via mapArea

                mapArea.setObtainedLength(true); // now we have obtained the length
                echo(drone, groundDirection, decision, parameters);
            }
        }
        else if (this.mapArea.getIsAbove())
        {
            if (this.counts % 3 == 1){
                echo(drone, groundDirection, decision, parameters);
            }
            else if (this.counts % 3 == 2){
                drone.scan(decision);
            }
            else if (this.counts % 3 == 0){
                drone.fly(decision);
            }
            this.counts++;
        }
        else{
            mapArea.setLengthEndPoint(mapArea.getDroneY());
            
            mapArea.getLengthOfIsland(); //internal mapArea memory we dont need to return this no relevance as its gonna be reffered to later via mapArea

            mapArea.setObtainedLength(true); // now we have obtained the length

            // if we havent obtained the width, we need to transition to width state, and update our heading 

            if (!mapArea.hasObtainedWidth())
            {
                drone.updateHeading(parameters, decision, groundDirection);

                Direction previousDirection = mapArea.getPrevHeading(); 

                this.setNewEchoGroundDirection(previousDirection);

                drone.setStatus(Status.WIDTH_STATE);
            }
            else{
                //! in this scenario, we would have already obtained our width 
                //! we transition into a new state that makes our drone go to the middle of the island

                // If we have found both the width and the length, we need to
                // transition into the move to center state where we will move
                // to the center point of the island. We will now update our
                // heading to turn into the direction of the last echo.
                drone.setStatus(Status.MOVE_CENTER_STATE);
                drone.updateHeading(parameters, decision, groundDirection);
            }
        }
    }


    private void echo(BaseDrone drone, Direction direction, JSONObject decision, JSONObject parameters){
        switch (direction)
        {
            case W:
                drone.echo(parameters, decision, Direction.W);
                break;
            case E:
                drone.echo(parameters, decision, Direction.E);
                break;
            default:
                break;
        }
    }


    private void setNewEchoGroundDirection(Direction priorDirection){
        switch (priorDirection) {
            case N:
                mapArea.setGroundEchoDirection(Direction.N);
                break;
            case S:
                mapArea.setGroundEchoDirection(Direction.S);
                break;
            default:
                break;
        }
        
    }
}
