package work;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class work {
    static DataStorer data = new DataStorer();
    static String loc;

    static {
        try {
            loc = new File(work.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    static File loca = new File(loc);
    static File loca2 = new File(loca.getParent());
    static File loca3 = new File(loca2.getParent());
    public static String path = loca3.getParent();

    public work() throws URISyntaxException {
    }

    public static void main(String args[]) throws IOException {
        try {
            String landcover_path = "";
            if (Files.exists(Paths.get(path + "/Input/landcover.asc"))) {
                landcover_path = path + "/Input/landcover.asc";
            } else {
                System.exit(23);
            }
            costSolver costs = new costSolver();
            costSolver cells = new costSolver();
            Dictionary costList = new Hashtable();
            Dictionary rowList = new Hashtable();

            Dictionary headerInfo = costs.getHeader(landcover_path);
            double[][] distMult = costs.distanceMultiplier(headerInfo);

            boolean isSelectedPop = false;
            boolean isSelectedAspect = false;
            boolean isSelectedSlope = false;
            boolean isSelectedRiver = false;
            boolean isSelectedRoad = false;
            boolean isSelectedRail = false;
            boolean isSelectedPipeline = false;

            System.out.println("Importing Landcover Data for Construction... ");
            costList = costs.landcoverInput(isSelectedPop, landcover_path);

            if (isSelectedSlope) {
                System.out.println("Importing Slope Data ... ");
                costList = costs.slopeInput(costList, isSelectedAspect, path+"\\Input\\slope.asc");
            }
            if (isSelectedRiver) {
                System.out.println("Importing River Data ... ");
                costList = costs.addRiverCrossings(costList, headerInfo, path+"\\Input\\rivers.asc");
            }
            if (isSelectedRoad){
            System.out.println("Importing Roads Data ... ");
            costList = costs.addRoadCrossings(costList, headerInfo, path+"\\Input\\roads.asc");
            }
            if (isSelectedRail){
            System.out.println("Importing Railroad Data ... ");
            costList = costs.addRailCrossings(costList, headerInfo, path+"\\Input\\railroads.asc");
            }
            if (isSelectedPipeline) {
                System.out.println("Importing Pipeline Data ... ");
                costList = costs.addPipelineCorridor(costList, headerInfo, path + "\\Input\\pipelines.asc");
            }
            BufferedWriter outputConstruction = new BufferedWriter(new FileWriter(path + "\\Output\\Construction Costs.txt"));
            System.out.println("Calculating Distance ...");
            costList = costs.solveDistance(headerInfo, distMult, costList, landcover_path);
            System.out.println("Writing to files...");
            costs.writeTxt(costList, headerInfo, outputConstruction, landcover_path);
            System.out.println("Construction calculations are complete.");


//commenting out right of way
//            System.out.println("Importing Landcover Data for ROWS ... ");
//            rowList = costs.landcoverInput(isSelectedPop, landcover_path);
//
//            rowList = costs.solveDistance(headerInfo, distMult, rowList, landcover_path);
//            BufferedWriter outputROWS = new BufferedWriter(new FileWriter("D:\\.Spaghetti\\Cortex\\Outputs\\RightOfWay Costs.txt"));
//            costs.writeTxt(rowList, headerInfo, outputROWS, landcover_path);
//
//            System.out.println("The Rights of way calculations are complete. ");

        } catch (IOException ex) {
            Logger.getLogger(work.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(work.class.getName()).log(Level.SEVERE, null, ex);
        }

        ControlActions controlActions = new ControlActions();
        System.out.println("Generating Cost Surface");
        controlActions.generate_cost_surface(data);
        System.out.println("Generating Delaunay Pairs");
        controlActions.saveDelaunayPairs();
        System.out.println("Generating Candidate Graph");
        controlActions.saveCandidateGraph();
        System.out.println("Done");
    }

}
