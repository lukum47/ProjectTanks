package com.lukum.gameProps.menu;

import static org.lwjgl.system.MemoryUtil.memAllocFloat;
import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.system.MemoryUtil.memFree;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.lwjgl.system.windows.RECT;


import com.Interfaces.AnimatedObject;
import com.lukum.App;
import com.lukum.Manager.Enums.ScreenAlignment;
import com.lukum.Render.Animation;
import com.lukum.Render.Atlas;
import com.lukum.Render.Render;
import com.lukum.Render.ShaderProgram;
import com.lukum.Render.Texture2D;
import com.lukum.Render.Transform;
import com.lukum.gameProps.game;
import com.lukum.gameProps.units.gameObject;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.*;
public class Background implements AnimatedObject {
    private Animation animation;
    private Transform transform = new Transform();
    private Render renderer;
    private Vector2i resolution;
    public Background (ScreenAlignment alignment, String atlasName, int atlasCols, int atlasRows, int width, int height) {

       renderer = new Render(width, height, atlasName, atlasCols, atlasRows);
       // renderer = new Render(600, atlasName, atlasCols, atlasRows);
         renderer.initShader(this.getClass().getSimpleName());
         resolution = game.getResolution();
         transform.translate(alignment.getX(resolution.x), alignment.getY(resolution.y));
        initAnimation(0.04f);

    }

    public void draw() {
     //   transform.setProjection(new Matrix4f());
        renderer.draw(App.getDeltaTime(), transform);
    }
    @Override
    public void initAnimation(float frameTime) {
        this.animation = new Animation(this, frameTime);
    }
    
    public Transform getTransform() {
        return transform;
    }
    @Override
    public Animation getAnimation() {
        return this.animation;
    }

    @Override
    public Render getRender() {
        return renderer;
    }
   
      
    public void delete() {
        renderer.delete();
    }
}
