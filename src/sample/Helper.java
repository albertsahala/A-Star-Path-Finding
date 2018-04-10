package sample;

import java.util.ArrayList;
import java.util.Collections;

public class Helper {

    public static ArrayList<MarkerLoc> AStar (ArrayList<MarkerLoc> markers, MarkerLoc start, MarkerLoc goal, Boolean[][] adjMatrix) {
        ArrayList<MarkerLoc> undiscoveredNode = new ArrayList<>();

        ArrayList<MarkerLoc> discoveredNode = new ArrayList<>();

        undiscoveredNode.add(start);

        int[] cameFrom = new int[markers.size()];

        Double[] gScore = new Double[markers.size()];

        for (int i = 0; i < markers.size(); i++) {
            gScore[i] = Double.MAX_VALUE;
        }

        gScore[markers.indexOf(start)] = 0.0;

        Double[] fScore = new Double[markers.size()];

        for (int i = 0; i < markers.size(); i++) {
            fScore[i] = Double.MAX_VALUE;
        }

        fScore[markers.indexOf(start)] = start.getLatLong().distanceFrom(goal.getLatLong());

        while (!undiscoveredNode.isEmpty()) {
            MarkerLoc current = markers.get(lowestFScore(fScore, undiscoveredNode, markers));
            System.out.println(current.getTitle());
            if (current.equals(goal))
                return getOrder(markers, cameFrom, goal, start);

            undiscoveredNode.remove(current);
            discoveredNode.add(current);

            int indexOfCurr = markers.indexOf(current);

            for (int i = 0; i < markers.size(); i++) {
                if (!adjMatrix[indexOfCurr][i]) {
                    continue;
                }
                MarkerLoc neighbor = markers.get(i);

                if (discoveredNode.indexOf(neighbor) != -1) {
                    continue;
                }

                if (undiscoveredNode.indexOf(neighbor) == -1) {
                    undiscoveredNode.add(neighbor);
                }

                Double tentativeGScore = gScore[indexOfCurr] + current.getLatLong().distanceFrom(neighbor.getLatLong());
                if (tentativeGScore >= gScore[i])
                    continue;

                cameFrom[i] = indexOfCurr;
                gScore[i] = tentativeGScore;
                fScore[i] = gScore[i] + neighbor.getLatLong().distanceFrom(goal.getLatLong());
            }
        }

        return null;
    }
    
    private static ArrayList<MarkerLoc> getOrder(ArrayList<MarkerLoc> markers, int[] cameFrom, MarkerLoc goal, MarkerLoc start) {
        int indexOfStart = markers.indexOf(start);
        int index = markers.indexOf(goal);
        ArrayList<MarkerLoc> order = new ArrayList<>();
        order.add(goal);
        while (index != indexOfStart) {
            index = cameFrom[index];
            order.add(markers.get(index));
        }
        Collections.reverse(order);
        return order;
    }

    private static int lowestFScore(Double[] fScore, ArrayList<MarkerLoc> undiscoveredNode, ArrayList<MarkerLoc> markers) {
        Double min = fScore[markers.indexOf(undiscoveredNode.get(0))];
        int minPos = markers.indexOf(undiscoveredNode.get(0));
        for (int i = 1; i < undiscoveredNode.size(); i++) {
            if (fScore[markers.indexOf(undiscoveredNode.get(i))] < min) {
                minPos = markers.indexOf(undiscoveredNode.get(i));
            }
        }
        return minPos;
    }

}
