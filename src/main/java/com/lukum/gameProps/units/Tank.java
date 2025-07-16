package com.lukum.gameProps.units;

import com.Interfaces.AnimatedObject;
import com.Interfaces.ControlableUnit;
import com.lukum.App;

import com.lukum.Manager.Enums.UnitType;
import com.lukum.Manager.Enums.WallParam;
import com.lukum.Manager.Enums.behaviourState;
import com.lukum.Manager.Enums.movementDirection;
import com.lukum.Render.Animation;
import com.lukum.Render.Atlas;
import com.lukum.Render.Render;
import com.lukum.Render.ShaderProgram;
import com.lukum.Render.Transform;
import com.lukum.gameProps.GameMap;
import com.lukum.Manager.Enums.HpType;

import static com.lukum.Manager.Enums.UnitType.SPARK;
import static com.lukum.Manager.Enums.UnitType.valueOf;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL30.*;

import java.util.*;

import org.joml.Vector2f;
import org.joml.Vector2i;

public class Tank extends gameObject implements ControlableUnit, AnimatedObject 
{   

    protected int health = 3;
    protected float velocity = 75f;
    protected Atlas atlas;
    protected int currentAtlasArrayIndx;
    private float behaviourTimer;
    protected float rotationVelocity;
    private boolean destroyWall;
    protected Vector2f leftBottom = new Vector2f();
    protected Vector2f RightTop = new Vector2f();
    protected boolean isStuck;
    protected movementDirection moveDirection;
    protected movementDirection targetMoveDirection;
    protected movementDirection fireDirection;
    protected int defaultCols = 8;
    protected int defaultRows = 4;
    protected UnitType unitType;
    protected ArrayList<movementDirection> currentPath;
    protected Vector2i destination = new Vector2i();
    protected int remainingMoves;
    protected float cooldown;
    public boolean moveIsDone = true;
    protected float fireLockTime = 2;
    protected float invincibleTime;
    protected float timer = 0;
    private boolean isBrickWallAtFront;
    private boolean isEndOfMapAtFront;
    private boolean isTankAtFront;
    private boolean isPlayerAtFront;
    protected boolean isCanonLocked = false;
    protected Animation animation;
    protected Vector2f targetPosition; //Позиция перемещения
    protected Vector2i gridTargetPos = new Vector2i();
    protected boolean isMoving = false;
    protected boolean isRotating;
    protected boolean isHitted;
    protected boolean isInvincible;
    private behaviourState currentBehaviour;
    // public Tank(UnitType unitType, float worldX, float worldY, int tileSize) 
    // {
    //     super(worldX, worldY, tileSize);
    //     updatePoints(transform.getPosition().x, transform.getPosition().y);
    //     this.unitType = unitType;
   
    // }
    public Tank(UnitType unitType, String textureAtlasName, int atlasCols, int atlasRows, float worldX, float worldY, int tileSize) {
        super(worldX, worldY, tileSize);
        this.unitType = unitType;
        renderer = new Render(tileSize, textureAtlasName, atlasCols, atlasRows);
        renderer.initShader(this.getClass().getSimpleName());
        initAnimation(0.4f);
        setCurrentBehaviour(behaviourState.ORDINARY);
    }
    @Override
    protected void init() 
    {
       this.transform.rotate(180.f);
        setRotationVelocity(250);
       isActive = true;
    }
    public boolean isInvincible() {
        return isInvincible;
    }
    public void setCurrentBehaviour(behaviourState behaviour) {
        currentBehaviour = behaviour;
    }
    public behaviourState getCurrentBeheviour() {
        return currentBehaviour;
    }
    public int getRemeiningMoves() {
        return remainingMoves;
    }
    public void setRemeiningMoves(int moveNum) {
        remainingMoves = moveNum;
    } 
    public ArrayList<movementDirection> getCurrentPath() {
        return currentPath;
    }
    public void setCurrentPath(ArrayList<movementDirection> currentPath) {
        this.currentPath = currentPath;
    }
    public Vector2i getDestination() {
        return destination;
    }
    public void setDestination(Vector2i destination) {
        this.destination = destination;
    }
    @Override 
    public void initAnimation(float frameTime) {
        this.animation = new Animation(this, frameTime);
    }
    public void setCooldown(float val) {
        cooldown = val;
    }
    public void decreaseCooldown() {
        cooldown -= App.getDeltaTime();
        if(cooldown <= 0) cooldown = 0;
    }
    public void setRotationVelocity(float velocity) {
        this.rotationVelocity = velocity;
    }
    
