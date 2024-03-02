package ca.mcmaster.se2aa4.island.team106;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class WidthFinder {

    private MapArea mapArea; 
    private int counts = 1; 

    private final Logger logger = LogManager.getLogger();


    public WidthFinder(MapArea mapArea){
        this.mapArea = mapArea; 
    }


    public void getWidthOfIsland(Drone drone, JSONObject decision, JSONObject parameters){
        Direction groundDirection = mapArea.getGroundEchoDirection(); 


        if (mapArea.hasObtainedLength() && !mapArea.getIsAbove()){

            if (mapArea.getHeading() == Direction.E && mapArea.getEastDistance() > 0)
            {
                drone.fly(decision);
                int newEastDistance = mapArea.getEastDistance() -1;
                mapArea.setEastDistance(newEastDistance);

                if (mapArea.getEastDistance() == 0){
                    mapArea.setIsAbove(true);
                } 
            }
            else if (mapArea.getHeading() == Direction.W && mapArea.getWestDistance() > 0){
                drone.fly(decision);
                int newWestDistance = mapArea.getWestDistance() -1;
                mapArea.setWestDistance(newWestDistance);

                if (mapArea.getWestDistance() == 0){
                    mapArea.setIsAbove(true);
                }
            }
            else{
                logger.info("i'm in the zoo with the lions and apes and bears !");
                drone.stop(decision);
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
            logger.info("I have now obtained my WIDTH");
            mapArea.setWidthEndPoint(mapArea.getDroneX());
            
            logger.info("Width of island achieved which is now: " + mapArea.getWidthOfIsland());

            mapArea.getWidthOfIsland(); //internal mapArea memory we dont need to return this no relevance as its gonna be reffered to later via mapArea

            mapArea.setObtainedWidth(true); // now we have obtained the width

            // if we havent obtained the length, we need to transition to length state, and update our heading 

            if (!mapArea.hasObtainedLength())
            {
                logger.info("WE have not found the length yet so now we are transitioning into the length state");
                drone.updateHeading(parameters, decision, groundDirection);

                Direction previousDirection = mapArea.getPrevHeading(); 

                this.setNewEchoGroundDirection(previousDirection);

                drone.setStatus(Status.LENGTH_STATE);
            }
            else{
                //! in this scenario, we would have already obtained our width 
                //! we transition into a new state that makes our drone go to the middle of the island
                logger.info("Both length and width have been found terminating for now!");
                drone.stop(decision);
            }

        }
    }


    private void echo(Drone drone, Direction direction, JSONObject decision, JSONObject parameters){
        if (direction == Direction.N){
            drone.echoNorth(parameters, decision);
        }
        else if(direction == Direction.S){
            drone.echoSouth(parameters, decision);
        }
        else{
            logger.info("This was an invalid echo attempted: " + direction);
        }
    }


    private void setNewEchoGroundDirection(Direction priorDirection){
        if (priorDirection == Direction.E){
            mapArea.setGroundEchoDirection(Direction.W);
        }
        else if (priorDirection == Direction.W){
            mapArea.setGroundEchoDirection(Direction.E);
        }
        else{
            logger.info("Invalid echo direction, your prior direction should be E or W but it was: " + priorDirection);
        }
    }

}
