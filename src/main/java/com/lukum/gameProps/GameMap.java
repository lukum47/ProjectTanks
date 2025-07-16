package com.lukum.gameProps;

import static com.lukum.Manager.Enums.UnitType.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import javax.sound.midi.ControllerEventListener;

import org.joml.Vector2f;
import org.joml.Vector2i;

import com.Interfaces.ControlableUnit;
import com.lukum.Manager.Enums.UnitType;
import com.lukum.gameProps.environment.HP;
import com.lukum.gameProps.environment.Wall;
import com.lukum.gameProps.menu.HpBar;
import com.lukum.gameProps.units.Bullet;
import com.lukum.gameProps.units.Player;
import com.lukum.gameProps.units.Tank;
import com.lukum.gameProps.units.animatedGameObject;
import com.lukum.gameProps.units.gameObject;

/**
 * Класс для работы с игровой картой.
 * Обрабатывает загрузку тайлов, их обновление при изменении размера окна и взаимодействие с объектами.
 */
public class GameMap {
    private String currentTileSet;         // Текущий тайлсет в виде строки
    private static GameMap instance = null;// Единственный экземпляр карты (Singleton)
    private Tile[][] grid;                 // Сетка тайлов
    private int tileSize;                  // Размер одного тайла в пикселях
    private ArrayList<Tank> tanks; // Список всех объектов на карте
    private ArrayList<Wall> wals;
    private ArrayList<Bullet> bullets; 
    private int enemyQuantity = 0;
    private ArrayList<animatedGameObject> animObj;
    private ArrayList<Vector2i> spawnPoints;
    private int dx, dy;                    // Размеры сетки (ширина и высота в тайлах)
    private int screenWidth, screenHeight; // Текущее разрешение экрана

    // Конструктор (приватный для Singleton)
    private GameMap(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.tanks = new ArrayList<>();
        this.bullets = new ArrayList<>();
        this.wals = new ArrayList<>();
        this.animObj = new ArrayList<>();
        this.spawnPoints = new ArrayList<>();
    }

    /**
     * Получение экземпляра карты (реализация Singleton).
     * @param screenWidth  Ширина экрана.
     * @param screenHeight Высота экрана.
     * @return Единственный экземпляр GameMap.
     */
    public static GameMap getInstance() {
      return instance;
    }
    public static void initialise(int screenWidth, int screenHeight) {
        if (instance == null) {
            instance = new GameMap(screenWidth, screenHeight);
        }
    }
    public void addEnvironment(animatedGameObject environment) {
        if(environment.isPhysical()) {
        Vector2i coords = environment.getCurrentGridPos();
       grid[coords.x][coords.y].exchangeTile(environment, environment.getUnitType());
        }
        animObj.add(environment);
    }
    public void addGameObject(UnitType type, int posX, int posY) {
        if(type == ENEMY) {
           grid[posX][posY] = createTile('E', posX, posY);
        }
    }
   
    public void addBullet(Bullet bullet) {
        if(bullet != null) {
            this.bullets.add(bullet);
        }
    }
    /**
     * Загрузка тайлсета из файла и инициализация карты.
     * @param tileSetPath Путь к файлу с тайлсетом.
     */
    public void setTileSet(String tileSetPath, int tileSize) {
        this.tileSize = tileSize;
        currentTileSet = readFromFile(tileSetPath);
        getGridSize();       // Определение размеров сетки
      //  calculateTileSize(); // Расчет размера тайлов
        initializeGrid();    // Заполнение сетки тайлами
    }
    public void resetGameMap() {
        initializeGrid();
    }
    public int getDx() {
        return dx;
    }
    
