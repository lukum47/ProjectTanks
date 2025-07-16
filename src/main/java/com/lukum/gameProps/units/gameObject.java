package com.lukum.gameProps.units;

import java.nio.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryUtil.*;

import com.lukum.App;
import com.lukum.Manager.Enums.UnitType;
import com.lukum.Render.Animation;
import com.lukum.Render.Atlas;
import com.lukum.Render.Render;
import com.lukum.Render.ShaderProgram;
import com.lukum.Render.Texture2D;
import com.lukum.Render.Transform;
import com.lukum.gameProps.GameMap;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import java.util.*;

import org.joml.Vector2i;
import org.lwjgl.system.MemoryStack;

/**
 * Базовый класс для всех игровых объектов.
 * Реализует базовую функциональность рендеринга, работы с текстурами и трансформациями.
 */


public abstract class gameObject {
    //поля начало 
    protected float velocity = 0.f;    // Скорость объекта
    protected boolean isActive;        // Флаг активности объекта
    protected boolean deleated;
    //protected GameMap map;
    protected String textureName;
    protected Render renderer;
    protected static Render preloadedRender;
    protected boolean startDestroy;
    protected boolean isDestroy;
    protected Transform transform = new Transform(); // Трансформации объекта
    protected UnitType unitType = null;
    protected Animation animation = null;
    protected int tileSize;
    protected int currentXGrid;
    protected int currentYGrid;
    protected int oldXGrid;
    protected int oldYGrid;
    //Инициализация начало
    public gameObject(float worldX, float worldY, int tileSize) {
        this.tileSize = tileSize;
        this.getTransform().translate(worldX, worldY);
        init();
    }

    /**
     * Инициализация объекта:
     * - Загрузка шейдеров
     * - Настройка меша
     * - Загрузка стандартной текстуры
     */
    public UnitType getUnitType() 
    {
        return this.unitType;
    }
   
    protected void init() {
        isActive = true;
    }
    //Инициализация конец 

    public void setCurrentGridPos(int gridX, int gridY) {
        this.currentXGrid = gridX;
        this.currentYGrid = gridY;
    }
    public Vector2i getOldGridPos() {
        return new Vector2i(this.oldXGrid, this.oldYGrid);
    }
    public void setOldGridPos(int gridX, int gridY) {
        this.oldXGrid = gridX;
        this.oldYGrid = gridY;
    }
    public Vector2i getCurrentGridPos() {
        return new Vector2i(this.currentXGrid, this.currentYGrid);
    }
   
    public void activate() 
    {
        isActive = true;
    }
    public void deactivate() 
    {
        isActive = false;
    }
    public boolean isActive() {
        return isActive;
    }
    //  Рендеринг начало
    /**
     * Отрисовка объекта
     */
    public void draw(float deltaTime) {
        renderer.draw(deltaTime, transform);
    }
    public static void preloadResources(Render renderer){
        preloadedRender = renderer;
    }
    // Рендеринг конец

  //  Утилиты начало
    /**
     * Очистка ресурсов OpenGL
     */
    public abstract void destroy();
    
    public void delete() {
        renderer.delete();
        
        deleated = true;
    }
    public boolean isDeleted() {
        return this.deleated;
    }
    public boolean getDestroyStatus() {
        return startDestroy;
    }
    public Transform getTransform()
    {
        return this.transform;
    };
   
    // Утилиты конец
  }