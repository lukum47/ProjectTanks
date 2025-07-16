package com.lukum.gameProps.units;


import org.joml.Vector2f;
import org.joml.Vector2i;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL30.*;

import java.util.Random;

import com.Interfaces.AnimatedObject;
import com.Interfaces.ControlableUnit;
import com.lukum.App;
import com.lukum.Manager.MoveController;
import com.lukum.Manager.Enums.UnitType;
import com.lukum.Manager.Enums.movementDirection;
import com.lukum.Render.Animation;
import com.lukum.Render.Atlas;
import com.lukum.Render.Render;
import com.lukum.gameProps.GameMap;

public class Bullet extends gameObject implements AnimatedObject{

    private Vector2f pointUp; //Объявление 4х точек по периметру пули
    private Vector2f pointBottom;
    private Vector2f pointLeft;
    private Vector2f pointRight;
    private final float pointOffset = tileSize;
    private int bounceIncreaser = 15;
    private boolean speedIncreased = false;
    private UnitType owner;
    private int spreadDir;
    private boolean isMoving;
    private movementDirection direction;
    public Vector2i lastCollisionGrid = new Vector2i(-1, -1);
    public boolean canBounce = true;
    public Bullet(GameMap map, UnitType owner, movementDirection direction, float worldX, float worldY, int tileSize) {
        super(worldX, worldY, tileSize);
        this.tileSize = tileSize/2;
        this.direction = direction;
        this.owner = owner;
        this.velocity = 1.f;
        updatePoints(worldX, worldY); // Инициализация точек через метод
        transform.scale(0.05f, 0.05f, tileSize);
        rotateByDirection();
        randomizeSpread();
        setMoving(true);
    }
    @Override
    protected void init() {
        textureName = "bullet.png";
        renderer = new Render(tileSize, textureName, 10, 2);    
        renderer.initShader(this.getClass().getSimpleName());
        initAnimation(0.1f);
    }
    public float getSpread() {
        float speed = this.velocity * bounceIncreaser * spreadDir; 
        float step = speed * App.getDeltaTime();

        return step;
    }
    public int getCurrentSpreadDir() {
        return this.spreadDir;
    }
    public void changeSpreadDir() {

        this.spreadDir *= (-1);
        if (!speedIncreased) {
            this.bounceIncreaser *= 5;
            speedIncreased = true;
        }
        // Принудительное обновление точек коллизии
        updatePoints(
            this.transform.getPosition().x,
            this.transform.getPosition().y
        );
    }
  
    public void resetBounce() {
        lastCollisionGrid.set(-1, -1);
        canBounce = true;
    }
    public UnitType getOwnerType() {
        return owner;
    }
    private void randomizeSpread() {
        Random rand = new Random();
        this.spreadDir = rand.nextInt(3) - 1; // {-1, 0, 1}
        
    }
    public boolean isDestroy() {
        return isDestroy;
    }
    // @Override
    public void updatePoints(float posX, float posY) {

          //все смещения относительно левого верхнего края тайла 
          switch (direction) {
            case UP, DOWN-> {
                this.pointUp = new Vector2f(posX + pointOffset/2, posY);
                this.pointBottom = new Vector2f(posX + pointOffset/2, posY + pointOffset);
                this.pointLeft = new Vector2f(posX + 9, posY + pointOffset);
                this.pointRight = new Vector2f(posX + 22, posY + pointOffset);
                        }
 
            case LEFT, RIGHT -> {
                this.pointUp = new Vector2f(posX + pointOffset/2, posY + 9);
                this.pointBottom = new Vector2f(posX + pointOffset/2, posY + 22);
                this.pointLeft = new Vector2f(posX, posY + pointOffset/2);
                this.pointRight = new Vector2f(posX + pointOffset, posY + pointOffset/2);}
            
        }

    }

    public int getTileSize() {
        return tileSize;
    }
//    @Override
    public movementDirection getDirection() {
        return this.direction;
    }
    public Vector2f getPoint(int index) {
        switch (index) {
            case 0: return this.pointUp;
            case 1: return this.pointBottom;
            case 2: return this.pointLeft;
            case 3: return this.pointRight;
            default: return new Vector2f(this.transform.getPosition().x, this.transform.getPosition().y);
        }
    }
    public void rotateByDirection() {
        switch (direction) {
            case UP -> {transform.rotate(90);}
            case DOWN -> {transform.rotate(270);}
            case LEFT -> {transform.rotate(0);}
            case RIGHT -> {transform.rotate(180);}
        }
    }
    @Override
    public void destroy() {
        if (!isDestroy) {
            textureName = "hit.png";
            renderer.setTextureAtlas(textureName,  6, 1);
            renderer.setSprite(0, 0);
            spreadDir = 0;
            isDestroy = true;
            animation.updateAtlas();
            animation.resetAnimation(); // Сброс и перезапуск анимации
        }
         if (animation.isFrameLast()) {
            animation.stopAnimation();
            delete();
        }
    }
  
    public void translate(float posX, float posY) {
        this.transform.smoothTranslate(posX, posY);
      
        updatePoints(this.transform.getPosition().x, this.transform.getPosition().y); // Обновляем точки после перемещения
    }
    public float getDeltaSpeed() {
        float speed = this.velocity * 200; 
        float step = speed * App.getDeltaTime();
        return step;
    }
    public void setDeltaSpeed(float val) {
        this.velocity = val;
    }

    @Override
    public Animation getAnimation() {
        return this.animation;
    }

   // @Override
    public boolean isMoving() {
        return this.isMoving;
    }
   // @Override
    public void setMoving(boolean moving) {
        this.isActive = moving;
    }

    @Override
    public void initAnimation(float frameTime) {
        this.animation = new Animation(this, frameTime);
    }
  //  @Override
    public float getVelocity() {
        return this.velocity;
    }
//     @Override
//     public void setTargetPosition(Vector2f targetPos, Vector2i gridTargetPos) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'setTargetPosition'");
//     }
//     public Vector2i getTargetGridPosition() {
//    // TODO Auto-generated method stub
//    throw new UnsupportedOperationException("Unimplemented method 'setTargetPosition'");
//        }
//     @Override
//     public Vector2f getTargetPosition() {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'getTargetPosition'");
//     }
//     @Override
//     public void setDirection(movementDirection dir) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'setDirection'");
//     }
//     @Override
//     public boolean isRotating() {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'isRotating'");
//     }
//     @Override
//     public boolean isStuck() {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'isStuck'");
//     }
//     @Override
//     public void setStuck(boolean condition) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'setStuck'");
//     }
    @Override
    public Render getRender() {
        return renderer;
    }

}