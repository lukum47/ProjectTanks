package com.Interfaces;

import org.joml.Vector2f;
import org.joml.Vector2i;

import com.lukum.Manager.Enums;
import com.lukum.Manager.Enums.HpType;
import com.lukum.Manager.Enums.UnitType;
import com.lukum.Manager.Enums.movementDirection;
import com.lukum.Render.Transform;
import com.lukum.gameProps.menu.HpBar;
import com.lukum.gameProps.units.animatedGameObject;
/**
 * Интерфейс управляемого объекта
 * много костылей!
 * изначально планировался для класса Player
 * потом не придумав ничего лучше для объединения логики, 
 * воткнул его во все подвижные объекты типа Bullet и Tank
 * поэтому в них много Depricated методовы
 */
public interface ControlableUnit {
   public UnitType getUnitType(); //Получить тип объекта для фабрики 
   public float getVelocity(); //получить скорость 
   public void setVelocity(float velocity);
   public Transform getTransform(); //получить экземпляр класса Transform отвечающего за передвижения
   public void setTargetPosition(Vector2f targetPos, Vector2i gridPos); //установить позицию в которую нужно переместиться 
   public Vector2f getTargetPosition();//получить ^ 
   public Vector2i getTargetGridPosition();//получить ^ 
   public void setMoving(boolean bool);//установить флаг движения
   public boolean isMoving();//получить ^
   default public boolean isRotating() { //получить флаг вращения объекта 
      return false;
   }; 
   public boolean isBrickWallAtFront();
   public void brickWallAtFront(boolean condition);
   public boolean isTankAtFront();
   public void tankAtFront(boolean condition);
   public boolean isPlayerAtFront();
   public void playerAtFront(boolean condition);
   public boolean isEndOfMapAtFront();
   public void endOfMapAtFront(boolean condition);
   public void restoreHealth(animatedGameObject hp, HpType type);
   public void updatePoints(float posX, float posY);
   public boolean isStuck();
   public void setStuck(boolean condition);
   public movementDirection getDirection(); //получить направление перемещения 
   public void setDirection(movementDirection dir); //установить направление 
   public Vector2i getCurrentGridPos();//получить текущее расположение в клетке 
   public void setCurrentGridPos(int gridX, int gridY);// установить ^
   public Vector2i getOldGridPos();//получить текущее расположение в клетке 
   public void setOldGridPos(int gridX, int gridY);// установить ^
}
