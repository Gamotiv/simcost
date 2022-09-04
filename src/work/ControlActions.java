package work;
import dataStore.Edge;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

//extends so we can use savedelaunypairs from here with the same constructed object with this.data=data already set
public class ControlActions {
    public DataStorer data;

    public void generate_cost_surface(DataStorer data) {
        this.data=data;
        double[][] constructionCosts = data.getConstructionCosts();

        Double[] costSurface = new Double[data.getWidth() * data.getHeight() + 1];
        for (int i = 0; i < constructionCosts.length; i++) {
            costSurface[i] = 0.0;
            for (int j = 0; j < constructionCosts[i].length; j++) {

                if (constructionCosts[i][j] != Double.MAX_VALUE) {
                    costSurface[i] += constructionCosts[i][j];
                }
            }
        }
        getImageFromArray(costSurface, data.getWidth(), data.getHeight());
    }

    public void getImageFromArray(Double[] pixels, int width, int height) {
        double minV = Collections.min(Arrays.asList(pixels));
        double maxV = Collections.max(Arrays.asList(pixels));
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] -= minV;
        }
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = 1.0 - pixels[i] / (maxV - minV);
        }
        BufferedImage image = new BufferedImage(width, height, 3);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float c = (float) pixels[x + y * width].doubleValue();
                int rgb = new java.awt.Color(c, c, c).getRGB();
                image.setRGB(x, y, rgb);
            }
        }
        try {
            ImageIO.write(image, "PNG", new File(work.path + "\\Output\\Cost.bmp"));
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
    }
    public void saveDelaunayPairs() {
        HashSet<Edge> delaunayPairs = data.getDelaunayPairs();

        String delaunayPairsPath = work.path+"\\Output\\DelaunayPaths.txt";

        // Save to file.
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(delaunayPairsPath))) {
            bw.write("#  Selected node pairs\n");
            for (Edge pair : delaunayPairs) {
                int vNum = data.sourceNum(pair.v1);
                if (vNum > -1) {
                    bw.write("SOURCE\t" +  String.join("-",data.getSources()[vNum].getLabel().split("\\s+")) + "\t");
                } else {
                    bw.write("SINK\t" + String.join("-",data.getSinks()[data.sinkNum(pair.v1)].getLabel().split("\\s+")) + "\t");
                }
                vNum = data.sourceNum(pair.v2);
                if (vNum > -1) {
                    bw.write("SOURCE\t" +  String.join("-",data.getSources()[vNum].getLabel().split("\\s+")) + "\t");
                } else {
                    bw.write("SINK\t" +  String.join("-",data.getSinks()[data.sinkNum(pair.v2)].getLabel().split("\\s+")) + "\t");
                }
                bw.write(pair.v1 + "\t" + pair.v2 + "\n");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void saveCandidateGraph() {
        HashMap<Edge, Double> graphEdgeCosts = data.getGraphEdgeCosts();
        HashMap<Edge, int[]> graphEdgeRoutes = data.getGraphEdgeRoutes();
        HashMap<Edge, Double> graphEdgeConstructionCosts = data.getGraphEdgeConstructionCosts();
        HashMap<Edge, Double> graphEdgeRightOfWayCosts = data.getGraphEdgeRightOfWayCosts();

        String rawPathsPath = work.path+"\\Output\\CandidateNetwork.txt";

        // Save to file.
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rawPathsPath))) {
            bw.write("Vertex1\tVertex2\tCost\tConCost\tROWCost\tCellRoute\n");
            for (Edge e : graphEdgeRoutes.keySet()) {
                bw.write(e.v1 + "\t" + e.v2 + "\t" + graphEdgeCosts.get(e) + "\t" + graphEdgeConstructionCosts.get(e) + "\t" + graphEdgeRightOfWayCosts.get(e));
                int[] route = graphEdgeRoutes.get(e);
                for (int vertex : route) {
                    bw.write("\t" + vertex);
                }
                bw.write("\n");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
