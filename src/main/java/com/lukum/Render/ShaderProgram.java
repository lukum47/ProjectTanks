package com.lukum.Render;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

import java.io.*;
import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

public class ShaderProgram {
        private boolean m_isCompiled = false; 
		private int m_ID = 0;
    
       public ShaderProgram(String vertexPath, String fragmentPath) {
        // Загрузка шейдеров
        String vertexSource = readFromFile(vertexPath);
        String fragmentSource = readFromFile(fragmentPath);

        // Компиляция
        int vertexID = compileShader(GL_VERTEX_SHADER, vertexSource);
        int fragmentID = compileShader(GL_FRAGMENT_SHADER, fragmentSource);

        // Линковка
        m_ID = glCreateProgram();
        glAttachShader(m_ID, vertexID);
        glAttachShader(m_ID, fragmentID);
        glLinkProgram(m_ID);

        // Проверка ошибок
        if (glGetProgrami(m_ID, GL_LINK_STATUS) == GL_FALSE) {
            throw new RuntimeException("Shader linking failed: " + glGetProgramInfoLog(m_ID));
        }

        // Освобождение
        glDeleteShader(vertexID);
        glDeleteShader(fragmentID);
    }

    private int compileShader(int type, String source) {
        int shaderID = glCreateShader(type);
        glShaderSource(shaderID, source);
        glCompileShader(shaderID);

        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new RuntimeException("Shader compilation failed: " + glGetShaderInfoLog(shaderID));
        }

        return shaderID;
    }


    public boolean isCompiled() {
        return m_isCompiled;
    };

    public void setVEC3 (String name, float x, float y, float z)
    {
        int objectLoc = glGetUniformLocation(m_ID, name);
        glUniform3f(objectLoc, x, y, z);
    }
        
    public void setVEC3 (String name, Vector3f vec)
    {
        int objectLoc = glGetUniformLocation(m_ID, name);
        glUniform3f(objectLoc, vec.x, vec.y, vec.z);
    }
    public void setVEC4 (String name, Vector4f vec)
    {
        int objectLoc = glGetUniformLocation(m_ID, name);
        glUniform4f(objectLoc, vec.x, vec.y, vec.z, vec.w);
    }
    public void setInt (String name, int value) 
    {
        int objectLoc = glGetUniformLocation(m_ID, name);
        glUniform1i(objectLoc, value);
    }
    public void setBool(String name, boolean state) {
         int objectLoc = glGetUniformLocation(m_ID, name);
         int value = state ? 1 : 0;
         glUniform1i(objectLoc, value);
    }
    public void setMTRX4(String name, Matrix4f mtrx) 
    {
        int objectLoc = glGetUniformLocation(m_ID, name);
        FloatBuffer buf = BufferUtils.createFloatBuffer(16);
        mtrx.get(buf);
        glUniformMatrix4fv(objectLoc, false, buf);
    }
    public void setVEC2(String name, Vector2f vec) 
    {
        int objectLoc = glGetUniformLocation(m_ID, name);
        glUniform2f(objectLoc, vec.x, vec.y);
    }
    public void setFloat(String name, float value) 
    {
        int objectLoc = glGetUniformLocation(m_ID, name);
        glUniform1f(objectLoc, value);
    }
    
    private String readFromFile(String path) {
        StringBuilder fString = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {

            String line;
            while ((line = br.readLine()) != null) {
                fString.append(line).append("\n");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read shader file: " + path, e);
        }
        return fString.toString();
    }

    public void use() {
        glUseProgram(m_ID);
     }
     
     public void delete() {
        glDeleteProgram(m_ID);
    }
}