    public int getDy() {
        return dy;
    }
    public Tile[][] getTileSet() {
        return this.grid;
    }
    /**
     * Чтение тайлсета из файла.
     * @param path Путь к файлу.
     * @return Содержимое файла в виде строки.
     */
    private String readFromFile(String path) {
        StringBuilder fString = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                fString.append(line).append("\n");
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка чтения файла: " + path, e);
        }
        return fString.toString();
    }
    public void setEnemyQuantity(int quantity ) {
        enemyQuantity = quantity;
    }
    public int getEnemyQuantity() {
        return enemyQuantity;
    }
    /**
     * Расчет размеров сетки на основе тайлсета.
     * Проверяет, что все строки имеют одинаковую длину.
     */
    private void getGridSize() {
        String[] lines = currentTileSet.split("\n");
        dy = lines.length; // Количество строк
        dx = (lines.length > 0) ? lines[0].length() : 0;

        // Проверка на одинаковую длину строк
        for (String line : lines) {
            if (line.length() != dx) {
                throw new RuntimeException("Ошибка: строки тайлсета имеют разную длину");
            }
        }

        this.grid = new Tile[dx][dy];
    }

    /**
     * Инициализация сетки тайлами на основе символов тайлсета.
     */
    private void initializeGrid() {
        String[] lines = currentTileSet.split("\n");
        for (int y = 0; y < dy; y++) {
            for (int x = 0; x < dx; x++) {
                char token = lines[y].charAt(x);
                grid[x][y] = createTile(token, x, y);
            }
        }
        if(enemyQuantity == 0 || enemyQuantity < tanks.size()-1) {
            enemyQuantity = tanks.size()-1;//минус танк игрока
        }
    }

    /**
     * Создание тайла заданного типа.
     * @param token Символ из тайлсета.
     * @param x     Координата X в сетке.
     * @param y     Координата Y в сетке.
     * @return Созданный тайл.
     */
    private Tile createTile(char token, int x, int y) {
        Tank tank = null;
        Tile tile = null;
        Wall wall = null;
        animatedGameObject obj = null;
        UnitType type = UnitType.EMPTY;

      switch (token) {
            case 'B' : 
            type = UnitType.BRICK_WALL;
            wall = (Wall)type.create(
                calculateWorldX(x), 
                calculateWorldY(y), 
                tileSize
            );
            wall.setCurrentGridPos(x, y);
              wals.add(wall);
              tile = new Tile( x, y, 
              tileSize, 
              type, 
              dx, dy, 
              screenWidth, screenHeight,
              wall // Передаем созданный объект или null
              );
            break;
            case 'A' : 
            type = UnitType.ARMOR_WALL;
            wall = (Wall)type.create(
                calculateWorldX(x), 
                calculateWorldY(y), 
                tileSize
            );
            wall.setCurrentGridPos(x, y);
              wals.add(wall);
              tile = new Tile( x, y, 
              tileSize, 
              type, 
              dx, dy, 
              screenWidth, screenHeight,
              wall // Передаем созданный объект или null
              );
            break;
        case 'P':
            type = UnitType.PLAYER;
            // Проверка на существующего игрока
            if (tanks.stream().anyMatch(o -> o instanceof Player)) {
                throw new RuntimeException("Игрок уже существует на карте");
            }
            tank = (Tank)type.create(
                calculateWorldX(x),
                calculateWorldY(y),
                tileSize
            );
            tank.setCurrentGridPos(x, y);
            tanks.add(tank);
            tile = new Tile( x, y, 
            tileSize, 
            type, 
            dx, dy, 
            screenWidth, screenHeight,
            tank // Передаем созданный объект или null
            );
            break;
            case 'W':
            type = UnitType.WATER;

            obj = (animatedGameObject)type.create(
                calculateWorldX(x),
                calculateWorldY(y),
                tileSize
            );
            obj.setCurrentGridPos(x, y);
            animObj.add(obj);
            tile = new Tile( x, y, 
            tileSize, 
            type, 
            dx, dy, 
            screenWidth, screenHeight,
            obj // Передаем созданный объект или null
            );
            break;
            case 'R': 
            spawnPoints.add(new Vector2i(x, y));
            type = UnitType.EMPTY;
            tile = new Tile( x, y, 
            tileSize, 
            type, 
            dx, dy, 
            screenWidth, screenHeight,
            null); 
            break;
            case 'E':
            type = UnitType.ENEMY;
            tank = (Tank)type.create(
                calculateWorldX(x),
                calculateWorldY(y),
                tileSize
            );
            tank.setCurrentGridPos(x, y);
            tank.activate();
            tanks.add(tank);
            tile = new Tile( x, y, 
            tileSize, 
            type, 
            dx, dy, 
            screenWidth, screenHeight,
            tank // Передаем созданный объект или null
            );
            break;
        default:
            type = UnitType.EMPTY;
            tile = new Tile( x, y, 
            tileSize, 
            type, 
            dx, dy, 
            screenWidth, screenHeight,
            null // Передаем созданный объект или null
            );
    }

    return tile;
    }

    /**
     * Обновление размера окна и пересчет тайлов.
     * @param screenWidth  Новая ширина экрана.
     * @param screenHeight Новая высота экрана.
     */
    public void resizeWindow(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
      //  calculateTileSize(); // Пересчет размера тайлов
        updateAllTileSizes();    // Обновление всех тайлов
    }
    // Вспомогательные методы для расчета координат
