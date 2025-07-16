package com.lukum.gameProps;

import static com.lukum.Manager.Enums.UnitType.BRICK_WALL;
import static com.lukum.Manager.Enums.UnitType.PLAYER;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.text.TabSet;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.system.MemoryStack;
import com.lukum.Manager.Enums.UnitType;
import com.lukum.Manager.Enums.WallParam;
import com.lukum.Render.Transform;
import com.lukum.gameProps.environment.Wall;
import com.lukum.gameProps.menu.Background;
import com.lukum.gameProps.menu.GameOver;
import com.lukum.gameProps.menu.HpBar;
import com.lukum.gameProps.menu.MainMenu;
import com.lukum.gameProps.menu.Title;
import com.lukum.gameProps.units.Bullet;
import com.lukum.gameProps.units.Player;
import com.lukum.gameProps.units.Tank;
import com.lukum.gameProps.units.animatedGameObject;
import com.lukum.gameProps.units.gameObject;
import com.Interfaces.AnimatedObject;
import com.Interfaces.ControlableUnit;

import com.lukum.App;
import com.lukum.Manager.Animator;
import com.lukum.Manager.BehaviourManager;
import com.lukum.Manager.MoveController;
import com.lukum.Manager.MovementSystem;
import com.lukum.Manager.Respawn;
import com.lukum.Manager.Enums.HpType;


