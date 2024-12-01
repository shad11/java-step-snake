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

    public static Map<Point, Direction> findPath(Board board, Point start, Point goal, Set<Point> barriers) {
        Queue<Point> queue = new LinkedList<>();
        Map<Point, Direction> path = new HashMap<>();
        Set<Point> visited = new HashSet<>(barriers);

        queue.add(start);

        while (!queue.isEmpty()) {
            Point current = queue.poll();

            if (current.equals(goal)) {
                return path;
            }

            Direction nextDirection = getNextDirection(current, goal, visited);

            if (nextDirection == null) {
                // TODO: IF STOP eat read apple
                Direction saveDirection = findSaveDirection(board, start, barriers);

                return new HashMap<>(Collections.singletonMap(start, Optional.ofNullable(saveDirection).orElse(Direction.STOP)));
            }

            Point next = nextDirection.change(current);

            queue.add(next);
            visited.add(next);

            path.put(current, nextDirection);
        }

        return null;
    }

    public static Direction findSaveDirection(Board board, Point current, Set<Point> visited) {
        List<Direction> directions = new ArrayList<>(Direction.getValues());

        for (Direction direction : directions) {
            Point next = direction.change(current);
//            System.out.println("Next: " + next);
//            System.out.println("None count: " + board.countNear(next, Elements.NONE));

            if (!visited.contains(next) && board.countNear(next, Elements.NONE) > 3) {
                return direction;
            }
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

            if (visited.contains(next)) {
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

        if (nextPoints.size() > 1) {
            System.out.println("Next points: " + nextPoints);
            System.out.println("Visited points: " + visited);
            for (Map.Entry<Direction, Point> entry : nextPoints.entrySet()) {
                Point point = entry.getValue();

                if (goal.getX() == point.getX() || goal.getY() == point.getY()) {
                    System.out.println("Found next point: " + point);
                    return entry.getKey();
                }
            }
        }

        System.out.println("Next points: " + nextPoints);
        System.out.println("Visited points: " + visited);

        return nextPoints.isEmpty() ? null : nextPoints.keySet().iterator().next();
    }

    private static int manhattanDistance(Point a, Point b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }
}
