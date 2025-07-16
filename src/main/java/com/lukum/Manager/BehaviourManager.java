package com.lukum.Manager;

import static com.lukum.Manager.Enums.UnitType.BRICK_WALL;
import static com.lukum.Manager.Enums.UnitType.PLAYER;
import static com.lukum.Manager.Enums.UnitType.WATER;

import java.lang.management.PlatformLoggingMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.joml.Vector2f;
import org.joml.Vector2i;

import com.lukum.App;
import com.lukum.Algorithms.Pathfinding;
import com.lukum.Manager.Enums.behaviourState;
import com.lukum.Manager.Enums.movementDirection;
import com.lukum.gameProps.GameMap;
import com.lukum.gameProps.units.Player;
import com.lukum.gameProps.units.Tank;

/**
 * Класс управления поведением игровых объектов (танков).
 * Обеспечивает обработку движения, поиск пути, реакцию на игрока и окружение.
 * Реализован как Singleton для централизованного управления.
 */
public class BehaviourManager {
    private static BehaviourManager instance;
    private MovementSystem moveSystem;
    private GameMap map;
    Vector2i playerPosition = new Vector2i(-1, -1); // Позиция игрока на сетке (-1 - неопределено)
    private float behaviourTimer = 0;
    // Приватный конструктор (Singleton)
    private BehaviourManager(MovementSystem system, GameMap map) {
        moveSystem = system;
        this.map = map;
    }

    /** Инициализация единственного экземпляра класса */
    static public void initialize(MovementSystem system, GameMap map) {
        if (instance == null)
            instance = new BehaviourManager(system, map);
    }

    /** Получение экземпляра класса */
    public static BehaviourManager getInstance() {
        return instance;
    }

    /**
     * Основной метод обновления состояния танков.
     * @param tanks Список танков для обработки
     */
    public void updateTanks(ArrayList<Tank> tanks) {
        for (Tank it : tanks) {
            if (it instanceof Player) {
                // Обновляем позицию игрока
                playerPosition = it.getCurrentGridPos();
            } else {
                // Обработка вражеских танков
                if (!it.isDestroy() && playerPosition.x != -1 && playerPosition.y != -1 && it.isActive()) {
                    processBehaviour(it);
                }
            }
        }
    }
      /**
     * Метод обрабатывающий поведение танков
     */
    private void processBehaviour(Tank tank) {
        //При нанесении урона рандомно выбирается одно из двух поведений 
        if(tank.isHitted()) {
            int randNum = getRandomValue(3);
            if(randNum == 0) {
                tank.setCurrentBehaviour(behaviourState.ATTACK); //атакующее поведение 
            }
            else {
                tank.setCurrentBehaviour(behaviourState.FALLBACK); //отступающее
            }
            behaviourTimer = getRandTime(20, 30); // время, которое длится выбранное поведение
        }
        if(behaviourTimer > 0) behaviourTimer -= App.getDeltaTime();
        if(behaviourTimer <= 0) {
            behaviourTimer = 0;
            tank.setCurrentBehaviour(behaviourState.ORDINARY); // по истечению времени запускается стандартное поведение 
        }
        switch (tank.getCurrentBeheviour()) { 
            case ORDINARY -> {randomMovement(tank);}
            case FALLBACK -> {fallback(tank);}
            case ATTACK -> {attack(tank);}
        }
    } 
      /**
     * Метод реализующий отступление танков
     */
    private void fallback(Tank tank) {
        tank.setVelocity(85f);
        tank.setRotationVelocity(150f);
    
        // Если путь завершен или не найден, ищем новый
        if (tank.moveIsDone) {
            ArrayList<movementDirection> path = Pathfinding.findCoverPath(
                map.getTileSet(), 
                tank.getCurrentGridPos(), 
                playerPosition
            );
    
            // Проверка валидности пути
            if (path != null && !path.isEmpty()) {
                tank.setCurrentPath(path);
                tank.setDestination(calculatePathEnd(tank.getCurrentGridPos(), path));
                tank.setRemeiningMoves(path.size());
                tank.moveIsDone = false;
            } else {
                // Если укрытие не найдено, используем случайное направление
                movementDirection dir = getRandDirection();
                moveSystem.moveObject(dir, tank);
                tank.moveIsDone = true;
                return;
            }
        }
    
        // Выполнение текущего шага пути
        ArrayList<movementDirection> path = tank.getCurrentPath();
        int currentStep = path.size() - tank.getRemeiningMoves();
    
        if (currentStep >= 0 && currentStep < path.size()) {
            movementDirection nextDir = path.get(currentStep);
            moveSystem.moveObject(nextDir, tank);
    
            // Обновление состояния
            if (!tank.isMoving() && !tank.isRotating()) {
                tank.setRemeiningMoves(tank.getRemeiningMoves() - 1);
                if (tank.getRemeiningMoves() == 0) {
                    tank.moveIsDone = true;
                }
            }
        }
    }
    
