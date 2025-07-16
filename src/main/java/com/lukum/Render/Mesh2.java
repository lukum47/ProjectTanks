package com.lukum.Render;


import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

public class Mesh2 {
  // Содаем класс для хранения атрибутов вершин 
public static class Vertex 
    {
        public Vector3f position;
        public Vector2f texCoords;

        public Vertex(Vector3f position) 
        {
            this(position, new Vector2f()); //Вызываем второй конструктор чтобы не повторять код 
        }

        public Vertex(Vector3f position, Vector2f texCoords ) 
        {
            this.position = position;
            this.texCoords = texCoords;
        }
        
    } 

public static class Texture 
    {   

        public int id;
        public String type;
        public String path;

        Texture(int id, String type, String path) 
        {
            this.id = id;
            this.type = type;
            this.path = path;
        }
    }
        private final int vao;
        private final int vbo;
        private final int ebo;
        private final int vertexCount;
        private final List<Texture> textures;
        private final int countFloat; 
        public Mesh2(List<Vertex> vertices, List<Integer> indices, List<Texture> textures) 
        {
            if(textures.isEmpty()) 
            {
                this.textures = List.of();
                countFloat = 3;
            }
            else 
            {
                this.textures = textures;
                countFloat = 5;
            }
            this.vertexCount = indices.size();

            // Создание буферов
            vao = glGenVertexArrays();
            vbo = glGenBuffers();
            ebo = glGenBuffers();

            setupMesh(vertices, indices);
        }

        private void setupMesh(List<Vertex> vertices, List<Integer> indices) 
        {

            // Создаем буффер float'ов из массива вертексов 

            FloatBuffer vertBuffer = MemoryUtil.memAllocFloat(vertices.size() * countFloat); // countFloat - кол-во float'ов в классе Vertex
            
            for (Vertex vertex : vertices) 
            {
                vertex.position.get(vertBuffer); //копируем все float'ы из поля position в буфер 
                if(countFloat == 5) vertex.position.get(vertBuffer);

            }
            vertBuffer.flip();

            IntBuffer indexBuf = MemoryUtil.memAllocInt(indices.size());
            indices.forEach(indexBuf::put);
            indexBuf.flip();

             // Настройка VAO
            glBindVertexArray(vao);

            // VBO
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, vertBuffer, GL_STATIC_DRAW);

            // EBO
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuf, GL_STATIC_DRAW);

            int stride = countFloat * Float.BYTES; //кол-во на размер float в байтах

            // Position (location 0)
            glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
            glEnableVertexAttribArray(0);

            glBindVertexArray(0);
            MemoryUtil.memFree(vertBuffer);
            MemoryUtil.memFree(indexBuf);
        }
        public void draw(ShaderProgram shader) 
        {
          // Проверка наличия текстур
            for(int i = 0; i < textures.size(); i++) 
            {
                glActiveTexture(GL_TEXTURE0 + i);
                Texture texture = textures.get(i);
                shader.setInt(texture.type, i);
                glBindTexture(GL_TEXTURE_2D, texture.id);
            }

         // Отрисовка меша
        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);

        // Сброс активной текстуры
        if(!textures.isEmpty()) {
            glActiveTexture(GL_TEXTURE0);
        }
    }

    public void cleanup() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
    }

}
