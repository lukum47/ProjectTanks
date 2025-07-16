package com.lukum.Manager;

import static com.lukum.Manager.Enums.UnitType.PLAYER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;

import org.joml.Vector2f;
import org.joml.Vector2i;

import com.Interfaces.ControlableUnit;
import com.lukum.App;
import com.lukum.Manager.Enums.movementDirection;
import com.lukum.gameProps.GameMap;
import com.lukum.gameProps.units.Bullet;
import com.lukum.gameProps.units.Player;
import com.lukum.gameProps.units.gameObject;

public class MovementSystem {
    private final MoveController movementController;
    private final GameMap map;
    public MovementSystem(MoveController movementController, GameMap map) {
        this.movementController = movementController;
        this.map = map;
    }

    /**
     * Обрабатывает ввод и перемещает игрока.
     */
    public void input(Player unit, int key[], float deltaTime) {
     {
         int deltaX = 0, deltaY = 0;
        movementDirection direction = null;
        // if(isTime) timeLock();
         if(!unit.isMoving())
        if (key[GLFW_KEY_W] == GLFW_PRESS || key[GLFW_KEY_W] == GLFW_REPEAT) 
            { 
               // unit.setMoving(true);
               direction = movementDirection.UP;
               //unit.setDirection(direction);
                deltaY = -1;
            }
        else if (key[GLFW_KEY_S] == GLFW_PRESS || key[GLFW_KEY_S] == GLFW_REPEAT) 
            {
               // unit.setMoving(true);
               direction = movementDirection.DOWN;
              // unit.setDirection(direction);
                //unit.getAnimation().update(deltaTime);
                deltaY = 1;
            }   
        else if (key[GLFW_KEY_A] == GLFW_PRESS || key[GLFW_KEY_A] == GLFW_REPEAT) 
            {
              //  unit.setMoving(true);   
              direction = movementDirection.LEFT;
              //unit.setDirection(direction);
                //unit.getAnimation().update(deltaTime);
                deltaX = -1;
            }
        else if (key[GLFW_KEY_D] == GLFW_PRESS || key[GLFW_KEY_D] == GLFW_REPEAT) 
            {
              //  unit.setMoving(true);
              direction = movementDirection.RIGHT;
           //   unit.setDirection(direction);
             //   unit.getAnimation().update(deltaTime);
                deltaX = 1;
            }
        if (deltaX != 0 || deltaY != 0) {
            movementController.move(unit, deltaX, deltaY, direction);
        }
        
        if(key[GLFW_KEY_SPACE] == GLFW_PRESS) {
            map.addBullet(unit.fire(map));
        }
    }
    updateMovement(unit, deltaTime);
}
public void moveObject(movementDirection direction, ControlableUnit unit) {
    int deltaX = 0, deltaY = 0;
   // if(!unit.isMoving()) {
   
    switch (direction) {
        case UP -> {
            deltaY = -1;
        }
        case DOWN -> {
            deltaY = 1;
        }
        case LEFT -> {
            deltaX = -1;
        }
        case RIGHT -> {
            deltaX = 1;
        }
    }
     if (deltaX != 0 || deltaY != 0 ) {
            movementController.move(unit, deltaX, deltaY, direction);
        }
   // }
    updateMovement(unit, App.getDeltaTime());
}
public void rotateObject(movementDirection direction, ControlableUnit unit) {
     if(unit.isMoving()) return;
     movementController.rotate(unit, direction);
 }

private void updateMovement(ControlableUnit unit, float deltaTime) {
    if (unit.isMoving()) {
        //unit.getAnimation().update(deltaTime);
        Vector2f currentPos = new Vector2f(unit.getTransform().getPosition().x, unit.getTransform().getPosition().y);
        Vector2f targetPos = unit.getTargetPosition();
    
        // Рассчитываем направление и расстояние
        float dx = targetPos.x - currentPos.x;
        float dy = targetPos.y - currentPos.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
    
        if (distance > 0) {
            // Фиксированная скорость (пикселей в секунду)
            float speed = unit.getVelocity(); 
            float step = speed * deltaTime;
    
            // Новая позиция
            float ratio = step / distance;
            float newX = currentPos.x + dx * ratio;
            float newY = currentPos.y + dy * ratio;
    
            unit.getTransform().translate(newX, newY);
            unit.updatePoints(newX, newY);
            // Проверка завершения
            if (Math.abs(targetPos.x - newX) < 1.0f && Math.abs(targetPos.y - newY) < 1.0f) {
                unit.getTransform().translate(targetPos.x, targetPos.y); // Точное выравнивание
                unit.setMoving(false);
                map.clearOldPosition(unit); //после завершения движения очищаем старую клетку
            }
        }
    }
}
}