    /** Вычисление конечной позиции пути */
    private Vector2i calculatePathEnd(Vector2i start, ArrayList<movementDirection> path) {
        Vector2i pos = new Vector2i(start);
        for (movementDirection dir : path) {
            pos.add(dir.getOffsetByDirection());
        }
        return pos;
    }
    /**
     * Движение танка с использованием пути, рассчитанного алгоритмом поиска A*.
     */
    private void attack(Tank tank) {
        tank.setVelocity(75f);
        tank.setRotationVelocity(150f);

        // Если игрок в зоне видимости - атака
            if (findPlayer(tank)) {
            map.addBullet(tank.fire(map));
        }

        // Расчет нового пути при завершении предыдущего
        if (tank.moveIsDone) {
            ArrayList<movementDirection> path = Pathfinding.findShortestPath(
                    map.getTileSet(), tank.getCurrentGridPos(), playerPosition);
            if (path != null) {
                tank.setCurrentPath(path);
                tank.setDestination(playerPosition);
                tank.setRemeiningMoves(path.size());
                tank.moveIsDone = false;
            }
        } else {
            // Пошаговое выполнение пути
            ArrayList<movementDirection> path = tank.getCurrentPath();
            moveSystem.moveObject(path.get(path.size() - tank.getRemeiningMoves()), tank);

            // Стрельба при столкновении со стеной
            if (tank.isBrickWallAtFront()) {
                map.addBullet(tank.fire(map));
            }
           
            // Обновление состояния движения
            if (!tank.isMoving() && !tank.isRotating()) {
                if (playerPosition != tank.getDestination()) {
                    tank.moveIsDone = true;
                    return;
                }
                tank.setRemeiningMoves(tank.getRemeiningMoves() - 1);
            }

            if (tank.getRemeiningMoves() == 0) {
                tank.moveIsDone = true;
            }
        }
    }

    /**
     * Базовое движение танка со случайными направлениями и реакцией на препятствия.
     */
    private void randomMovement(Tank tank) {
        if (tank.isRotating()) return;
        tank.setVelocity(35f);
        tank.setRotationVelocity(150f);
        movementDirection dir;
        // Проверка видимости игрока
        boolean isPlayerOnLine = findPlayer(tank);
        if (isPlayerOnLine) {
            map.addBullet(tank.fire(map));
        }
        // Смена направления по таймеру
        if (tank.getBehaviourTimer() <= 0 && !isPlayerOnLine) {
          
           if(tank.isWallDestroing() || tank.isPlayerAtFront()) dir = tank.getDirection(); // проверка на стену или игрока перед танком без сменны направления
           else dir = getRandDirection();
            tank.setBehaviourTimer(getRandTime(3f, 5f)); //при каждом последующем движении уменьшаем счетчик 
            moveSystem.moveObject(dir, tank);
        }
        else tank.setBehaviourTimer(tank.getBehaviourTimer() - App.getDeltaTime() * 100);
        // Обработка застревания
        if (tank.isStuck() && !isPlayerOnLine) {
            if (!tank.isEndOfMapAtFront()) {
                    // Действия при столкновении со стеной
               if(tank.isTankAtFront()) {
                 dir = tank.getDirection().getOppositeDirection(); // разворачиваем танк при столкновении с союзником 
                 moveSystem.moveObject(dir, tank);
                 tank.setStuck(false);
               }
               else if(tank.isPlayerAtFront()) {
                dir = tank.getDirection();
                moveSystem.moveObject(dir, tank);//чтобы обновлять информацию необходимо вызывать этот метод иначе двигаться танк больше не будет
                return;
               }
               else if(tank.isBrickWallAtFront()) { 
                    if (tank.isWallDestroing()) {
                        map.addBullet(tank.fire(map));
                        return;
                    }
                    // Случайный выбор: разрушение стены или обход
                    if (getRandomValue(6) == 1) {
                        tank.destroyWall(true);
                    } else {
                        dir = findClearPath(tank);
                        tank.setStuck(false);
                        moveSystem.moveObject(dir, tank);
                    }
                } else {
                    // Восстановление движения
                    if (tank.isWallDestroing()) {
                        tank.destroyWall(false);
                        dir = tank.getDirection();
                        tank.setStuck(false);
                        moveSystem.moveObject(dir, tank);
                    } else {
                        dir = findClearPath(tank);
                        tank.setStuck(false);
                        moveSystem.moveObject(dir, tank);
                    }
                }
            } else {
                // Случайное направление при выходе за границы
                dir = getRandDirection();
                tank.setStuck(false);
                moveSystem.moveObject(dir, tank);
            }
        } else {
            // Продолжение движения
            moveSystem.moveObject(tank.getTargetDirection(), tank);
        }
    }

