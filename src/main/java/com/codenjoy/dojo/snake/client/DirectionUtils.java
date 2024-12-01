package com.codenjoy.dojo.snake.client;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;

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

    public static List<String> findPath(Point start, Point goal, LinkedList<Point> snake, Set<Point> barriers) {
        Queue<Point> queue = new LinkedList<>();
        List<String> directions = new ArrayList<>();
        Set<Point> visited = new HashSet<>(barriers);

        queue.add(start);

        while (!queue.isEmpty()) {
            Point current = queue.poll();

            if (current.equals(goal)) {
                return directions;
            }

            Direction nextDirection = getNextDirection(current, goal, visited);

//            Point tail = snake.removeLast();
//            visited.remove(tail);

            if (nextDirection == null) {
//                return new ArrayList<>(Arrays.asList(Optional.ofNullable(findSaveDirection(current, barriers)).orElse(Direction.STOP).toString()));
                // TODO: IF STOP eat read apple
                return new ArrayList<>(Collections.singletonList(Optional.ofNullable(findSaveDirection(start, barriers)).orElse(Direction.STOP).toString()));
            }

            Point next = nextDirection.change(current);

            queue.add(next);
            visited.add(next);
//            snake.addFirst(next);

            directions.add(nextDirection.toString());
        }

        return null;
    }

    public static Direction findSaveDirection(Point current, Set<Point> visited) {
        List<Direction> directions = new ArrayList<>(Direction.getValues());

        for (Direction direction : directions) {
            Point next = direction.change(current);

            if (!visited.contains(next)) {
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
//        Direction nearestDirection = getDirection(current, goal);
//        Point next = nearestDirection.change(current);
//
//        if (!board.isAt(next, Elements.BREAK) && !board.isAt(next, Elements.BAD_APPLE) && !visited.contains(next)) {
//            return nearestDirection;
//        }
//
//        List<Direction> directions = new ArrayList<>(Direction.getValues());
//        directions.remove(nearestDirection);
//
//        for (Direction direction : directions) {
//            next = direction.change(current);
//
//            if (!board.isAt(next, Elements.BREAK) && !board.isAt(next, Elements.BAD_APPLE) && !visited.contains(next)) {
//                return direction;
//            }
//        }
//
//        return null;
    }

    private static int manhattanDistance(Point a, Point b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }
}
