package com.lukum.gameProps.units;

import static com.lukum.Manager.Enums.UnitType.*;

import org.joml.Vector2f;
import org.joml.Vector3f;

import com.Interfaces.AnimatedObject;
import com.lukum.Manager.Enums.UnitType;
import com.lukum.Render.Animation;
import com.lukum.Render.Atlas;
import com.lukum.Render.Render;
import com.lukum.gameProps.GameMap;

public class animatedGameObject extends gameObject implements AnimatedObject{
    protected int atlasRows, atlasCols;
    protected boolean terminatable;
    protected boolean isPhysical;
    protected boolean startDestroy;
    protected Render destroyRender;
    protected boolean dontDelete;
    protected Render ordinaryRender;
    protected boolean folowTheRelated;
    protected GameMap map;
    protected gameObject relatedObject;
    String atlasName;
    public animatedGameObject(UnitType type, float worldX, float worldY, int tileSize, String atlasName, int cols, int rows, boolean isTerminatable, boolean ceepAlive, float frameTime) {
            super(worldX, worldY, tileSize);
            renderer = new Render(tileSize, atlasName, cols, rows);
            renderer.initShader("Simple");
            terminatable = isTerminatable;
            unitType = type;
            initAnimation(frameTime);
            animation.stopAtLastFrame(ceepAlive);
        }
       
        public animatedGameObject(Render render, UnitType type, float worldX, float worldY, int tileSize, boolean isTerminatable, boolean ceepAlive, float frameTime) {
            super(worldX, worldY, tileSize);
            renderer = render;
            terminatable = isTerminatable;
            unitType = type;
            initAnimation(frameTime);
            animation.stopAtLastFrame(ceepAlive);
            dontDelete = true;
        }
    public void setDestroyRender(int tileSize, String atlasName, int cols, int rows) {
        destroyRender = new Render(tileSize, atlasName, cols, rows);
        destroyRender.initShader("Simple");
    }
    public void setDestroyRender(Render render) {
        destroyRender = render;
    }
    public void setOrdinaryRender(int tileSize, String atlasName, int cols, int rows) {
        renderer = new Render(tileSize, atlasName, cols, rows);
        renderer.initShader("Simple");
        animation.resetAnimation();
    }
    public boolean isTerminatable() {
        return terminatable;
    }
        @Override
    public Animation getAnimation() {
        return animation;
    }
    public void startDestroy() {
        if(startDestroy) return;
        startDestroy = true;
        if(destroyRender != null) {
            renderer = destroyRender;
           animation.updateRender(renderer);
        }
    }
    public boolean isDestroing() {
        return startDestroy;
    }
    @Override
    public Render getRender() {
        return renderer;
    }
    public void setPhysic(boolean condition) {
        isPhysical = condition;
    }
    public boolean isPhysical() {
        return isPhysical;
    }
    @Override
    public void initAnimation(float frameTime) {
     animation = new Animation(this, frameTime);
     animation.startAnimation();
    }
   
    public void addRelateObject(gameObject object) {
        relatedObject = object;
        processTypeTransforms();
    }
    private void processTypeTransforms() {
        if(unitType == SPARK && relatedObject instanceof Tank) {
            transform.scale(0.08f, 0.08f, tileSize);

            switch (((Tank)relatedObject).getFireDirection()) {
               case UP -> {transform.rotate(270); transform.smoothTranslate(0, -10);}
               case DOWN -> {transform.rotate(90); transform.smoothTranslate(0, 10);}
               case LEFT -> {transform.rotate(180); transform.smoothTranslate(-10, 0);}
               case RIGHT -> {transform.rotate(0); transform.smoothTranslate(10, 0);}
            }
        
        }
        if(unitType == BUFF && relatedObject instanceof Tank) {
             folowTheRelated = true;
             map = GameMap.getInstance();
             transform.scale(0.14f, 0.14f, tileSize);
        }
    }
    @Override
    public void draw(float deltaTime) {
        if(folowTheRelated) {
            Vector3f coords = relatedObject.getTransform().getPosition();
            transform.translate(coords.x, coords.y);
        }
        super.draw(deltaTime);
    }
    @Override
    public void destroy() {
       
     if (animation.isFrameLast()) {
        if(relatedObject != null) {
            if(!relatedObject.isActive) {
                relatedObject.activate();
            }
        }
            animation.stopAnimation();
            delete();
        }
    }
    @Override
    public void delete() {
        if(!dontDelete) renderer.delete();
        deleated = true;
    }
}
