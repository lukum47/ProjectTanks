package com.lukum.Manager;

import static com.lukum.Manager.Enums.UnitType.BRICK_WALL;
import static com.lukum.Manager.Enums.UnitType.EMPTY;
import static com.lukum.Manager.Enums.UnitType.ENEMY;
import static com.lukum.Manager.Enums.UnitType.HP;
import static com.lukum.Manager.Enums.UnitType.PLAYER;
import static com.lukum.Manager.Enums.UnitType.WATER;

import java.util.ArrayList;
import java.util.Iterator;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import com.Interfaces.AnimatedObject;
import com.Interfaces.ControlableUnit;
import com.lukum.Manager.Enums.UnitType;
import com.lukum.Manager.Enums.WallParam;
import com.lukum.Manager.Enums.movementDirection;
import com.lukum.gameProps.GameMap;
import com.lukum.gameProps.Tile;
import com.lukum.gameProps.game;
import com.lukum.gameProps.environment.HP;
import com.lukum.gameProps.environment.Wall;
import com.lukum.gameProps.units.Bullet;
import com.lukum.gameProps.units.Player;
import com.lukum.gameProps.units.Tank;
import com.lukum.gameProps.units.animatedGameObject;
import com.lukum.gameProps.units.gameObject;

/*
 * Контроллер перемещений, проверяющий коллизии и обновляющий позиции на карте.
 */
