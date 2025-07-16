package com.lukum.Render;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_NEAREST_MIPMAP_NEAREST;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL15.glIsBuffer;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Vector;

import org.joml.Vector2i;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;
import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.system.MemoryUtil.memFree;

import com.lukum.App;
import com.lukum.gameProps.game;

public class Render {   
    private int VAO, VBO, EBO;       // OpenGL буферы
    private int vertexCount;         // Количество вершин для отрисовки
    private String vertSource;       // Путь к вершинному шейдеру
    private String fragSource;       // Путь к фрагментному шейдеру
    private ShaderProgram shader;    // Шейдерная программа
    private Texture2D texture;
    private Atlas textureAtlas;
    private int tileSize;
    private int width, height;
    private boolean isTexture;
    private boolean isAtlas;
    public Render(int tileSize, String atlasName, int cols, int rows) {
        this.tileSize = tileSize;
        isAtlas = true;
        setTextureAtlas(atlasName, cols, rows);
        setSprite(0, 0);
    }
    public Render(int tileSize, Atlas textureAtlas) {
        this.tileSize = tileSize;
        isAtlas = true;
        this.textureAtlas = textureAtlas;
        setSprite(0, 0);
    }
    public Render(int width, int height, String atlasName, int cols, int rows) {
       this.width = width;
       this.height = height;
        isAtlas = true;
        setTextureAtlas(atlasName, cols, rows);
        setSprite(0, 0);
    }
    public Render(int tileSize, String textureName) {
        this.tileSize = tileSize;
        isTexture = true;
        textureFromFile(App.getTexturePath() + "/" + textureName);
        setupMesh();
    }
    public Render(int width, int height, String textureName) {
        isTexture = true;
        textureFromFile(App.getTexturePath() + "/" + textureName);
        setupMesh(width,height);
    }
    public void initShader(String className) {
                // Генерация путей к шейдерам на основе имени класса
        vertSource = App.getShaderPath() + className + "VertShader.txt";
        fragSource = App.getShaderPath() + className + "FragShader.txt";
        
        shader = new ShaderProgram(vertSource, fragSource);

    }
    public ShaderProgram getShader() {
        return shader;
    }
    public void setTextureAtlas(String atlasName, int cols, int rows) 
    {
        textureAtlas = new Atlas(App.getTexturePath() + "/" + atlasName, cols, rows);
       // setSprite(0, 0);
    }
    
    public void setSprite(int row, int col)
     {
        if(tileSize == 0 || width != 0 && height != 0) {
            setupMeshWithUV(textureAtlas.getSprite(row, col, width, height));
        }
        else
         setupMeshWithUV(textureAtlas.getSprite(row, col, tileSize));
     }

    private void textureFromFile(String path) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);
            
            ByteBuffer image = stbi_load(path, width, height, channels, 0);
            if (image == null) {
                throw new RuntimeException("Ошибка загрузки текстуры: " + path);
            }
            image.flip();
            // Извлечение имени текстуры из пути
            String name = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
                texture = new Texture2D(
                width.get(0), 
                height.get(0), 
                image, 
                channels.get(0), 
                GL_NEAREST_MIPMAP_NEAREST, 
                GL_CLAMP_TO_EDGE, 
                name
            );
            stbi_image_free(image);
        }
    }

    public void changeTexture(String textureName) {
        this.texture.delete();
        textureFromFile(App.getTexturePath() + "/" + textureName);
    }
    public Atlas getAtlas() {
        return this.textureAtlas;
    }
    protected void processMesh(float vertices[], int indices[]) 
    {
         FloatBuffer vertexBuffer = memAllocFloat(vertices.length);
        vertexBuffer.put(vertices).flip();

        IntBuffer indexBuffer = memAllocInt(indices.length);
        indexBuffer.put(indices).flip();

        // Инициализация OpenGL объектов
        VAO = glGenVertexArrays();
        glBindVertexArray(VAO);

        VBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
       
        
         // Указываем атрибуты вершин (координаты + текстурные координаты)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 20, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 20, 12);
        glEnableVertexAttribArray(1);

        EBO = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);

        // Отвязка
        glBindVertexArray(0);
        vertexCount = indices.length;

        // Освобождение памяти
        memFree(vertexBuffer);
        memFree(indexBuffer);
    }
     private void useShaderSingleTexture(Transform transform) {
        shader.use();
        shader.setMTRX4("m_Transform", transform.getModelProjectionMatrix());
        glActiveTexture(GL_TEXTURE0);
        texture.bind();
        shader.setInt("texture0", 0);
    }
    private void useShaderAtlas(Transform transform) {
        shader.use();
        shader.setMTRX4("m_Transform", transform.getModelProjectionMatrix());
        glActiveTexture(GL_TEXTURE0);
        textureAtlas.getTexture().bind(); 
        shader.setInt("texture0", 0);
    }
    public void setHighlightColor(Vector4f color) {
        shader.use();
        shader.setVEC4("highlightColor", color);
    }

    public void setHighlightIntensity(float intensity) {
        shader.use();
        shader.setFloat("highlightIntensity", intensity);
    }
    public void setColor(Vector4f color) {
        shader.use();
        shader.setVEC4("customColor", color);
    }
      public void draw(float deltaTime, Transform transform) {
        if(isAtlas) {
            useShaderAtlas(transform);
        }
        else if(isTexture) {
            useShaderSingleTexture(transform);
        }
        glBindVertexArray(VAO);
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }
    public void delete() {
        glDeleteVertexArrays(VAO);
        glDeleteBuffers(VBO);
        glDeleteBuffers(EBO);
        if(texture != null)
        this.texture.delete();
        if(textureAtlas != null) 
        textureAtlas.delete();
        shader.delete();
    }
    private void setupMesh() {

                 // Настройка меша (прямоугольник)
                 float halfSize = tileSize / 2.0f;
                    float[] vertices = {
                     -halfSize, -halfSize, 0.0f, 0.0f, 0.0f,
                      halfSize, -halfSize, 0.0f, 1f, 0.0f,
                      halfSize,  halfSize, 0.0f, 1f, 1f,
                     -halfSize,  halfSize, 0.0f, 0.0f, 1f
                 };
        
                int[] indices = {
                    0, 1, 2,
                    2, 3, 0
                };
        
                processMesh(vertices, indices);
        
    }
    /**
     * Ширина и высота в пикселях
     * @param width
     * @param height
     */
    private void setupMesh(int width, int height) {
    
        float[] vertices = {
            -width, -height, 0.0f, 0.0f, 0.0f,
            width, -height, 0.0f, 1f, 0.0f,
            width,  height, 0.0f, 1f, 1f,
            -width,  height, 0.0f, 0.0f, 1f
        };
    
        int[] indices = { 0, 1, 2, 2, 3, 0 };
        processMesh(vertices, indices);
    }
    public void setupMeshWithUV(float[] vertices) 
    {
        int[] indices = { 0, 1, 2, 2, 3, 0 };
        processMesh(vertices, indices);
    }
}
