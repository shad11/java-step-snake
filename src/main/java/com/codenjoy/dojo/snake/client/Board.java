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

import javax.lang.model.element.Element;
import java.util.*;

public class Board extends AbstractBoard<Elements> {
    public Map<Point, Direction> path = new HashMap<>();

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
        Point head = getHead();
        List<Point> snake = getSnake();
        int sizeMax = (int) Math.pow(this.size() - 2, 2) / 4;
        Elements goalStone = snake.size() < sizeMax ? Elements.GOOD_APPLE : Elements.BAD_APPLE;
        Elements badStone = snake.size() < sizeMax ? Elements.BAD_APPLE : Elements.GOOD_APPLE;
        Point goal = get(goalStone).get(0);

        this.path = new HashMap<>();

        if (goal == null) {
            return;
        }

        // TODO: 30 should be a constant
        if (isNear(head, goalStone) || (snake.size() >= 30 && isNear(head, badStone))) {
            Direction direction = DirectionUtils.getDirection(head, goal);
            Point next = direction.change(head);

            if (!isAt(next, Elements.BREAK) && !isAt(next, badStone) && !getSnake().contains(next)) {
                this.path.put(head, direction);

                return;
            }
        }

        Set<Point> visited = new HashSet<>(snake);
        visited.addAll(getWalls());
        visited.addAll(get(badStone));

        this.path = DirectionUtils.findPath(this, head, goal, visited);
    }
}