public class MoveController {
    private final GameMap map;
  //  private boolean shown;
    public MoveController(GameMap map) {
        this.map = map;
    }
    /**
     * @param obj    Объект для перемещения.
     * @param deltaX Смещение по X (в тайлах).
     * @param deltaY Смещение по Y (в тайлах).
     * @return true, если перемещение успешно.
     */
    public void moveBullet(Bullet bullet) {
        Vector2i currentGrid = map.worldToGridCentered(
            bullet.getTransform().getPosition().x,
            bullet.getTransform().getPosition().y
        );
    
        // Если пуля покинула клетку последней коллизии — сбрасываем флаг
        if (!currentGrid.equals(bullet.lastCollisionGrid)) {
            bullet.resetBounce();
        }
    
        movementDirection direction = bullet.getDirection();
        switch (direction) {
            case UP -> {bullet.translate(bullet.getSpread(), -bullet.getDeltaSpeed());}
            case DOWN -> {bullet.translate(bullet.getSpread(), bullet.getDeltaSpeed());}
            case LEFT -> {bullet.translate(-bullet.getDeltaSpeed(), bullet.getSpread());}
            case RIGHT -> {bullet.translate(bullet.getDeltaSpeed(), bullet.getSpread());}
        }
        boolean collision = false;
        Vector2i gridCoord = new Vector2i();
    // Проверка коллизий
    for (int i = 0; i < 4; i++) {
        Vector2f point = bullet.getPoint(i);

        gridCoord = map.worldToGridCentered(point.x, point.y);
        if (gridCoord.x < 0 || gridCoord.x >= map.getDx() || gridCoord.y < 0 || gridCoord.y >= map.getDy()) {
            bullet.setDeltaSpeed(0);
            bullet.destroy();
            return;
        }
        if (!map.isCellEmpty(gridCoord.x, gridCoord.y) && map.getTile(gridCoord.x, gridCoord.y).getType() != bullet.getOwnerType() && map.getTile(gridCoord.x, gridCoord.y).getType() != HP && map.getTile(gridCoord.x, gridCoord.y).getType() != WATER) {
            if (processBounce(bullet, i, gridCoord)) {
                // Если отскок обработан, прерываем проверку
            
                return;
            } else {
                collision = true;
                break;
            }
        }
    }
    

     if (collision) {
        if(map.getTile(gridCoord.x, gridCoord.y).getObject() instanceof Wall && !bullet.isDestroy()) {
        Wall wall = (Wall)(map.getTile(gridCoord.x, gridCoord.y).getObject());
        wall.processHit();
    }
    if(map.getTile(gridCoord.x, gridCoord.y).getType() == WATER && !bullet.isDestroy()) {
      return;
    }
    if(map.getTile(gridCoord.x, gridCoord.y).getObject() instanceof Tank && !bullet.isDestroy()) {
        Tank tank = (Tank)(map.getTile(gridCoord.x, gridCoord.y).getObject());
        if(!processGeometryCollision(tank, bullet)) return;
        if(tank.isInvincible()) return;
        tank.processHit();
    }
    Vector3f lastValidPos = bullet.getTransform().getPosition();
    bullet.getTransform().translate(lastValidPos.x, lastValidPos.y);
    bullet.setDeltaSpeed(0);
    bullet.destroy();
    
    }
 }
   private boolean processGeometryCollision(Tank tank, Bullet bullet) {
    for (int i = 0; i < 4; i++) {
        Vector2f bPoint = bullet.getPoint(i);
        Vector2f leftBottom = tank.getLeftBottom();
        Vector2f RightTop = tank.getRightTop();
        if(bPoint.x >= leftBottom.x && bPoint.y <= leftBottom.y) {
            if(bPoint.x <= RightTop.x && bPoint.y >= RightTop.y) {
                return true;
            }
        }
    }
    return false;
   }
   private boolean processBounce(Bullet bullet, int pointIndx, Vector2i currentGrid) {
    
    if (bullet.lastCollisionGrid.equals(currentGrid)) {
        return false;
    }
    movementDirection direction = bullet.getDirection();
    boolean bounced = false;
    Vector3f pos = bullet.getTransform().getPosition();

    switch (direction) {
        case UP, DOWN -> {
            if (pointIndx == 2 && bullet.getCurrentSpreadDir() == (-1) || pointIndx == 3 && bullet.getCurrentSpreadDir() == (1)) {
                bullet.changeSpreadDir();
                bounced = true;
            }
        }
        case LEFT, RIGHT -> {
            if (bullet.getCurrentSpreadDir() == (-1) && pointIndx == 0 || bullet.getCurrentSpreadDir() == (1) && pointIndx == 1) {
                bullet.changeSpreadDir();
                bounced = true;
            }
        }
    }

    if (bounced) {
        bullet.lastCollisionGrid.set(currentGrid);
        bullet.canBounce = false;
        bullet.getTransform().translate(pos.x, pos.y);
        bullet.updatePoints(pos.x, pos.y); // Обновляем точки коллизии
    }
    return bounced;
}
   public void move(ControlableUnit unit, int deltaX, int deltaY, movementDirection direction) {
        if(unit.isRotating() || unit.isMoving()) return ;
        Vector2i currentGrid = unit.getCurrentGridPos();
        movementDirection currentDir = unit.getDirection();
        if (currentDir != direction) {
            unit.setDirection(direction);
            return;
    }
        int newX = currentGrid.x + deltaX;
        int newY = currentGrid.y + deltaY;
    //    unit.brickWallAtFront(false);
        unit.endOfMapAtFront(false);
        unit.brickWallAtFront(false);
        unit.playerAtFront(false);
        unit.tankAtFront(false);
        unit.setStuck(false);
        // Проверка на выход за границы карты
        if (newX < 0 || newX >= map.getDx() || newY < 0 || newY >= map.getDy()) {
            unit.endOfMapAtFront(true);
            unit.setStuck(true);
            return ;
        }
        
         if (!map.isCellEmpty(newX, newY)) {
            if(map.getTile(newX, newY).getType() == UnitType.HP) {
                HP obj = (HP)map.getTile(newX, newY).getObject();
                unit.restoreHealth(obj, obj.getHPType());
             }
             else{
            UnitType type = map.getTile(newX, newY).getType();
            if(type == BRICK_WALL) unit.brickWallAtFront(true);
            else if(type == PLAYER) unit.playerAtFront(true);
            else if(type == ENEMY) unit.tankAtFront(true);
            unit.setStuck(true); 
            return;
             }
        } 
      
            Vector2f newWorldPos = map.gridToWorldCentered(newX, newY);
             unit.setTargetPosition(newWorldPos, new Vector2i(newX, newY));
             map.updateObjectPosition(unit, newX, newY); // обновляем позицию танка 
            unit.setMoving(true);
       
        
    
    }
    public void rotate(ControlableUnit unit, movementDirection direction) { 
   
        if (unit.getDirection() != direction) {
            unit.setDirection(direction);
            return;
    }
    }
}