package com.lukum.Render;
import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;

import java.lang.Math;
import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MathUtil;

import com.lukum.App;

public class Transform {
    private Vector3f position;     // Позиция объекта
    private float rotation;        // Угол поворота (в радианах)
    private Vector3f scale;        // Масштаб
    private Matrix4f modelMatrix;  // Матрица модели (преобразования объекта)
    private static Matrix4f projectionMatrix = new Matrix4f(); // Общая матрица проекции
    
    public Transform() 
    {
        position = new Vector3f(0, 0, 0);
        rotation = 0.0f;
        scale = new Vector3f(1, 1, 1);
        modelMatrix = new Matrix4f().identity();
    }
    public void setProjection(float width, float height) {
        if (width == 0 || height == 0) return;
       // float aspectRatio = width / height;
        projectionMatrix.setOrtho(0, width, height, 0, -1, 1);
    }

    public void translate(float dx, float dy) {
        position.set(dx, dy, 0);
        updateModelMatrix();
    }
    public void setProjection(Matrix4f projection) {
        projectionMatrix = projection;
    }
    public static void setGlobalProjection(float width, float height) {
        if (width == 0 || height == 0) return;
        projectionMatrix.setOrtho(0, width, height, 0, -1, 1);
    }
    public void smoothTranslate(float dx, float dy) {
        position.add(dx, dy, 0);
        updateModelMatrix();
    }
    public void updateModelMatrix() {
        modelMatrix.identity()
            .translate(position)
            .rotateZ( rotation)
            .scale(scale);
    }
    public void rotate(float angle) {
        rotation = (float)Math.toRadians(angle);
        updateModelMatrix();
    }
    public boolean smoothRotate(float targetAngleDeg, float speedDegPerSec) {
        // Нормализация углов (приведение к диапазону 0-360)
        float currentRotationDeg = (float) Math.toDegrees(rotation) % 360;
        targetAngleDeg = targetAngleDeg % 360;
    
        // Вычисление кратчайшего направления поворота
        float deltaAngle = (targetAngleDeg - currentRotationDeg + 360) % 360;
        if (deltaAngle > 180) deltaAngle -= 360;
    
        float deltaStep = speedDegPerSec * App.getDeltaTime();
        float step = Math.copySign(Math.min(Math.abs(deltaAngle), deltaStep), deltaAngle);
    
        if (Math.abs(deltaAngle) > 0.1f) { // Порог завершения
            rotation += (float) Math.toRadians(step);
            updateModelMatrix();
            return true;
        } else {
            rotation = (float) Math.toRadians(targetAngleDeg);
            updateModelMatrix();
            return false;
        }
    }
    public Matrix4f getModelProjectionMatrix() {
        Matrix4f mpm = new Matrix4f(projectionMatrix).mul(modelMatrix);
        return mpm;
    }
    public void scale(float sx, float sy, int tileSize) {
        scale.mul(sx * tileSize, sy * tileSize, 1);
        updateModelMatrix();
    }
    public Vector3f getPosition() {
        return this.position;
    }
}