    /**
     * Проверка прямой видимости игрока для танка.
     * @param tank Проверяемый танк
     * @return true если игрок виден и танк направлен в его сторону
     */
    private boolean findPlayer(Tank tank) {
        Vector2i tankPos = tank.getCurrentGridPos();
        movementDirection direction;
        if(tankPos.y == playerPosition.y) {
            if(tankPos.x < playerPosition.x ) {
                direction = movementDirection.RIGHT;
                for(int i = tankPos.x+1; i < playerPosition.x; ++i) {
                    if (!map.isCellEmpty(i, tankPos.y) && map.getTile(i, tankPos.y).getType() != WATER) {
                        return false;
                    }
                }
                if(tank.getDirection() != direction) {
                    moveSystem.rotateObject(direction, tank);
                    return false;
                    }
                    else
                    return true;
            }
            else if(tankPos.x > playerPosition.x) {
                direction = movementDirection.LEFT;
                for(int i = tankPos.x-1; i > playerPosition.x; --i) {
                    if (!map.isCellEmpty(i, tankPos.y) && map.getTile(i, tankPos.y).getType() != WATER)  {
                        return false;
                    }
                }
                if(tank.getDirection() != direction) {
                    moveSystem.rotateObject(direction, tank);
                    return false;
                    }
                    else
                    return true;
            }
        }
        if(tankPos.x == playerPosition.x) {
            if(tankPos.y < playerPosition.y) {
                direction = movementDirection.DOWN;
                for(int i = tankPos.y+1; i < playerPosition.y; ++i) {
                    if (!map.isCellEmpty(i, tankPos.y) && map.getTile(i, tankPos.y).getType() != WATER)  {
                        return false;
                    }
                }
                if(tank.getDirection() != direction) {
                    moveSystem.rotateObject(direction, tank);
                    return false;
                    }
                    else
                    return true;
            }
            else if(tankPos.y > playerPosition.y ) {
                direction = movementDirection.UP;
                for(int i = tankPos.y-1; i > playerPosition.y; --i) {
                    if (!map.isCellEmpty(i, tankPos.y) && map.getTile(i, tankPos.y).getType() != WATER)  {
                        return false;
                    }
                }
                if(tank.getDirection() != direction) {
                    moveSystem.rotateObject(direction, tank);
                    return false;
                    }
                    else
                    return true;
            }
        }
         return false;
      }
    /**
     * Поиск свободного направления для движения.
     * @param tank Танк, для которого ищется путь
     * @return Направление движения
     */
    private movementDirection findClearPath(Tank tank) {
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        Vector2i currentPos = tank.getCurrentGridPos();

        // Проверка соседних клеток
        for (int i = 0; i < 4; i++) {
            Vector2i inspectPos = new Vector2i(currentPos.x + dr[i], currentPos.y + dc[i]);
            if (inspectPos.x > 0 && inspectPos.x < map.getDx() && inspectPos.y > 0 && inspectPos.y < map.getDy()) {
                if (map.isCellEmpty(inspectPos.x, inspectPos.y) || map.getTile(inspectPos.x, inspectPos.y).getType() == BRICK_WALL) {
                    // Определение направления по смещению
                    if (inspectPos.x < currentPos.x) return movementDirection.LEFT;
                    else if (inspectPos.x > currentPos.x) return movementDirection.RIGHT;
                    else if (inspectPos.y < currentPos.y) return movementDirection.UP;
                    else if (inspectPos.y > currentPos.y) return movementDirection.DOWN;
                }
            }
        }
        return tank.getDirection().getOppositeDirection(); // Резервное направление
    }

    // --- Вспомогательные методы ---
    
    /** Генерация случайного направления */
    private movementDirection getRandDirection() {
        return switch (new Random().nextInt(4)) {
            case 0 -> movementDirection.UP;
            case 1 -> movementDirection.DOWN;
            case 2 -> movementDirection.LEFT;
            case 3 -> movementDirection.RIGHT;
            default -> movementDirection.UP; // fallback
        };
    }

    /** Случайное значение (0-max) */
    private int getRandomValue(int max) {
        return new Random().nextInt(max);
    }

    /** Генерация случайного времени (1.0f - maxTimeValue) */
    private float getRandTime(float minTimeValue, float maxTimeValue) {
        return new Random().nextFloat(minTimeValue, maxTimeValue);
    }
}