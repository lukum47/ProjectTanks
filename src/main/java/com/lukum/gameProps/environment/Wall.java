package com.lukum.gameProps.environment;

import org.joml.Vector2f;

import com.Interfaces.AnimatedObject;
import com.lukum.App;
import com.lukum.Manager.Enums.UnitType;
import com.lukum.Manager.Enums.WallParam;
import com.lukum.Render.Animation;
import com.lukum.Render.Atlas;
import com.lukum.Render.Render;
import com.lukum.Render.Texture2D;
import com.lukum.gameProps.units.gameObject;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL30.*;

public class Wall extends gameObject implements AnimatedObject{

    private int durability;
    private WallParam param;
    public Wall(String textureName, UnitType type, WallParam param, float posX, float posY, int tileSize) {
        super(posX, posY, tileSize);
        this.unitType = type;
        this.param = param;
        this.textureName = textureName;
        initStaticWall();
    }
    public Wall(String textureAtlasName, int textureCols, int textureRows, UnitType type, WallParam param, float posX, float posY, int tileSize) {
        super(posX, posY, tileSize);
        this.unitType = type;
        this.param = param;
        this.textureName = textureAtlasName;
        this.durability = 3;
        initAnimatebleWall(textureCols, textureRows);
    }
    public WallParam getWallParam() {
      return this.param;
    }
    private void initAnimatebleWall(int atlasCols, int atlasRows) {
      renderer = new Render(tileSize, textureName, atlasCols, atlasRows);
      renderer.initShader(this.getClass().getSimpleName());
      initAnimation(0.1f);
    }
    private void initStaticWall() {
          // Загрузка текстуры для стены

        renderer = new Render(tileSize, textureName);
        renderer.initShader(this.getClass().getSimpleName());
    }
    public void processHit() {
        if (this.param == WallParam.BREAKABLE) {
            durability--;
            if(durability == 0) {
               startDestroy = true;
            }
            else {
                this.animation.changeFrameManually();
            }
        }
    }
  
    public void setPosition(float posX, float posY)
    {
        this.transform.translate(posX, posY);
    }
    @Override
    public void destroy() {
        if(this.param == WallParam.BREAKABLE) {
        if (!isDestroy) {
            textureName = "BlockAnim.png";
            renderer.setTextureAtlas(textureName,9, 1);
            renderer.setSprite(0, 0);
            animation.updateAtlas();
            animation.resetAnimation();
            isDestroy = true;
        }

        if (animation.isFrameLast()) {
            animation.stopAnimation();
            delete();
        }
    } else return;
    } 
    @Override 
    public void delete() {
        renderer.delete();
        deleated = true;
    }
    @Deprecated
    protected void init() {
        
    }
    @Override
    public Animation getAnimation() {
        return this.animation;
    }


    @Override
    public void initAnimation(float frameTime) {
        this.animation = new Animation(this, frameTime);
        this.animation.stopAnimation(); //останавливаем анимацию для ручного перемещения по кадрам
    }
    // @Deprecated
    // public void updateGeometryAnim() {
    @Override
    public Render getRender() {
        return renderer;
    }
       
    // }
    // @Deprecated
    // public void processRotation() {
     
    // }
   
    
}