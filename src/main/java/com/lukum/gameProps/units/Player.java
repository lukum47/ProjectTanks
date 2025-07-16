package com.lukum.gameProps.units;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;

import org.joml.Vector2f;

import com.Interfaces.ControlableUnit;
import com.lukum.App;
import com.lukum.Render.Animation;
import com.lukum.Render.Atlas;
import com.lukum.Render.ShaderProgram;
import com.lukum.gameProps.GameMap;
import com.lukum.gameProps.menu.HpBar;
import com.lukum.Manager.Enums.HpType;
import com.lukum.Manager.Enums.ScreenAlignment;
import com.lukum.Manager.Enums.UnitType;
import com.lukum.Manager.Enums.movementDirection;

public class Player extends Tank 
{
    private static Player instance;
    private float velocity = 100;
    private HpBar hpBar;
    private Player(String textureAtlasName, int atlasCols, int atlasRows, float worldX, float worldY, int tileSize)
    {
        super(UnitType.PLAYER, textureAtlasName, atlasCols, atlasRows, worldX, worldY, tileSize);
        setRotationVelocity(400);
        hpBar = new HpBar(this, ScreenAlignment.RIGHT, 64, 64, "heart.png", 17, 2, 64, 0);
    }
    @Override
    protected void init() {
        this.transform.rotate(90);
        this.moveDirection = movementDirection.RIGHT;
        this.fireDirection = moveDirection;
        isActive = true;
    }
    public HpBar getHpBar() {
        return hpBar;
    }
    @Override
    public void initAnimation(float frameTime) {
      this.animation = new Animation(this, frameTime);
    }
    public static Player getInstance()
    {
        
        return instance;
    }
    public static void initialize (String textureAtlasName, int atlasCols, int atlasRows, float worldX, float worldY, int tileSize) {
        if(instance == null)
        {
            instance = new Player( textureAtlasName,  atlasCols, atlasRows, worldX, worldY, tileSize);
        }
    }
    public void updatePosition(float x, float y) {
        this.transform.translate(x, y);
    }
   
    @Override
    public void processRotation() {
        if(targetMoveDirection != null && targetMoveDirection != moveDirection) {
        boolean isRotationInProgress = false;
        switch (targetMoveDirection) {
            case UP -> {
                if(moveDirection != movementDirection.DOWN)
                    {
                    isRotationInProgress = this.transform.smoothRotate(0, rotationVelocity);
                    fireDirection = targetMoveDirection;
                    }
            }
            case DOWN -> {
                if(moveDirection != movementDirection.UP)
                    {
                    isRotationInProgress = this.transform.smoothRotate(180, rotationVelocity);
                    fireDirection = targetMoveDirection;
                    }
            }
            case LEFT -> {
                if(moveDirection != movementDirection.RIGHT)
                    {
                    isRotationInProgress = this.transform.smoothRotate(270, rotationVelocity);
                    fireDirection = targetMoveDirection;
                    }
            }
            case RIGHT -> {
                if(moveDirection != movementDirection.LEFT)
                    {
                    isRotationInProgress = this.transform.smoothRotate(90, rotationVelocity);
                    fireDirection = targetMoveDirection;
                    }
                }
            }
            
            // Если поворот завершен, обновляем направление
            if (!isRotationInProgress) {
                moveDirection = targetMoveDirection;
                isRotating = false;
            } else {
                isRotating = true;
            }
        }
        }
@Override
  public void restoreHealth(animatedGameObject hp, HpType type) {

    int needToRestore = health;
    switch (type) {
        case RED -> {
            if(health < 3) {
                health++;
            }
        }
        case SILVER -> {
            if(health == 2) {
                health++;
            }
            else if(health == 1) {
                health = 3;
            }
        }
    }
    needToRestore = health - needToRestore;
    hpBar.addHeart(needToRestore);
     GameMap map = GameMap.getInstance();
   animatedGameObject buff = (animatedGameObject)UnitType.BUFF.create(this.transform.getPosition().x, this.transform.getPosition().y, map.getTileSize());
    buff.addRelateObject(this);
   map.addEnvironment(buff);

   //hp.startDestroy();
   hp.delete();
   
  }
  @Override
  public void delete() {
      super.delete();
      instance = null;
  }
    @Override
    public float getVelocity()  
     {
        return velocity;
     }
}