    public float getRotationVelocity() {
        return this.rotationVelocity;
    }
    public float getCooldown() {
        return cooldown;
    }
    public Atlas getTextureAtlas(int index) 
    {
         return this.atlas;
    }
    public Bullet fire(GameMap map) {
        if(!isCanonLocked && !isRotating) { //пропускаем если заблокирована пушка или происходит вращение 
        Vector2i gridPos = this.getCurrentGridPos();
        switch (fireDirection) {
            case UP -> {gridPos.y--;}
            case DOWN -> {gridPos.y++;}
            case LEFT -> {gridPos.x--;}
            case RIGHT -> {gridPos.x++;}
        }
        
        Vector2f worldPos = map.gridToWorldCentered(gridPos.x, gridPos.y);
        animatedGameObject spark = (animatedGameObject)SPARK.create(worldPos.x, worldPos.y, tileSize);
        spark.addRelateObject(this);
        map.addEnvironment(spark);
        isCanonLocked = true;
        return new Bullet(map, unitType, fireDirection, worldPos.x, worldPos.y, tileSize);
    }
        return null;
    }
    public void setStuck(boolean condition) {
        isStuck = condition;
    }
    public boolean isStuck() {
        return isStuck;
    }
    @Override
    public void setDirection(movementDirection dir) {
        if(!isRotating) isRotating = true;
              
              targetMoveDirection = dir;
        }
   public boolean isDestroy() {
    return isDestroy;
   }
        public void processRotation() {
            if(targetMoveDirection != null && targetMoveDirection != moveDirection && !isDestroy) {
            boolean isRotationInProgress = false;
            switch (targetMoveDirection) {
                case UP -> {
                   
                        isRotationInProgress = this.transform.smoothRotate(0, rotationVelocity);
                        fireDirection = targetMoveDirection;
                        
                }
                case DOWN -> {
            
                        isRotationInProgress = this.transform.smoothRotate(180, rotationVelocity);
                        fireDirection = targetMoveDirection;
                        
                }
                case LEFT -> {
                   
                        isRotationInProgress = this.transform.smoothRotate(270, rotationVelocity);
                        fireDirection = targetMoveDirection;
                        
                }
                case RIGHT -> {
                   
                        isRotationInProgress = this.transform.smoothRotate(90, rotationVelocity);
                        fireDirection = targetMoveDirection;
                        
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
        public void setBehaviourTimer(float timer) {
            this.behaviourTimer = timer;
        }
        public float getBehaviourTimer() {
            return this.behaviourTimer;
        }
  //  }
    public movementDirection getDirection() {
        return moveDirection;
    }
    public movementDirection getFireDirection() {
        return fireDirection;
    }
    public movementDirection getTargetDirection() {
        return this.targetMoveDirection;
    }
    protected void updateCanonLock(float deltaTime) {
        if(isCanonLocked) {
            timer += deltaTime;
            if(timer >= fireLockTime) {
                timer = 0;
                isCanonLocked = false;
            }
        }
    }
    public int getHealth() {
        return health;
    }
    public void setHealth(int healthPoints) {
        health = healthPoints;
    }
    public void destroyWall(boolean condition) {
        destroyWall = condition;
    }
    public boolean isWallDestroing() {
        return destroyWall;
    }
    public void processHit() {
        // if(isInvincible) {System.out.println("invincible");return;}
            health--;
            if(health == 0) {
               startDestroy = true;
            }
            else {
               isHitted = true;
            }
            
    }
    public boolean isHitted() {
        return isHitted;
    }
    @Override
        public void destroy() {
        if (!isDestroy) {
            textureName = "EXPLOSION.png";
            renderer.setTextureAtlas(textureName, 11, 2);
            animation.updateAtlas();
            this.transform.scale(0.15f, 0.15f, tileSize);
            animation.resetAnimation();
            animation.setFrameTime(0.03f);
            isDestroy = true;
            isMoving = false;
            setCurrentGridPos(-1, -1);
        }

        if (animation.isFrameLast()) {
            animation.stopAnimation();
            delete();
        }
    } 
      @Override 
    public void delete() {
     renderer.delete();
        deleated = true;
    }
    // protected void processAnimation() {
    //     if(isMoving)
    //     this.animation.update(App.getDeltaTime());
    // }
   
    protected void updateInvincibility(float deltaTime) {
        if (isHitted() && !startDestroy) {
            isInvincible = true;
            invincibleTime = 5;
            isHitted = false;
        }
    
        if (isInvincible) {
            invincibleTime -= deltaTime;
            if (invincibleTime <= 0) {
                invincibleTime = 0;
                isInvincible = false;
            }
            ShaderProgram program = renderer.getShader();
            program.use();
            program.setBool("u_invincible", isInvincible);
            program.setFloat("u_time", invincibleTime);
        }
    }
    @Override
    public void draw(float deltaTime) {
       updateInvincibility(deltaTime);
       updateCanonLock(deltaTime); 
       processRotation();
        super.draw(deltaTime);
    }
    @Override
    public float getVelocity() {
        return velocity;
    }
    @Override 
    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }
     @Override
    public UnitType getUnitType() 
    {
        return this.unitType;
    }
        
    @Override
    public Transform getTransform() {
        return this.transform;
    }

    @Override
    public Animation getAnimation() {
     
        return this.animation;
    }
    @Override
    public boolean isMoving() {
        return isMoving;
    }

    @Override
    public void setMoving(boolean moving) {
        isMoving = moving;
    }

    @Override
    public void setTargetPosition(Vector2f targetPos, Vector2i gridTargetPos) {
            this.targetPosition = targetPos;
            this.gridTargetPos = gridTargetPos;
            isMoving = true;
   }
   @Override
    public Vector2f getTargetPosition() {
           return this.targetPosition;
        }
   @Override
   public boolean isRotating() {
    return this.isRotating;
   }
   @Override
   public void updatePoints(float posX, float posY) {
    leftBottom.x = posX;
    leftBottom.y = posY + tileSize;
    RightTop.x = posX+tileSize;
    RightTop.y = posY; 
   }
  public Vector2f getLeftBottom() {
    return this.leftBottom;
  }
  public Vector2f getRightTop() {
    return this.RightTop;
  }
  @Override
  public Vector2i getTargetGridPosition() {
   return gridTargetPos;
  }
  @Override
  public Render getRender() {
 return renderer;
  }
  @Override
  public boolean isBrickWallAtFront() {
    return isBrickWallAtFront;
  }
  @Override
  public void brickWallAtFront(boolean condition) {
    isBrickWallAtFront = condition;
  }
  @Override
  public boolean isEndOfMapAtFront() {
    return isEndOfMapAtFront;
  }
  @Override
  public void endOfMapAtFront(boolean condition) {
    isEndOfMapAtFront = condition;
  }
  @Override
  public boolean isTankAtFront() {
   return isTankAtFront;
  }
  @Override
  public void tankAtFront(boolean condition) {
    isTankAtFront = condition;
  }
  @Override
  public boolean isPlayerAtFront() {
    return isPlayerAtFront;
  }
  @Override
  public void playerAtFront(boolean condition) {
    isPlayerAtFront = condition;
  }
  @Override
  public void restoreHealth(animatedGameObject hp, HpType type) {
    if(hp.isDeleted()) return;
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
    GameMap map = GameMap.getInstance();
    Vector2f coords = map.gridToWorldCentered(this.getCurrentGridPos().x, this.getCurrentGridPos().y);
    animatedGameObject buff = (animatedGameObject)UnitType.BUFF.create(coords.x, coords.y, map.getTileSize());
    buff.addRelateObject(this);
    map.addEnvironment(buff);
    hp.delete();
  }


}