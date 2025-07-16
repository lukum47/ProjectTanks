package com.lukum.Manager;

import static com.lukum.Manager.Enums.UnitType.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.joml.Vector2f;
import org.joml.Vector2i;

import com.lukum.App;
import com.lukum.Manager.Enums.HpType;
import com.lukum.Manager.Enums.ScreenAlignment;
import com.lukum.Manager.Enums.UnitType;
import com.lukum.Render.Render;
import com.lukum.gameProps.GameMap;
import com.lukum.gameProps.game;
import com.lukum.gameProps.environment.HP;
import com.lukum.gameProps.menu.Counter;
import com.lukum.gameProps.menu.Title;
import com.lukum.gameProps.units.Bullet;
import com.lukum.gameProps.units.Tank;
import com.lukum.gameProps.units.animatedGameObject;
import com.lukum.gameProps.units.gameObject;

public class Respawn {
    private GameMap map;
    private ArrayList<Vector2i> spawnPoints;
    private static Respawn instance;
    private  float timer;
    private int tankNeedToResp = 0;
    private  float HPTimer;
    private int numOfTanks;
    private Title tankIcon;
    private int numOfAliveTanks;
    private Counter tankCounter;
    private Respawn(GameMap map) {
        this.map = map;
        timer = 45;
        HPTimer = 2;
        spawnPoints = map.getSpawnPoints();
        numOfTanks = map.getEnemyQuantity();
        numOfAliveTanks = map.getTanks().size()-1;
        tankNeedToResp = numOfTanks - numOfAliveTanks;
       
        
        setTankCounter();
    }
    public void reset() {
        timer = 45;
        HPTimer = 2;
        spawnPoints = map.getSpawnPoints();
        numOfTanks = map.getEnemyQuantity();
        numOfAliveTanks = map.getTanks().size()-1;
        tankNeedToResp = numOfTanks - numOfAliveTanks;
        tankCounter.setCurrentNumber(numOfTanks);
    }
    private void setTankCounter() {


        tankIcon = new Title(ScreenAlignment.TOP_RIGHT, 32, 32, "tankIcon.png");
        tankIcon.setOffset(-300, +250);
        Vector2f digitPlacement = new Vector2f(tankIcon.getTransform().getPosition().x + 75, tankIcon.getTransform().getPosition().y);
        tankCounter = new Counter(digitPlacement.x, digitPlacement.y, map.getTileSize()*2, 2);
        tankCounter.setCurrentNumber(numOfTanks);
    }
    public static void initialize(GameMap map) {

        instance = new Respawn(map);
    }

    public static Respawn getInstance() {
        return instance;
    }
    public void update()
     {
        if(map.getEnemyQuantity() < numOfTanks) {
            tankCounter.decrease();
            numOfTanks = map.getEnemyQuantity();
            numOfAliveTanks--;
        }
        tankIcon.draw();
        tankCounter.draw();
        if(timer <= 0) {
        if(numOfTanks > 0 && tankNeedToResp > 0) {
            if((numOfAliveTanks + spawnPoints.size()) < 5) {
          if(tankNeedToResp == 1) {
            tankNeedToResp = 0;
            processRespawn(1);
            numOfAliveTanks++;
          }
          else {
            processRespawn(2);
            tankNeedToResp -=2;
            numOfAliveTanks+=2;
        }
          processSpawnCooldown();
        }
    }
    }
    else {
        timer -= App.getDeltaTime();
    }
    if(HPTimer <= 0) {
        Vector2i coords = getRandomEmptyCell();
        Vector2f worldCoords = map.gridToWorldCentered(coords.x, coords.y);
        animatedGameObject hp = (animatedGameObject)UnitType.HP.create(worldCoords.x, worldCoords.y, map.getTileSize());

        hp.setCurrentGridPos(coords.x, coords.y);
       
        map.addEnvironment(hp);
        processHPCooldown();
    }
    else {
        HPTimer -= App.getDeltaTime();
    }
    if(map.getTanks().size() == 0 && map.getEnemyQuantity() != 0) {
        if(numOfTanks > 0 && tankNeedToResp > 0) {
            if(numOfAliveTanks<numOfTanks) {
          tankNeedToResp = tankNeedToResp - spawnPoints.size();
          if(tankNeedToResp == -1) {
            tankNeedToResp = 0;
            processRespawn(1);
            numOfAliveTanks++;
          }
          else {
            processRespawn(2);
            numOfAliveTanks+=2;
        }
          processSpawnCooldown();
        }
    }
}
    }
    private void processSpawnCooldown() {
        Random rand = new Random();
        timer = rand.nextInt(45, 60);

    }
    private void processHPCooldown() {
        Random rand = new Random();
        HPTimer = rand.nextInt(35, 50);
    }
    private Vector2i getRandomEmptyCell() {
        Random rand = new Random();
        int x, y;
        while(true) {
         x = rand.nextInt(map.getDx()-1);
         y = rand.nextInt(map.getDy()-1);
        if(map.isCellEmpty(x, y)) {
            return new Vector2i(x,y);
        }
    }
    }
  
    private void processRespawn(int numOfTanks) {
        int num = numOfTanks;
        for(Vector2i it : spawnPoints) {
            if(num == 0) return;
            if(map.isCellEmpty(it.x, it.y)) {
                map.addGameObject(ENEMY, it.x, it.y);
                gameObject tank = map.getTile(it.x, it.y).getObject();
                Vector2f worldCoords = map.gridToWorldCentered(it.x, it.y);
                tank.deactivate();
                animatedGameObject obj = (animatedGameObject)(SMOKE.create(worldCoords.x, worldCoords.y, map.getTileSize()));
                obj.addRelateObject(tank);
                obj.getTransform().scale(0.2f, 0.2f, map.getTileSize());
                map.addEnvironment(obj);
                num--;
            }
        }
    }
    public void cleanup() {

    }
}
