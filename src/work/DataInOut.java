package work;

import dataStore.Edge;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;

import static utilities.Utilities.isDouble;

public class DataInOut {
    private DataStorer data;
     DataInOut(DataStorer data){
        this.data = data;
    }
    public void loadStuff() throws IOException {
        String path = work.path+"\\Output\\Construction Costs.txt";
        BufferedReader br = new BufferedReader(new FileReader(path));

        br.readLine();
        br.readLine();

        // Read dimensions.
        String line = br.readLine();
        String[] elements = line.split("\\s+");
        data.setWidth(Integer.parseInt(elements[1]));
        line = br.readLine();
        elements = line.split("\\s+");
        data.setHeight(Integer.parseInt(elements[1]));

        // Read conversions.
        line = br.readLine();
        elements = line.split("\\s+");
        data.setLowerLeftX(Double.parseDouble(elements[1]));

        line = br.readLine();
        elements = line.split("\\s+");
        data.setLowerLeftY(Double.parseDouble(elements[1]));

        line = br.readLine();
        elements = line.split("\\s+");
        data.setCellSize(Double.valueOf(elements[1]));
    }
    public void loadCosts()  {
        String path = work.path+"\\Output\\Construction Costs.txt";

        try {
            loadStuff();
            loadSources();
            loadSinks();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        double[][] constructionCosts = new double[data.getWidth() * data.getHeight() + 1][8];

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            // Create construction costs array.

            for (int i = 0; i < constructionCosts.length; i++) {
                for (int j = 0; j < constructionCosts[i].length; j++) {
                    constructionCosts[i][j] = Double.MAX_VALUE;
                }
            }
            for (int i = 0; i < 8; i++) {
                br.readLine();
            }
            String line = br.readLine();
            while (line != null) {
                String costLine = br.readLine();
                String[] costs = costLine.split("\\s+");
                String[] cells = line.split("\\s+");

                int centerCell = Integer.parseInt(cells[0]);
                for (int i = 1; i < costs.length; i++) {
                    constructionCosts[centerCell][data.getNeighborNum(centerCell, Integer.parseInt(cells[i]))] = Double.parseDouble(costs[i]);
                }
                line = br.readLine();
            }
        } catch (IOException e2) {
            System.out.println(e2.getMessage());
        }
        data.setConstructionCosts(constructionCosts);
        data.setRoutingCosts(constructionCosts);
    }
    private void loadSources() throws IOException {
        String sourcePath = work.path+"\\Input\\Sources.csv";
        BufferedReader br = new BufferedReader(new FileReader(sourcePath));
        br.readLine();
        br.readLine();
        br.readLine();
        String line = br.readLine();
        ArrayList<Source> sources = new ArrayList<>();
        while (line != null && !line.startsWith(",") && !line.startsWith(" ")) {
            String[] elements = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
            Source source = new Source(data);

            source.setLabel(elements[1]);
            source.setCellNum(data.latLonToCell(Double.parseDouble(elements[8]), Double.parseDouble(elements[7])));

            if (elements[3].equals("") || (isDouble(elements[3]) && Double.parseDouble(elements[3]) == 0)) {
                if (elements[4].equals("")) {
                    source.setOpeningCost(0.0);
                } else {
                    source.setOpeningCost(Double.parseDouble(elements[4]));
                }

                if (elements[5].equals("")) {
                    source.setOMCost(0.0);
                } else {
                    source.setOMCost(Double.parseDouble(elements[5]));
                }

                if (elements[6].equals("")) {
                    source.setCaptureCost(0.0);
                } else {
                    source.setCaptureCost(Double.parseDouble(elements[6]));
                }
            } else {
                source.setOpeningCost(0.0);
                source.setOMCost(0.0);
                source.setCaptureCost(Double.parseDouble(elements[3]));
            }
            source.setProductionRate(Double.parseDouble(elements[2]));
            sources.add(source);
            line = br.readLine();
        }
        data.setSources(sources.toArray(new Source[0]));
        }
    private void loadSinks() throws IOException {
        String sinkPath = work.path+"\\Input\\Sinks.csv";
        BufferedReader br = new BufferedReader(new FileReader(sinkPath));
        br.readLine();
        br.readLine();
        br.readLine();
        String line = br.readLine();
        ArrayList<Sink> sinks = new ArrayList<>();
        while (line != null && !line.startsWith(",") && !line.startsWith(" ")) {
            String[] elements = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
            Sink sink = new Sink(data);
            sink.setLabel(elements[1]);
            sink.setCellNum(data.latLonToCell(Double.parseDouble(elements[11]), Double.parseDouble(elements[10])));

            if (elements[3].equals("") || (isDouble(elements[3]) && Double.parseDouble(elements[3]) == 0)) {
                if (elements[4].equals("")) {
                    sink.setOpeningCost(0.0);
                } else {
                    sink.setOpeningCost(Double.parseDouble(elements[4]));
                }
                if (elements[5].equals("")) {
                    sink.setOMCost(0.0);
                } else {
                    sink.setOMCost(Double.parseDouble(elements[5]));
                }

                if (elements[6].equals("")) {
                    sink.setWellCapacity(0);
                } else {
                    sink.setWellCapacity(Double.parseDouble(elements[6]));
                }

                if (elements[7].equals("")) {
                    sink.setWellOpeningCost(0);
                } else {
                    sink.setWellOpeningCost(Double.parseDouble(elements[7]));
                }

                if (elements[8].equals("")) {
                    sink.setWellOMCost(0);
                } else {
                    sink.setWellOMCost(Double.parseDouble(elements[8]));
                }

                if (elements[9].equals("")) {
                    sink.setInjectionCost(0.0);
                } else {
                    sink.setInjectionCost(Double.parseDouble(elements[9]));
                }
            } else {
                sink.setOpeningCost(0.0);
                sink.setOMCost(0.0);
                sink.setWellCapacity(Double.MAX_VALUE);
                sink.setWellOpeningCost(0.0);
                sink.setWellOMCost(0.0);
                sink.setInjectionCost(Double.parseDouble(elements[3]));
            }
            sink.setCapacity(Double.parseDouble(elements[2]));
            sinks.add(sink);
            line = br.readLine();
        }
        data.setSinks(sinks.toArray(new Sink[0]));
    }


}


