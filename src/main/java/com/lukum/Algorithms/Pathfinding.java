package com.lukum.Algorithms;
import static com.lukum.Manager.Enums.UnitType.ARMOR_WALL;
import static com.lukum.Manager.Enums.UnitType.BRICK_WALL;
import static com.lukum.Manager.Enums.UnitType.EMPTY;
import static com.lukum.Manager.Enums.UnitType.WATER;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Vector;

import org.joml.Vector2i;

import com.lukum.Manager.Enums;
import com.lukum.Manager.Enums.UnitType;
import com.lukum.Manager.Enums.movementDirection;
import com.lukum.gameProps.Tile;

public class Pathfinding {
    public static class Node implements Comparable<Node> {
        public Vector2i pos;
        public int cost;
        public int priority;

        public Node(Vector2i pos, int cost, Vector2i end) {
            this.pos = pos;
            this.cost = cost;
            this.priority = cost + Math.abs(pos.x - end.x) + Math.abs(pos.y - end.y); 
        }

        @Override
        public int compareTo(Node otherNode) {
            return Integer.compare(this.priority, otherNode.priority);
        }
    }

    private static final int[] dr = {-1, 1, 0, 0};  // Направления: вверх, вниз, влево, вправо
    private static final int[] dc = {0, 0, -1, 1};


public static ArrayList<movementDirection> findCoverPath(Tile[][] grid, Vector2i start, Vector2i playerPos) {
    int rows = grid.length;
    int cols = grid[0].length;
    int[][] safetyScore = new int[rows][cols];

    // Рассчитываем "уровень безопасности" для каждой клетки:
    // - Чем дальше от игрока, тем выше безопасность.
    // - Наличие стен увеличивает безопасность.
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            int distance = Math.abs(i - playerPos.x) + Math.abs(j - playerPos.y);
            int wallBonus = (grid[i][j].getType() == BRICK_WALL) ? 50 : 0;
            safetyScore[i][j] = distance * 10 + wallBonus;
        }
    }

    // Ищем клетку с максимальным safetyScore
    Vector2i bestCover = start;
    int maxScore = -1;
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            if (safetyScore[i][j] > maxScore && isPathClear(grid, start, new Vector2i(i, j))) {
                maxScore = safetyScore[i][j];
                bestCover = new Vector2i(i, j);
            }
        }
    }
    if (bestCover.equals(start)) {
        return null;
    }

    // Возвращаем путь к укрытию
    return findShortestPath(grid, start, bestCover);
}

private static boolean isPathClear(Tile[][] grid, Vector2i from, Vector2i to) {
    int dxDir = Integer.compare(to.x, from.x);
    int dyDir = Integer.compare(to.y, from.y);
    Vector2i current = new Vector2i(from);
    int rows = grid.length;
    int cols = grid[0].length;

    while (!current.equals(to)) {
        current.add(dxDir, dyDir);
        // Проверка выхода за границы карты
        if (current.x < 0 || current.x >= rows || current.y < 0 || current.y >= cols) {
            return false;
        }
        // Проверка препятствий
        if (grid[current.x][current.y].getType() != UnitType.EMPTY) {
            return false;
        }
    }
    return true;
}
    public static ArrayList<movementDirection> findShortestPath(Tile grid[][], Vector2i start, Vector2i end) {
        try {
            int rows = grid.length;
            int cols = grid[0].length;

            // Массив расстояний (инициализируется -2)
            int[][] distance = new int[rows][cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    distance[i][j] = -2;
                }
            }

            // Очередь для обработки клеток
            PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(n -> n.priority));
            queue.add(new Node(start, 0, end));
            distance[start.x][start.y] = 0;

            
            while (!queue.isEmpty()) {
                Node current = queue.poll();
                Vector2i pos = current.pos;

                if (pos.equals(end)) break;

                for (int i = 0; i < 4; i++) {
                    int nr = pos.x + dr[i];
                    int nc = pos.y + dc[i];

                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
                        try {
                            int weight;
                            if (grid[nr][nc].getType() == BRICK_WALL) {
                                weight = 3;
                            } else if (grid[nr][nc].getType() == ARMOR_WALL) {
                                continue;
                            } 
                            else if (grid[nr][nc].getType() == WATER) {
                                continue;
                            }
                            else {
                                weight = 1;
                            }

                            int newCost = distance[pos.x][pos.y] + weight;

                            if (distance[nr][nc] == -2 || newCost < distance[nr][nc]) {
                                distance[nr][nc] = newCost;
                                queue.add(new Node(new Vector2i(nr, nc), newCost, end));
                            }
                        } catch (NullPointerException e) {
                            System.err.println("Null tile at position: (" + nr + ", " + nc + ")");
                            continue;
                        }
                    }
                }
            }

            // Восстановление пути (если он есть)
            if (distance[end.x][end.y] == -2) {
                return null;  // Путь не найден
            }
        //              for(int i = 0; i < rows; i++) {
        //     System.out.print("\n");
        //     for(int j = 0; j < cols; j++) {
        //         if (distance[i][j] <= 9 && distance[i][j] >= 0) {
        //             System.out.print("0" + distance[i][j] + "|");
        //         }
        //         else
        //         System.out.print(distance[i][j] + "|");

        //     }
        //  }
            List<int[]> path = new ArrayList<>();
            int r = end.x;
            int c = end.y;
            path.add(new int[]{r, c});

            while (r != start.x || c != start.y) {
                int minDistance = Integer.MAX_VALUE;
                int bestNr = -1;
                int bestNc = -1;
                
                // Проверяем все 4 направления
                for (int i = 0; i < 4; i++) {
                    int nr = r + dr[i];
                    int nc = c + dc[i];
                    
                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
                        // Ищем соседа с минимальной стоимостью
                        if (distance[nr][nc] != -2 && distance[nr][nc] < minDistance) {
                            minDistance = distance[nr][nc];
                            bestNr = nr;
                            bestNc = nc;
                        }
                    }
                }
                
                // Если не нашли подходящего направления
                if (bestNr == -1 || bestNc == -1) {
                    return null;
                }
                
                r = bestNr;
                c = bestNc;
                path.add(new int[]{r, c});
            }
            
            // Разворачиваем путь (чтобы был от старта к цели)
            java.util.Collections.reverse(path);
            ArrayList<movementDirection> dir = new ArrayList<>();
            Vector2i lastPos = new Vector2i();
            Vector2i offset = new Vector2i();
            lastPos.x = path.get(0)[0];
            lastPos.y = path.get(0)[1];

            for (int[] cell : path) {
                offset.x = cell[0] - lastPos.x;
                offset.y = cell[1] - lastPos.y;
                switch (offset.x) {
                    case -1 -> dir.add(movementDirection.LEFT);
                    case 1 -> dir.add(movementDirection.RIGHT);
                }
                switch (offset.y) {
                    case -1 -> dir.add(movementDirection.UP);
                    case 1 -> dir.add(movementDirection.DOWN);
                }
                lastPos.x = cell[0];
                lastPos.y = cell[1];
            }

            return dir;

        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Invalid grid coordinates: " + e.getMessage());
            return null;
        } catch (NullPointerException e) {
            System.err.println("Null reference encountered: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Unexpected error in pathfinding: " + e.getMessage());
            return null;
        }
    }
}