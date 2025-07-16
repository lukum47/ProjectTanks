package com.lukum.Render;
import com.lukum.Render.Texture2D;
import org.joml.Vector2f;
import org.lwjgl.system.MemoryStack;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL30.*;
public class Atlas {
    private Texture2D texture;
    private int atlasWidth;
    private int atlasHeight;
    private int spriteWidth;
    private int spriteHeight;
    private String atlasName;
    private boolean reversed;
    private int cols;
    private int rows;
    private ArrayList<ArrayList<Sprite>> spriteMap = new ArrayList();

    public Atlas(String texturePath, int cols, int rows) {
        int lastSlashIndex = texturePath.lastIndexOf("/");
        int lastDotIndex = texturePath.lastIndexOf(".");

        // Проверка наличия '/' и корректности индекса
        if (lastSlashIndex == -1) {
            throw new IllegalArgumentException("Символ '/' не найден в пути: " + texturePath);
        }

        // Проверка наличия '.' и его позиции после '/'
        if (lastDotIndex == -1 || lastDotIndex <= lastSlashIndex) {
         throw new IllegalArgumentException("Некорректный формат пути: " + texturePath);
        }

        // Проверка, что индекс '/' + 1 не выходит за границы
        int startIndex = lastSlashIndex + 1;
        if (startIndex >= texturePath.length()) {
            throw new IllegalArgumentException("Нет данных после '/' в пути: " + texturePath);
        }
        String lastSymbol = texturePath.substring(texturePath.length()-1, texturePath.length());
        if(lastSymbol.equals("R")) {
            reversed = true;
            texturePath = texturePath.substring(0, texturePath.length()-1);
        }
        atlasName = texturePath.substring(startIndex, lastDotIndex);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer nrChannels = stack.mallocInt(1);
            
            ByteBuffer image = stbi_load(texturePath, width, height, nrChannels, 0);
            if (image == null) {
                throw new RuntimeException("Failed to load texture: " + stbi_failure_reason());
            }
            
            this.atlasWidth = width.get(0);
            this.atlasHeight = height.get(0);
            this.cols = cols;
            this.rows = rows;
            this.spriteWidth = atlasWidth / cols;
            this.spriteHeight = atlasHeight / rows;
            
            this.texture = new Texture2D(atlasWidth, atlasHeight, image, nrChannels.get(0), 
                GL_NEAREST, GL_CLAMP_TO_EDGE, texturePath);
            
            stbi_image_free(image);
            separateByLines();
        }
    }
    public String getName()
    {
        return this.atlasName;
    }
   
    public float[] getSprite(int row, int col, int tileSize) 
    {
       Vector2f[] uv = spriteMap.get(row).get(col).getUV();
       float halfSize = (float)tileSize/2;
       float[] vertices = {
            // x     y      z       u     v
            -halfSize,  halfSize, 0.0f, uv[0].x, uv[0].y, // Верхний левый
            halfSize,  halfSize, 0.0f, uv[1].x, uv[1].y, // Верхний правый
            halfSize, -halfSize, 0.0f, uv[2].x, uv[2].y, // Нижний правый
            -halfSize, -halfSize, 0.0f, uv[3].x, uv[3].y  // Нижний левый
        };
        return vertices;
    }
    public float[] getSprite(int row, int col, int width, int height) 
    {
              Vector2f[] uv = spriteMap.get(row).get(col).getUV();
       float halfWidth = (float)width/2;
       float halfHeight = (float)height/2;
       float[] vertices = {
            // x     y      z       u     v
            -halfWidth,  halfHeight, 0.0f, uv[0].x, uv[0].y, // Верхний левый
            halfWidth,  halfHeight, 0.0f, uv[1].x, uv[1].y, // Верхний правый
            halfWidth, -halfHeight, 0.0f, uv[2].x, uv[2].y, // Нижний правый
            -halfWidth, -halfHeight, 0.0f, uv[3].x, uv[3].y  // Нижний левый
        };
        return vertices;
    }
   
    public Texture2D getTexture() {
        return texture;
    }
    private void separateByLines() {
    if(!reversed) {
    for (int row = 0; row < rows; row++) {
        ArrayList<Sprite> rowSprites = new ArrayList<>();
        for (int col = 0; col < cols; col++) {
            Sprite sprite = new Sprite(
                col, 
                row, 
                this.spriteWidth, 
                this.spriteHeight, 
                this.atlasWidth, 
                this.atlasHeight, 
                this.cols, 
                this.rows
            );
            rowSprites.add(sprite);
        }
        spriteMap.add(rowSprites);
    }
}
else {
    for (int row = rows-1; row >= 0; row--) {
        ArrayList<Sprite> rowSprites = new ArrayList<>();
        for (int col = cols-1; col >= 0; col--) {
            Sprite sprite = new Sprite(
                col, 
                row, 
                this.spriteWidth, 
                this.spriteHeight, 
                this.atlasWidth, 
                this.atlasHeight, 
                this.cols, 
                this.rows
            );
            rowSprites.add(sprite);
        }
        spriteMap.add(rowSprites);
    } 
}
}
public void delete() {
    if (texture != null) {
        texture.delete(); 
        texture = null;
    }
}

public int getRows() {
    return this.rows;
}
public int getCols() {
    return this.cols;
}
}