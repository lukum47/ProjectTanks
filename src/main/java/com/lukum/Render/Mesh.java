
package com.lukum.Render;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.*;
import java.util.ArrayList;

public class Mesh {
    private int VAO, VBO, EBO;
    private int vertexCount;
    private ArrayList<Texture> textures = null;

     

        public static class Texture {
        public int id;
        public String type;
        public String path;
    }

    public Mesh(float[] vertices, int[] indices) {
        // Создание буферов
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
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);

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

    public Mesh(float[] vertices, int[] indices, ArrayList<Texture> textures) {
        this.textures = textures;
        // Создание буферов
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
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);

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
    public void draw(ShaderProgram shader) {
        for (int i = 0; i < textures.size(); i++) 
        { 
            glActiveTexture(GL_TEXTURE0 + i);
            shader.setInt(textures.get(i).type, i);
            glBindTexture(GL_TEXTURE_2D, textures.get(i).id);
        }
        shader.use();
        glBindVertexArray(VAO);
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    public void deleteMesh() {
        glDeleteVertexArrays(VAO);
        glDeleteBuffers(VBO);
        glDeleteBuffers(EBO);
    }
}