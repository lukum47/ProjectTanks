package com.lukum.Render;
import org.joml.Vector2f;

public class Sprite 
{
    private float u;
    private float v;
    private float u2;
    private float v2;
    private float x1;
    private float y1;
    private float x2;
    private float y2;
    private Vector2f vec = new Vector2f();
    public Sprite (int col, int row, int spriteWidth, int spriteHeight, int atlasWidth, int atlasHeight, int cols, int rows) 
    {   
    //    // Нормализованные координаты
 
        int offsetX = spriteWidth * col;
        int offsetY = spriteHeight * row;
       
        x1 = offsetX/(float)atlasWidth;
        y1 = offsetY/(float)atlasHeight;
        x2 = (offsetX+(float)spriteWidth)/atlasWidth;
        y2 = (offsetY+(float)spriteHeight)/atlasHeight;
    } 
    
    public Vector2f[] getUV() 
    {

        return new Vector2f[] {
            new Vector2f(x1, y2),  // Верхний левый
            new Vector2f(x2, y2),  // Верхний правый
            new Vector2f(x2, y1),   // Нижний правый
            new Vector2f(x1, y1)    // Нижний левый
        };
        

    }
}