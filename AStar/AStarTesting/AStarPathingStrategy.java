import org.w3c.dom.Node;

import java.nio.channels.Pipe;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class PointNode implements Comparable<PointNode> {
    private int f;
    private final Point point;
    private final PointNode priorVertex;

    public PointNode(int f, Point point, PointNode priorVertex){
        this.f = f;
        this.point = point;
        this.priorVertex = priorVertex;
    }

    @Override
    public int compareTo(PointNode other){

        return getF() - other.getF();
    }



    public int getF() {
        return f;
    }

    public Point getPoint() {
        return point;
    }

    public PointNode getPriorVertex() {
        return priorVertex;
    }

}

class AStarPathingStrategy
        implements PathingStrategy
{

    //hashset for closed list
    //priority queue for open list


    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors)
    {
        PriorityQueue<PointNode> openList = new PriorityQueue<>();
        Hashtable<Point, Point> closedList = new Hashtable<>(); // key is point, value is prior node

        // start open list with starting node
        PointNode current = new PointNode(calculateF(start, start, end), start, null);
        openList.add(current);

        // check if current node is null, or if the current node is within reach of the goal
        while (current != null && !withinReach.test(current.getPoint(), end)) {
            List<Point> neighbors =
                    potentialNeighbors.apply(current.getPoint()) // get potential neighbors
                            .filter(canPassThrough) //filter if it can pass through
                            .filter(p -> !closedList.containsKey(p)) // filter out any points in closed list
                            .collect(Collectors.toList());

            for (Point p : neighbors)
            {
                PointNode pn = new PointNode(calculateF(start, p, end), p, current);

                // check to see if point is in open list already, if not, add it to open list
                boolean add = true;
                for (PointNode open : openList)
                    if (open.getPoint().equals(pn.getPoint())){
                        add = false;
                        break;
                    }

                if (add){
                    openList.add(pn);
                }

            }

            if (current.getPriorVertex() == null) {
                closedList.put(current.getPoint(), start);
            } else {
                closedList.put(current.getPoint(), current.getPriorVertex().getPoint());
            }

            openList.remove(current);

            current = openList.poll();
        }

        List<Point> path = new LinkedList<>();

        // add points to the path using hashtable
        Point next;
        if (current != null) {
            path.add(current.getPoint());
            next = current.getPriorVertex().getPoint();

            while (next != start) {
                path.add(next);
                next = closedList.get(next);
            }
        }


        Collections.reverse(path);
        return path;
    }

    private int distance(Point start, Point end){
        return Math.abs(start.x-end.x) + Math.abs(start.y-end.y);
    }

    private int calculateF(Point start, Point option, Point goal){
        int g = distance(start, option); //calculate by links
        int h = distance(option, goal);
        return g+h;
    }



}
