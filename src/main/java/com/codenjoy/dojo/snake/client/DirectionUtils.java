package com.codenjoy.dojo.snake.client;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;

import java.util.*;

public class DirectionUtils {
    public static Direction getDirection(Point from, Point to) {
        if (to.getX() > from.getX()) {
            return Direction.RIGHT;
        } else if (to.getX() < from.getX()) {
            return Direction.LEFT;
        } else if (to.getY() > from.getY()) {
            return Direction.UP;
        } else {
            return Direction.DOWN;
        }
    }

    public static List<Point> leePath(Point start, Point goal, Set<Point> barriers) {
        Queue<Point> queue = new LinkedList<>();
        Map<Point, Point> pointsMap = new HashMap<>();
        Set<Point> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Point current = queue.poll();

            if (current.equals(goal)) {
                return reconstructPath(pointsMap, goal);
            }

            for (Direction direction : Direction.getValues()) {
                Point next = direction.change(current);

                if (barriers.contains(next) || visited.contains(next)) {
                    continue;
                }

                queue.add(next);
                visited.add(next);
                pointsMap.put(next, current);
            }
        }

        return null;
    }

    private static List<Point> reconstructPath(Map<Point, Point> pointsMap, Point goal) {
        List<Point> path = new ArrayList<>();
        Point current = goal;

        while (current != null) {
            path.add(0, current);
            current = pointsMap.get(current);
        }

        return path;
    }

    public static List<Point> findPath(Point start, Point goal, Set<Point> barriers) {
        Queue<LinkedList<Point>> queue = new LinkedList<>();
        Set<Point> visited = new HashSet<>();

        visited.add(start);
        queue.add(new LinkedList<>(Collections.singletonList(start)));

        while (!queue.isEmpty()) {
            List<Point> path = queue.poll();
            Point current = path.get(path.size() - 1);

            if (current.equals(goal)) {
                return path;
            }

            for (Direction direction : Direction.getValues()) {
                Point next = direction.change(current);

                if (barriers.contains(next) || visited.contains(next)) {
                    continue;
                }

                LinkedList<Point> newPath = new LinkedList<>(path);
                newPath.add(next);

                queue.add(newPath);
                visited.add(next);
            }
        }

        return null;
    }

    public static Direction findSaveDirection(Point current, Set<Point> barriers) {
        List<Direction> directions = new ArrayList<>(Direction.getValues());

        for (Direction direction : directions) {
            Point next = direction.change(current);

            if (barriers.contains((next))) {
                continue;
            }

            if (!willLeadToLoop(next, barriers)) {
                return direction;
            }
        }

        return null;
    }

    private static boolean willLeadToLoop(Point current, Set<Point> barriers) {
        Queue<Point> queue = new LinkedList<>();
        Set<Point> visited = new HashSet<>();
        int moveLimit = 10;

        queue.add(current);
        visited.add(current);

        int moves = 0;

        while (!queue.isEmpty() && moves < moveLimit) {
            Point point = queue.poll();

            for (Direction direction : Direction.getValues()) {
                Point next = direction.change(point);

                if (barriers.contains(next) || visited.contains(next)) {
                    continue;
                }

                queue.add(next);
                visited.add(next);
            }

            moves++;
        }

        return visited.size() < moveLimit / 2;
    }
}
