package com.lukum.Render;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;
import java.util.Map;

import org.joml.Vector2f;


public class Texture2D {

    public class Tile2D 
    {
        public Vector2f leftBottomUV;
        public Vector2f rightBottomUV;

        Tile2D(Vector2f leftBottomUV, Vector2f rightBottomUV) 
        {
            this.leftBottomUV = leftBottomUV;
            this.rightBottomUV = rightBottomUV;
        }
        Tile2D() 
        {
            this.leftBottomUV.set(0.f);
            this.rightBottomUV.set(1.f);
        }
    }
    private Map<String, Tile2D> m_TileMap;
    
    public Texture2D(int width, int height, ByteBuffer data, int chanels, int filter, int wrapMode, String textureName)
    {
        initTextureUnit(width, height, data, chanels, filter, wrapMode, textureName);
      
    }
    public void addTile(String texName, Vector2f leftBottomUV, Vector2f rightBottomUV) 
    {
        m_TileMap.put(texName, new Tile2D(leftBottomUV, rightBottomUV));
    }
    public Tile2D getTile(String texName) 
    {
        Tile2D tile = this.m_TileMap.get(texName);
        if(tile != null) // Проверяем map на наличие искомого тайла 
        {
            return tile;
        }
        return new Tile2D();// если не находим возвращаем всю текстуру 
    }
    public void rewriteTextureUnit(int width, int height, ByteBuffer data, int chanels, int filter, int wrapMode, String textureName)
    {
        this.delete();
        this.initTextureUnit(width, height, data, chanels, filter, wrapMode, textureName);
    }
    private void initTextureUnit(int width, int height, ByteBuffer data, int chanels, int filter, int wrapMode, String textureName) 
    {
        this.textureName = textureName;
        m_Width = width;
        m_Height = height;
        switch(chanels) {
            case 4: {m_Mode = GL_RGBA;} break;
            case 3: { m_Mode = GL_RGB; } break;
            default: { m_Mode = GL_RGBA; } break;
        }
        
        m_ID = glGenTextures();
      	glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, m_ID);
		glTexImage2D(GL_TEXTURE_2D, 0, m_Mode, m_Width, m_Height, 0, m_Mode, GL_UNSIGNED_BYTE, data);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrapMode);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrapMode);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter);
		glGenerateMipmap(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D, 0);
    }
    public String getName() 
    {
        return this.textureName;
    }
    public int getWidth() { return m_Width; }

    public int getHeight() { return m_Height; }
    
    public void bind() 
    {
        glBindTexture(GL_TEXTURE_2D, m_ID);
    }
    public void delete() {
        glDeleteTextures(m_ID);
    }
    private int m_ID;
    private int m_Mode;
    private String textureName;
    private int m_Width = 0;
    private int  m_Height = 0;
}
