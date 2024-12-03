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


import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.RandomDice;

import java.util.*;

/**
 * User: your name
 */
public class YourSolver implements Solver<Board> {

    private Dice dice;
    private Board board;

    public YourSolver(Dice dice) {
        this.dice = dice;
    }

    @Override
    public String get(Board board) {
        this.board = board;

        if (board.getHead() == null) {
            return this.get(board);
        }

        Direction direction = board.getNextDirection();

        if (direction != null) {
            return direction.toString();
        }

        return Direction.UP.toString();
    }
//    public String get(Board board) {
//        this.board = board;
//
//        Point head = board.getHead();
//        Point apple = board.getApples().get(0);
//
//        List<Point> obstacles = new ArrayList<>();
//        obstacles.addAll(board.getSnake());
//        obstacles.addAll(board.getWalls());
//        obstacles.addAll(board.getStones());
//
//        List<Point> path = findShortestPath(head, apple, obstacles);
//
//        if (path != null && path.size() > 1) {
//            Point nextPoint = path.get(1);
//            return getDirection(head, nextPoint);
//        }
//
//        for (Direction direction : Direction.values()) {
//            Point nextPoint = direction.change(head);
//            if (!obstacles.contains(nextPoint)) {
//                return direction.toString();
//            }
//        }
//
//        return Direction.UP.toString();
//    }

    private List<Point> findShortestPath(Point start, Point goal, List<Point> obstacles) {
        Queue<List<Point>> queue = new LinkedList<>();
        Set<Point> visited = new HashSet<>();

        queue.add(Collections.singletonList(start));
        visited.add(start);

        while (!queue.isEmpty()) {
            List<Point> path = queue.poll();
            Point current = path.get(path.size() - 1);

            if (current.equals(goal)) {
                return path;
            }

            for (Direction direction : Direction.values()) {
                Point next = direction.change(current);

                if (!visited.contains(next) && !obstacles.contains(next)) {
                    visited.add(next);
                    List<Point> newPath = new ArrayList<>(path);
                    newPath.add(next);
                    queue.add(newPath);
                }
            }
        }

        return null;
    }

    private String getDirection(Point head, Point nextPoint) {
        if (nextPoint.getX() > head.getX()) {
            return Direction.RIGHT.toString();
        } else if (nextPoint.getX() < head.getX()) {
            return Direction.LEFT.toString();
        } else if (nextPoint.getY() > head.getY()) {
            return Direction.UP.toString();
        } else if (nextPoint.getY() < head.getY()) {
            return Direction.DOWN.toString();
        }
        return Direction.UP.toString();
    }

    public static void main(String[] args) {
        WebSocketRunner.runClient(
                // paste here board page url from browser after registration
                "http://178.128.203.65/codenjoy-contest/board/player/qbohdyc2n3j018ca28vi?code=496027681218043421",
                new YourSolver(new RandomDice()),
                new Board());
    }
}
