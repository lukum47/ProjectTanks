package com.lukum.gameProps;
import org.joml.Vector2f;

import com.lukum.Manager.Enums.UnitType;
import com.lukum.gameProps.units.gameObject;

/**
 * Класс для представления тайла на игровой карте.
 * Обрабатывает позиционирование, создание объектов и обновление параметров.
 */
public class Tile {
    private Vector2f worldCoords;   // Мировые координаты тайла (в пикселях)
    private int tileSize;           // Текущий размер тайла
    private int gridX, gridY;       // Позиция в сетке карты
    private gameObject object;      // Игровой объект, связанный с тайлом
    private UnitType type;          // Тип тайла (стена, игрок, пустота и т.д.)
    private int maxGridX, maxGridY; // Максимальные размеры сетки карты
    private int screenWidth, screenHeight; // Разрешение экрана

    /**
     * Конструктор тайла.
     * @param gridX        Координата X в сетке карты.
     * @param gridY        Координата Y в сетке карты.
     * @param tileSize     Размер тайла в пикселях.
     * @param type         Тип тайла.
     * @param maxGridX     Ширина сетки карты (количество тайлов по X).
     * @param maxGridY     Высота сетки карты (количество тайлов по Y).
     * @param screenWidth  Ширина экрана.
     * @param screenHeight Высота экрана.
     */
    public Tile(
        int gridX, int gridY, 
        int tileSize, UnitType type,
        int maxGridX, int maxGridY,
        int screenWidth, int screenHeight,
        gameObject obj // Принимаем готовый объект
    ) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.tileSize = tileSize;
        this.type = type;
        this.object = obj; // Сохраняем переданный объект
        this.maxGridX = maxGridX;
        this.maxGridY = maxGridY;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        
        // gridToWorld(); // Рассчитываем координаты
        // if (obj != null) {
        //     obj.getTransform().translate(worldCoords.x, worldCoords.y);
        // }
    }
    /**
     * Преобразует координаты сетки в мировые с учетом центрирования карты.
     */
    private void gridToWorld() {
        // Смещение для центрирования карты на экране
        float offsetX = (screenWidth - maxGridX * tileSize) / 2.0f;
        float offsetY = (screenHeight - maxGridY * tileSize) / 2.0f;
        
        worldCoords = new Vector2f(
            offsetX + gridX * tileSize, // X с учетом центрирования
            offsetY + gridY * tileSize  // Y с учетом центрирования
        );
    }
    
    /**
     * Заменяет тип тайла и пересоздает связанный объект.
     * @param type Новый тип тайла.
     */
    public void exchangeTile(gameObject object, UnitType type) {
        // if (this.object != null) {
        //     /this.object.delete();
        // }
        this.object = object;
        this.type = type;
        // if (object != null) {
        //     object.getTransform().translate(worldCoords.x, worldCoords.y);
        // }
    }

    // ---------------------- Геттеры ----------------------
    public gameObject getObject() {
        return this.object;
    }

    public UnitType getType() {
        return this.type;
    }

    /**
     * Обновляет размер тайла и его позицию на экране.
     * @param tileSize Новый размер тайла.
     */
    public void updateTileSize(int tileSize) {
        this.tileSize = tileSize;
        gridToWorld();
        this.object.getTransform().translate(worldCoords.x, worldCoords.y);
    }

    /**
     * Обновляет позицию тайла в сетке карты.
     * @param gridX Новая координата X в сетке.
     * @param gridY Новая координата Y в сетке.
     */
    public void updatePosition(int gridX, int gridY) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.object.setCurrentGridPos(gridX,gridY);
        gridToWorld();
        this.object.getTransform().translate(worldCoords.x, worldCoords.y);
    }
}