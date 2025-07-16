package com.lukum.gameProps.menu;

import org.joml.Vector2i;

import com.Interfaces.AnimatedObject;
import com.lukum.App;
import com.lukum.Manager.Enums.ScreenAlignment;
import com.lukum.Render.Animation;
import com.lukum.Render.Render;
import com.lukum.Render.Transform;
import com.lukum.gameProps.game;

public class Title implements AnimatedObject{
protected Render renderer;
protected String textureName;
protected int rows, cols;
protected int titleWidth;
protected boolean isDeleted;
protected boolean dontDelete;
protected int titleHeight;
protected boolean isAnimated;
protected Animation animation;
protected Vector2i resolution;
protected boolean isDestroy;
protected Vector2i currentOffset = new Vector2i();
protected Transform transform = new Transform();
public Title(ScreenAlignment alignment, int Width, int Height, String textureName) {
    titleHeight = Height;
    titleWidth = Width;
    this.textureName = textureName;
    resolution = game.getResolution();
    transform.translate(alignment.getX(resolution.x), alignment.getY(resolution.y));
    init();
}
public Title(ScreenAlignment alignment, Render render, Boolean isAnimated) {
    resolution = game.getResolution();
    this.renderer = render;
    transform.translate(alignment.getX(resolution.x), alignment.getY(resolution.y));
    dontDelete = true;
    this.isAnimated = isAnimated;
}
public Title(float worldX, float worldY, Render render, Boolean isAnimated) {
    resolution = game.getResolution();
    this.renderer = render;
    transform.translate(worldX, worldY);
    dontDelete = true;
    this.isAnimated = isAnimated;
}
public Title(ScreenAlignment alignment, int Width, int Height, String atlasName, int cols, int rows, float frameTime) {
    titleHeight = Height;
    titleWidth = Width;
    this.cols = cols;
    this.rows = rows;
    this.textureName = atlasName;
    resolution = game.getResolution();
    transform.translate(alignment.getX(resolution.x), alignment.getY(resolution.y));
    initAnimated(frameTime);
}
protected void init() {
renderer = new Render(titleWidth, titleHeight, textureName);
renderer.initShader(this.getClass().getSimpleName());
isAnimated = false;
}
protected void initAnimated(float frameTime) {
renderer = new Render(titleWidth, titleHeight, textureName, cols, rows);
renderer.initShader(this.getClass().getSimpleName());
isAnimated = true;
initAnimation(frameTime);
}
public boolean isAnimated() {
    return isAnimated;
}
public void setOffset(int x, int y) {   
 
    currentOffset.x = x;
    currentOffset.y = y;
    transform.smoothTranslate(x, y);
}
public void draw() {
    renderer.draw(App.getDeltaTime(), transform);
}
public void delete() {
    if(!dontDelete) renderer.delete();
    isDeleted = true;
}
public boolean isDeleted() {
    return isDeleted;
}
public void destroy() {
    if (!isDestroy) {
        animation.resetAnimation();
        isDestroy = true;
    }

    if (animation.isFrameLast()) {
        animation.stopAnimation();
        delete();
    }
} 
public Transform getTransform() {
    return transform;
}
public boolean isDestroy() {
    return isDestroy;
}
@Override
public Animation getAnimation() {
    if(isAnimated) {
        return animation;
    }
    else throw new UnsupportedOperationException("не Анимированный объект");
}
@Override
public Render getRender() {
    if(isAnimated) {
    return renderer;
    }
    else throw new UnsupportedOperationException("не Анимированный объект");
}
@Override
public void initAnimation(float frameTime) {
    if(isAnimated) {
        animation = new Animation(this, frameTime);
    }
    else throw new UnsupportedOperationException("не Анимированный объект");
}
}
