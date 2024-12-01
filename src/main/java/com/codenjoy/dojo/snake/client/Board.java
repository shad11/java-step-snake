package com.codenjoy.dojo.snake.client;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import com.codenjoy.dojo.client.AbstractBoard;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snake.model.Elements;

import java.util.*;

public class Board extends AbstractBoard<Elements> {
    public List<String> moves = new ArrayList<>();

    @Override
    public Elements valueOf(char ch) {
        return Elements.valueOf(ch);
    }

    public List<Point> getApples() {
        return get(Elements.GOOD_APPLE);
    }

    @Override
    protected int inversionY(int y) {
        return size - 1 - y;
    }

    public Direction getSnakeDirection() {
        Point head = getHead();
        if (head == null) {
            return null;
        }
        if (isAt(head.getX(), head.getY(), Elements.HEAD_LEFT)) {
            return Direction.LEFT;
        } else if (isAt(head.getX(), head.getY(), Elements.HEAD_RIGHT)) {
            return Direction.RIGHT;
        } else if (isAt(head.getX(), head.getY(), Elements.HEAD_UP)) {
            return Direction.UP;
        } else {
            return Direction.DOWN;
        }
    }

    public Point getHead() {
        List<Point> result = get(
                Elements.HEAD_UP,
                Elements.HEAD_DOWN,
                Elements.HEAD_LEFT,
                Elements.HEAD_RIGHT);
        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
    }

    public List<Point> getBarriers() {
        List<Point> result = getSnake();
        result.addAll(getStones());
        result.addAll(getWalls());
        return result;
    }

    public List<Point> getSnake() {
        Point head = getHead();
        if (head == null) {
            return Arrays.asList();
        }
        List<Point> result = get(
                Elements.TAIL_END_DOWN,
                Elements.TAIL_END_LEFT,
                Elements.TAIL_END_UP,
                Elements.TAIL_END_RIGHT,
                Elements.TAIL_HORIZONTAL,
                Elements.TAIL_VERTICAL,
                Elements.TAIL_LEFT_DOWN,
                Elements.TAIL_LEFT_UP,
                Elements.TAIL_RIGHT_DOWN,
                Elements.TAIL_RIGHT_UP);
        result.add(0, head);
        return result;
    }

    public boolean isGameOver() {
        return getHead() == null;
    }

    @Override
    public String toString() {
        return String.format("Board:\n%s\n" +
                        "Apple at: %s\n" +
                        "Stones at: %s\n" +
                        "Head at: %s\n" +
                        "Snake at: %s\n" +
                        "Current direction: %s",
                boardAsString(),
                getApples(),
                getStones(),
                getHead(),
                getSnake(),
                getSnakeDirection());
    }

    public List<Point> getStones() {
        return get(Elements.BAD_APPLE);
    }

    public List<Point> getWalls() {
        return get(Elements.BREAK);
    }

    public void move() {
        Point apple = getApples().get(0);
        Point head = getHead();

        this.moves = new ArrayList<>();

        if (apple == null) {
            this.moves.add(Direction.STOP.toString());

            return;
        }

        if (isNear(head, Elements.GOOD_APPLE)) {
            Direction direction = DirectionUtils.getDirection(head, apple);
            Point next = direction.change(head);

            if (!isAt(next, Elements.BREAK) && !isAt(next, Elements.BAD_APPLE) && !getSnake().contains(next)) {
                this.moves.add(direction.toString());

                return;
            }
        }

        LinkedList<Point> snake = new LinkedList<>(getSnake());
        Set<Point> visited = new HashSet<>(snake);
        visited.addAll(getBarriers());

        this.moves = DirectionUtils.findPath(head, apple, snake, visited);

        System.out.println("Moves:" + this.moves);
    }

//    private List<String> bfs(Point start, Point goal) {
//        Queue<Point> queue = new LinkedList<>();
////        List<String> directions = new ArrayList<>();
//        Map<Point, String> directions = new LinkedHashMap<>();
//        Map<Point, Point> predecessors = new HashMap<>();
//        Map<Point, Point> failedPoints = new HashMap<>(); // To avoid revisiting dead-ends
//        LinkedList<Point> snake = new LinkedList<>(getSnake());
//        Set<Point> visited = new HashSet<>(getSnake());
//
//        queue.add(start);
//        visited.add(start);
//
//        while (!queue.isEmpty()) {
//            Point current = queue.poll();
//
//            if (current.equals(goal)) {
//                return new ArrayList<>(directions.values());
//            }
//
//            Set<Point> failedNextPoints = new HashSet<>(visited);
//
//            if (!failedPoints.isEmpty()) {
//                failedPoints.forEach((key, value) -> {
//                    if (value.equals(current)) {
//                        failedNextPoints.add(key);
//                    }
//                });
//            }
//
////            Direction nextDirection = getNextDirection(current, goal, visited);
//            Direction nextDirection = getNextDirection(current, goal, failedNextPoints);
//
//            if (nextDirection == null) { // TODO: Need to handle this case
//                System.out.println("No direction found");
//
//                Point currentPoint = current;
//                Point rollbackPoint = predecessors.get(currentPoint);
//
//                System.out.println("Current point: " + currentPoint);
//                System.out.println("Goal point: " + goal);
//                System.out.println("Visited points: " + visited);
//                System.out.println("Predecessors: " + predecessors);
//                System.out.println("Rollback point: " + rollbackPoint);
//                System.out.println("Directions: " + directions.values());
//
//                String currentValue = directions.remove(currentPoint);
//                failedPoints.put(currentPoint, rollbackPoint);
//
//                while (true) {
//                    boolean isValid = false;
//
//                    for (Direction direction : Direction.getValues()) {
//                        Point next = direction.change(rollbackPoint);
//
//                        if (isAt(next, Elements.BREAK) || isAt(next, Elements.BAD_APPLE)
//                                || getSnake().contains(next)
//                                || failedPoints.get(next) == rollbackPoint
//                        ) {
//                            continue;
//                        }
//
//                        isValid = true;
//                        break;
//                    }
//
//                    if (isValid) {
//                        System.out.println("Directions after rollback: " + directions.values());
//                        System.out.println("Failed points: " + failedPoints);
//                        return new ArrayList<>(directions.values());
//                    }
//
//                    currentValue = directions.remove(rollbackPoint);
//
//                    if (directions.isEmpty()) {
//                        break;
//                    }
//
//                    currentPoint = rollbackPoint;
//                    rollbackPoint = predecessors.get(rollbackPoint);
//                }
//
//                System.out.println("Directions after rollback: " + directions.values());
//
//                if (directions.isEmpty()) { // Restart BFS from the rollback point
//                    rollbackPoint = start;
//                }
//
//                queue.add(rollbackPoint);
//                continue;
//            }
//
//            Point next = nextDirection.change(current);
//
//            queue.add(next);
//            visited.add(next);
//            snake.addFirst(next);
//            predecessors.put(next, current);
//            directions.put(next, nextDirection.toString());
//
//            Point tail = snake.removeLast();
//
//            visited.remove(tail);
//        }
//
//        return null;
//    }
}
