package com.codenjoy.dojo.snake.client;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snake.model.Elements;

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

    public static String[] findPath(Point start, Point goal, Set<Point> barriers) {
        Queue<LinkedHashMap<Point, String>> queue = new LinkedList<>();
        Set<Point> visited = new HashSet<>();

        visited.add(start);
        queue.add(new LinkedHashMap<>(Collections.singletonMap(start, null)));

        while (!queue.isEmpty()) {
            Map<Point, String> path = queue.poll();
            Point current = path.keySet().toArray(new Point[0])[path.size() - 1];

            if (current.equals(goal)) {
                return Arrays.copyOfRange(path.values().toArray(new String[0]), 1, path.size());
            }

            for (Direction direction : Direction.getValues()) {
                Point next = direction.change(current);

                // TODO: add check for nearest free direction
                if (barriers.contains(next) || visited.contains(next)) {
                    continue;
                }

                LinkedHashMap<Point, String> newPath = new LinkedHashMap<>(path);
                newPath.put(next, direction.toString());

                queue.add(newPath);
                visited.add(next);
            }
        }

        return null;
    }

    public static Direction findSaveDirection(Board board, Point current, Set<Point> barriers) {
        List<Direction> directions = new ArrayList<>(Direction.getValues());
        SortedMap<Integer, Direction> directionsNear = new TreeMap<>();

        for (Direction direction : directions) {
            Point next = direction.change(current);

            if (barriers.contains((next))) {
                continue;
            }

            Point nextDirectionFree = direction.change(next);

            if (!barriers.contains(nextDirectionFree)) {
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
}