public class game {
    public class EGameState
    {
       public boolean Active;
       public boolean Pause;
       public boolean GameOver;
       public boolean buttonWasPressed;
       public boolean Reload;
       public boolean Exit;
    };
    private static EGameState eCurrentGameState;
    private int m_Keys[] = new int[GLFW_KEY_LAST+1]; 
    private GameMap map = null;
    private MoveController moveController;
    private MovementSystem movementSystem;
    private Respawn respawn;
    private BehaviourManager behaviour;
    private MainMenu mainMenu;
    private final int enemyQuantity = 7;
    private GameOver gameOver;
    private Animator animator = Animator.getInstance();
    private long window;
    private static int width, height;
    public boolean sceneIsEmpty = false;
    public game() 
    {
       eCurrentGameState = new EGameState();
       eCurrentGameState.Pause = true;
        for (int i = 0; i < m_Keys.length; i++) 
        {
            m_Keys[i] = GLFW_RELEASE;
        }
        
    }
    public void init(long window) 
    { 
        this.window = window;
    try (MemoryStack stack = MemoryStack.stackPush()) {
        IntBuffer w = stack.mallocInt(1);
        IntBuffer h = stack.mallocInt(1);
        glfwGetWindowSize(window, w, h);
        width = w.get(0);
        height = h.get(0);
    }
        setupResizeCallback();

        GameMap.initialise(width, height);
        map = GameMap.getInstance();
        map.setEnemyQuantity(enemyQuantity);
        map.setTileSet(App.getTileSetPath() + "map3.txt", 32);
        moveController = new MoveController(map);
        movementSystem = new MovementSystem(moveController, map);
        BehaviourManager.initialize(movementSystem, map);
        Respawn.initialize(map);
        HpType.preloadHP();
        respawn = Respawn.getInstance();
        behaviour = BehaviourManager.getInstance();
        mainMenu = new MainMenu();
        gameOver = new GameOver();
    }
    public static Vector2i getResolution () {
        return new Vector2i(width, height);
    }
    public void update(float deltaTime) 
    {   
        Transform.setGlobalProjection(width, height);
            updateGameState();
            
            if(eCurrentGameState.Active) {
            mainMenu.deactivate();
            gameOver.deactivate();
            setupResizeCallback();
            updateWalls(map.getWalls(), deltaTime);
            behaviour.updateTanks(map.getTanks());
            updateTanks(map.getTanks(), deltaTime);
            map.updateTiles();  
            updateEnvironment(map.getEnvironment(), deltaTime);
            respawn.update();
            updateBullets(map.getBullets(), deltaTime);
            }
        if(eCurrentGameState.Pause) {
            mainMenu.setActive();
            gameOver.deactivate();
            mainMenu.processInput(m_Keys);
           animator.animateObject(mainMenu.getBackground());
            mainMenu.draw();
        }
        if(eCurrentGameState.Reload) {
            map.resetGameMap();
            respawn.reset();

            eCurrentGameState.Active = true;
            eCurrentGameState.Reload = false;
            sceneIsEmpty = false;
        }
        if(eCurrentGameState.GameOver) {
            if(!sceneIsEmpty) {
                sceneIsEmpty = true;
                clearScene();
            }
            gameOver.setActive();
            animator.animateObject(gameOver.getBackground());
            ArrayList<Title> titles = gameOver.getTitles();
            for(Title it : titles) {
                if(it.isAnimated())
                animator.animateObject(it);
            }
            gameOver.processInput(m_Keys);
            gameOver.draw();
        }
        
       
    }
    public static EGameState getCurrentGameState() {
        return eCurrentGameState;
    }
    private void updateGameState() {

        if(eCurrentGameState.Reload) {
            eCurrentGameState.Active = !eCurrentGameState.Reload;
            eCurrentGameState.Pause = !eCurrentGameState.Reload;
            eCurrentGameState.GameOver = !eCurrentGameState.Reload;
        }
        if(eCurrentGameState.GameOver) {
            eCurrentGameState.Active = !eCurrentGameState.GameOver;
            eCurrentGameState.Pause = !eCurrentGameState.GameOver;

            return;
        }
        boolean buttonIsPressed = m_Keys[GLFW_KEY_ESCAPE] == GLFW_PRESS;
        
        // Обработка только при отпускании клавиши
        if(eCurrentGameState.buttonWasPressed && !buttonIsPressed && !eCurrentGameState.Pause) {
            eCurrentGameState.Active = !eCurrentGameState.Active;
            eCurrentGameState.Pause = !eCurrentGameState.Active;

        }

        eCurrentGameState.buttonWasPressed = buttonIsPressed;

    }
    private void updateTanks(ArrayList<Tank> tanks, float deltaTime) {
    Iterator<Tank> iterator = tanks.iterator();
    while (iterator.hasNext()) {
        Tank obj = iterator.next();
        if (obj == null || obj.isDeleted()) {
            if (obj.getUnitType() == PLAYER) {
                eCurrentGameState.GameOver = true;
                eCurrentGameState.Active = false;
                eCurrentGameState.Pause = false;
            }
            else {
            map.setEnemyQuantity(map.getEnemyQuantity()-1);
            }
            iterator.remove();
            continue;
        }
       

        if (obj.getUnitType() == PLAYER) {
            Player player = (Player) obj;
            player.getHpBar().update(animator);
            if(!obj.isDestroy())
            movementSystem.input(player, m_Keys, deltaTime);
        }
        if(obj.isMoving()) {
            animator.animateObject(obj);
        }
        if(obj.getDestroyStatus()) {
            animator.animateObject(obj);
        }

        obj.draw(deltaTime);
    }
}
private void updateWalls(ArrayList<Wall> objects, float deltaTime) {
    Iterator<Wall> iterator = objects.iterator();
    while (iterator.hasNext()) {
        Wall obj = iterator.next();
        if (obj == null || obj.isDeleted()) {
            iterator.remove();
            continue;
        }
        if(obj instanceof AnimatedObject && obj.getWallParam() == WallParam.BREAKABLE) {
            animator.animateObject((AnimatedObject)obj);
        }
        
        obj.draw(deltaTime);
    }
}
private void updateEnvironment(ArrayList<animatedGameObject> objects, float deltaTime) {
    Iterator<animatedGameObject> iterator = objects.iterator();
    while (iterator.hasNext()) {
        animatedGameObject obj = iterator.next();
        if (obj == null || obj.isDeleted()) {
            iterator.remove();
            continue;
        }
        animator.animateObject((AnimatedObject)obj);
        
        obj.draw(deltaTime);
    }
}
    public void setKey(int key, int action)
    {   
       if(key >= 0 && key < m_Keys.length)
       {
        m_Keys[key] = action;
       }
    }
    private void updateBullets(ArrayList<Bullet> bullets, float deltaTime) {
        Iterator<Bullet> iterator = bullets.iterator();
        while(iterator.hasNext()) {
           Bullet bullet = iterator.next();
           if (bullet == null || bullet.isDeleted()) {
            iterator.remove();
            continue;
        }
         if(!bullet.isDestroy()) moveController.moveBullet(bullet);
            animator.animateObject(bullet);
            bullet.draw(deltaTime);
        }
        }

    public static void startGame() {
            eCurrentGameState.Active = !eCurrentGameState.Active;
            eCurrentGameState.Pause = !eCurrentGameState.Active;
    }
    public static void exitGame() {
        eCurrentGameState.Exit = true;
    }
    public static void reloadGame() {
       eCurrentGameState.Reload = true;

    }
   private void setupResizeCallback() 
   {
    glfwSetFramebufferSizeCallback(window, (w, w_width, w_height) ->
    {
        width = w_width;
        height = w_height;
        map.resizeWindow(w_width, w_height);
    });

   }
   public void clearScene()
   {
    // Очистка танков
    for (Tank tank : map.getTanks()) {
        tank.delete();
    }
    map.getTanks().clear();

    // Очистка стен
    for (Wall wall : map.getWalls()) {
        wall.delete();
    }
    map.getWalls().clear();

    // Очистка пуль
    for (Bullet bullet : map.getBullets()) {
        bullet.delete();
    }
    map.getBullets().clear();
    for(animatedGameObject environment : map.getEnvironment()) {
        environment.delete();
    }
    map.getEnvironment().clear();
    // Очистка меню
    sceneIsEmpty = true;
   }
   public void deleteMenu() {
    mainMenu.cleanup();
    gameOver.cleanup();
   }

}