private float calculateWorldX(int gridX) {
    float offsetX = (screenWidth - dx * tileSize) / 2.0f;
    return offsetX + gridX * tileSize;
}

private float calculateWorldY(int gridY) {
    float offsetY = (screenHeight - dy * tileSize) / 2.0f;
    return offsetY + gridY * tileSize;
}
public void updateTiles() { //обновление тайлов в цикле 
    for(int i = 0; i < dx; i++) { // по большому счету нужно для обновления методов типа destroy
        for(int j = 0; j < dy; j++) {
            Tile tile = grid[i][j];
             gameObject obj = tile.getObject();
             if(obj != null) {
             if(obj.getDestroyStatus()) {
                    obj.destroy();
                    if(obj.isDeleted()) tile.exchangeTile(null, EMPTY);
                }
            }
        }
    }
    for(Bullet bullet : bullets) {
        if(bullet != null && bullet.isDestroy()) { 
            bullet.destroy();
        }   
    }
    for(animatedGameObject obj : animObj) {
        if(obj.isPhysical() && !obj.isDeleted()) {
            grid[obj.getCurrentGridPos().x][obj.getCurrentGridPos().y].exchangeTile(obj, obj.getUnitType());
        }
        if(obj != null && obj.isTerminatable() || obj.isDestroing()) {
            obj.destroy();
            if(obj.isPhysical()) {
                     grid[obj.getCurrentGridPos().x][obj.getCurrentGridPos().y].exchangeTile(null, EMPTY);  
                obj.setPhysic(false);
            }
        }
    }
}
public void updateTileCondition(int gridX, int gridY, UnitType condition) {
    this.grid[gridX][gridY].exchangeTile(null, condition);
} 
    /**
     * Обновление размера всех тайлов на карте.
     */
    public void updateAllTileSizes() {
        for (int y = 0; y < dy; y++) {
            for (int x = 0; x < dx; x++) {
                if (grid[x][y] != null) {
                    grid[x][y].updateTileSize(tileSize);
                }
            }
        }
    }
      /**
     * Обновляет позицию объекта в сетке.
     * вначале движения и в процессе танк занимает 2 клетки 
     */
    public void updateObjectPosition(ControlableUnit unit, int newGridX, int newGridY) {
        if (newGridX < 0 || newGridX >= dx || newGridY < 0 || newGridY >= dy) {
            throw new IllegalArgumentException("Недопустимые координаты сетки");
        }
        
        Vector2i oldPos = unit.getCurrentGridPos();
        unit.setCurrentGridPos(newGridX, newGridY);
        unit.setOldGridPos(oldPos.x, oldPos.y);
        gameObject object = grid[oldPos.x][oldPos.y].getObject();
       // grid[oldPos.x][oldPos.y].exchangeTile(null, EMPTY);
    //    if(object == null) {
    //     grid[newGridX][newGridY].exchangeTile(object, EMPTY);
    //    }
    //    else
        grid[newGridX][newGridY].exchangeTile(object, object.getUnitType());
    }
    // public void setNewPosition(ControlableUnit unit, int newGridX, int newGridY) {
    //     if (newGridX < 0 || newGridX >= dx || newGridY < 0 || newGridY >= dy) {
    //         throw new IllegalArgumentException("Недопустимые координаты сетки");
    //     }
    //     Vector2i oldPos = unit.getCurrentGridPos();
    //     unit.setCurrentGridPos(newGridX, newGridY);
    //     unit.setOldGridPos(oldPos.x, oldPos.y);
    //     gameObject object = grid[oldPos.x][oldPos.y].getObject();
    //     grid[newGridX][newGridY].exchangeTile(object, object.getUnitType());
    // }
    /**
     * этим методом очищаем старую позицию после того, как танк завершит движение 
     * @param unit // интерфейс из которого получаем нужные координаты сетки 
     */
    public void clearOldPosition(ControlableUnit unit) {
        Vector2i oldPos = unit.getOldGridPos();
        if(grid[oldPos.x][oldPos.y].getType() != EMPTY) {
            grid[oldPos.x][oldPos.y].exchangeTile(null, EMPTY);
        }
    }
    /**
     * Расчет размера тайла с учетом пропорций экрана и карты.
     */
    // private void calculateTileSize() {
    //     if (dx == 0 || dy == 0) return;
    //     tileSize = 32;
    //     // float screenAspect = (float) screenWidth / screenHeight;
    //     // float mapAspect = (float) dx / dy;

    //     // // Масштабирование по меньшей стороне
    //     // tileSize = (screenAspect > mapAspect) 
    //     //     ? (int) (screenHeight / dy)  // Экран шире → масштабируем по высоте
    //     //     : (int) (screenWidth / dx); // Экран уже → масштабируем по ширине
    // }

    // ---------------------- Вспомогательные методы ----------------------

      /**
     * Преобразует сеточные координаты в мировые с учетом центрирования.
     */
    public Vector2f gridToWorldCentered(int gridX, int gridY) {
        float offsetX = (screenWidth - dx * tileSize) / 2.0f;
        float offsetY = (screenHeight - dy * tileSize) / 2.0f;
        return new Vector2f(
            offsetX + gridX * tileSize,
            offsetY + gridY * tileSize
        );
    }
    /**
     * Преобразует мировые координаты в сеточные с учетом центрирования.
     */
    public Vector2i worldToGridCentered(float worldX, float worldY) {
        float offsetX = (screenWidth - dx * tileSize) / 2.0f;
        float offsetY = (screenHeight - dy * tileSize) / 2.0f;
        return new Vector2i(
            (int) ((worldX - offsetX) / tileSize),
            (int) ((worldY - offsetY) / tileSize)
        );
    }
    public ArrayList<Bullet> getBullets() {
        return this.bullets;
    }
    /**
     * Проверка, является ли ячейка пустой.
     */
    public boolean isCellEmpty(int gridX, int gridY) {
        // Проверка на валидность координат
        if (gridX < 0 || gridX >= dx || gridY < 0 || gridY >= dy) {
            return false;
        }
        return grid[gridX][gridY].getType() == UnitType.EMPTY;
    }
    public int getTileSize() {
        return this.tileSize;
    }
    /**
     * Получение списка всех объектов на карте.
     */
    public ArrayList<Tank> getTanks() {
        return tanks;
    }
    public ArrayList<animatedGameObject> getEnvironment() {
        return animObj;
    }
    public ArrayList<Wall> getWalls() {
        return wals;
    }
    public Tile getTile(int gridX, int gridY) {
        if(gridX <= this.dx && gridY <= this.dy)
        return this.grid[gridX][gridY];
        else return null;
    }
    public ArrayList<Vector2i> getSpawnPoints() {
        return spawnPoints;
    }
    /**
     * Удаление объекта с карты.
     */
    public void removeObject(gameObject object) {
        Vector2i position = worldToGridCentered(
            object.getTransform().getPosition().x, 
            object.getTransform().getPosition().y
        );
        grid[position.x][position.y].exchangeTile(null, EMPTY);
    }
}