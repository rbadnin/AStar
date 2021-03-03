import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;


class Node implements Comparable<Node> {

    public final Point p;
    public final int h;

    public Node(Point p, int h) {
        this.p = p;
        this.h = h;
    }

    @Override
    public int compareTo(Node o) {
        return Integer.compare(h, o.h);
    }
}


class AStarPathingStrategyParthiv implements PathingStrategy {

    public int heuristic(Point current, Point start, Point end) {
        int distFromStart = Math.abs(start.x - current.x) + Math.abs(start.y - current.y);
        int distFromEnd = Math.abs(end.x - current.x) + Math.abs(end.y - current.y);
        return distFromStart + distFromEnd;
        // return Math.min(distFromStart, distFromEnd);
        // return distFromEnd;
    }

    public List<Point> computePath(Point start, Point end, Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors) {
        List<Point> path = new LinkedList<>();

        PriorityQueue<Node> astar = new PriorityQueue<>();
        Hashtable<Point, Point> seen = new Hashtable<>();
        Point pathend = end;
        seen.put(start, new Point(-1, -1));
        astar.add(new Node(start, heuristic(start, start, end)));
        while (!astar.isEmpty()) {
            Node current = astar.poll();
            if (withinReach.test(current.p, end)) {
                pathend = current.p;
                break;
            }
            path = potentialNeighbors.apply(current.p)
                    .filter(canPassThrough)
                    .filter(p -> !seen.containsKey(p))
                    .collect(Collectors.toList());
            for (Point p : path) {
                seen.put(p, current.p);
                astar.add(new Node(p, heuristic(p, start, end)));
            }
        }
        path.clear();
        if (pathend != end) {
            while (seen.containsKey(pathend) && pathend != start) {
                path.add(pathend);
                pathend = seen.get(pathend);
            }
        }
        return path;
    }
}
