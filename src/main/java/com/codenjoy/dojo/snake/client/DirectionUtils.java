package com.codenjoy.dojo.snake.client;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snake.model.Elements;

import java.util.*;

public class DirectionUtils {
    public static Direction getDirection(Point from, Point to) {
        if (to.getY() == from.getY() && to.getX() > from.getX()) {
            return Direction.RIGHT;
        } else if (to.getY() == from.getY() && to.getX() < from.getX()) {
            return Direction.LEFT;
        } else if (to.getX() == from.getX() && to.getY() > from.getY()) {
            return Direction.UP;
        } else {
            return Direction.DOWN;
        }
    }

    public static Map<Point, Direction> findPath(Point start, Point goal, Set<Point> barriers) {
        Queue<Point> queue = new LinkedList<>();
        Map<Point, Direction> path = new LinkedHashMap<>();
        Set<Point> visited = new HashSet<>(barriers);

        queue.add(start);

        while (!queue.isEmpty()) {
            Point current = queue.poll();

            if (current.equals(goal)) {
                return path;
            }

            Direction nextDirection = getNextDirection(current, goal, visited);

            if (nextDirection != null) {
                Point next = nextDirection.change(current);

                queue.add(next);
                visited.add(next);

                path.put(current, nextDirection);
            }
        }

        return null;
    }

    public static Direction findSaveDirection(Board board, Point current, Set<Point> visited) {
        List<Direction> directions = new ArrayList<>(Direction.getValues());
        SortedMap<Integer, Direction> directionsNear = new TreeMap<>();

        for (Direction direction : directions) {
            Point next = direction.change(current);

            if (visited.contains((next))) {
                continue;
            }

            Point nextDirectionFree = direction.change(next);

            if (!visited.contains(nextDirectionFree)) {
                directionsNear.put(
                        board.countNear(nextDirectionFree, Elements.NONE) + board.countNear(next, Elements.NONE),
                        direction
                );
            } else {
                directionsNear.put(
                        board.countNear(next, Elements.NONE),
                        direction
                );
            }
        }

        if (!directionsNear.isEmpty()) {
            return directionsNear.get(directionsNear.lastKey());
        }

        return null;
    }

    private static Direction getNextDirection(Point current, Point goal, Set<Point> visited) {
        Map<Direction, Point> nextPoints = new HashMap<>();
        int minDistance = 0;

        for (Direction direction : Direction.getValues()) {
            Point next = direction.change(current);

            if (next.equals(goal)) {
                return direction;
            }

            if (visited.contains(next) || !isSaveNextPoint(next, visited)) {
                continue;
            }

            if (minDistance == 0) {
                minDistance = manhattanDistance(next, goal);
                nextPoints.put(direction, next);
            } else {
                int distance = manhattanDistance(next, goal);

                if (distance < minDistance) {
                    minDistance = distance;
                    nextPoints.clear();
                    nextPoints.put(direction, next);
                } else if (distance == minDistance) {
                    nextPoints.put(direction, next);
                }
            }
        }

        if (nextPoints.size() == 1) {
            return nextPoints.keySet().iterator().next();
        }

        if (nextPoints.size() > 1) {
//            System.out.println("Next points: " + nextPoints);
//            System.out.println("Visited points: " + visited);
            for (Map.Entry<Direction, Point> entry : nextPoints.entrySet()) {
                Point point = entry.getValue();

                if (goal.getX() == point.getX() || goal.getY() == point.getY()) {
                    // TODO: Here we need to add alternative path, cause sometimes it could be blocked
//                    System.out.println("Found next point: " + point);
                    return entry.getKey();
                }
            }
        }

//        System.out.println("Next points: " + nextPoints);
//        System.out.println("Visited points: " + visited);

        return nextPoints.isEmpty() ? null : nextPoints.keySet().iterator().next();
    }

    private static boolean isSaveNextPoint(Point point, Set<Point> visited) {
        List<Direction> directions = new ArrayList<>(Direction.getValues());

        for (Direction direction : directions) {
            Point next = direction.change(point);

            if (!visited.contains(next)) {
                return true;
            }
        }

        return false;
    }

    private static int manhattanDistance(Point a, Point b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }
}
