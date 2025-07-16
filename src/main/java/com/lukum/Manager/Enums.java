package com.lukum.Manager;
import static com.lukum.Manager.Enums.UnitType.*;

import java.util.Random;

import org.joml.Vector2f;
import org.joml.Vector2i;


import com.lukum.Render.Render;
import com.lukum.gameProps.game;
import com.lukum.gameProps.environment.HP;
import com.lukum.gameProps.environment.Wall;
import com.lukum.gameProps.units.Bullet;
import com.lukum.gameProps.units.Player;
import com.lukum.gameProps.units.Tank;
import com.lukum.gameProps.units.animatedGameObject;
import com.lukum.gameProps.units.gameObject;

public class Enums 
{
    
    public enum UnitType {
        PLAYER {

            @Override
            public gameObject create(float posX, float posY, int tileSize) {
                 Player.initialize( "PLAYER.png" , 8, 1, posX, posY, tileSize);
                 return Player.getInstance();
            }
        }, 
        ENEMY {

            @Override
            public gameObject create(float posX, float posY, int tileSize) {
                Tank tank = new Tank(UnitType.ENEMY, "ENEMY.png" , 8, 1,posX, posY, tileSize);
                return tank;
            }
        }, 
        ALLY {

            @Override
            public gameObject create(float posX, float posY, int tileSize) {
                return null;
            }
        }, 
        BRICK_WALL {

            @Override
            public gameObject create(float posX, float posY, int tileSize) {
                return new Wall("BLOCK.png", 3, 1, BRICK_WALL, WallParam.BREAKABLE, posX, posY, tileSize);
            }
        },
        ARMOR_WALL {

            @Override
            public gameObject create(float posX, float posY, int tileSize) {
                return new Wall("STEEL.png", ARMOR_WALL, WallParam.UNBREAKABLE, posX, posY, tileSize);
            }
        },
        WATER {

            @Override
            public gameObject create(float posX, float posY,int tileSize) {
                return new animatedGameObject(WATER, posX, posY, tileSize, "water.png", 3, 3, false, false, 0.2f);
            }
        },
        BUFF {

            @Override
            public gameObject create(float posX, float posY,int tileSize) {
                return new animatedGameObject(BUFF, posX, posY, tileSize, "buff.png", 8, 4, true, false, 0.03f);
            }
        },
        SMOKE {

            @Override
            public gameObject create(float posX, float posY,int tileSize) {
                return new animatedGameObject(SMOKE, posX, posY, tileSize, "smoke.png", 7, 2, true, false, 0.06f);
            }
        },
        HP {

            @Override
            public gameObject create(float posX, float posY,int tileSize) {
                HpType type = HpType.processPointType();
              return type.create(posX, posY, tileSize);
            }
        },
        
        SPARK {

            @Override
            public gameObject create(float posX, float posY,int tileSize) {
                return new animatedGameObject(SPARK, posX, posY, tileSize, "spark.png", 5, 2, true, true, 0.06f);
            }
        },
        EMPTY {

            @Override
            public gameObject create(float posX, float posY,int tileSize) {
                return null;
            }
        };

        public abstract gameObject create(float posX, float posY, int tileSize);
    }
    public enum movementDirection {
        UP
        {
            @Override
            public movementDirection getOppositeDirection() {
                return DOWN;
            }
            @Override 
            public Vector2i getOffsetByDirection() {
                return new Vector2i(0, -1);
            }
        },     
        DOWN 
        {
            @Override
            public movementDirection getOppositeDirection() {
                return UP;
            }
            @Override 
            public Vector2i getOffsetByDirection()  {
                return new Vector2i(0, 1);
            }
        },  
        LEFT
        {
            @Override
            public movementDirection getOppositeDirection() {
                return RIGHT;
            }
            @Override 
            public Vector2i getOffsetByDirection()  {
                return new Vector2i(-1, 0);
            }
        },  
        RIGHT  
        {
            @Override
            public movementDirection getOppositeDirection() {
                return LEFT;
            }
            @Override 
            public Vector2i getOffsetByDirection() {
                return new Vector2i(1, 0);
            }
        };
        public abstract movementDirection getOppositeDirection();
        public abstract Vector2i getOffsetByDirection();
    }
    public enum WallParam {
        BREAKABLE, 
        UNBREAKABLE
    }
    /**
     * enum класс для определения расположения текста и кнопок в окне
     * так-же в самом классе кнопки или текста можно устанавливать смещения
     * от этих позиций в пикселях
     *  */ 
    public enum ScreenAlignment {
        // Горизонтальные позиции
        LEFT(0.0f, 0.5f),
        CENTER(0.5f, 0.5f),
        RIGHT(1.0f, 0.5f),
    
        // Вертикальные позиции
        TOP(0.5f, 1.0f),
        BOTTOM(0.5f, 0.0f),
    
        // Комбинированные
        TOP_LEFT(0.0f, 1.0f),
        TOP_RIGHT(1.0f, 1.0f),
        BOTTOM_LEFT(0.0f, 0.0f),
        BOTTOM_RIGHT(1.0f, 0.0f);
    
        private final float xFactor;
        private final float yFactor;
    
        ScreenAlignment(float xFactor, float yFactor) {
            this.xFactor = xFactor;
            this.yFactor = yFactor;
        }
    
        public float getX(int screenWidth) {
            return xFactor * screenWidth;
        }
    
        public float getY(int screenHeight) {
            return (1.0f - yFactor) * screenHeight; // Инверсия Y для OpenGL
        }
    }
    /**
     * enum класс определяющий дествия кнопок в mainMenu
     * связан напрямую со статическими методами в классе game 
     */
    public enum ButtonAction {
        START_GAME {
            @Override
            public void processAction() {
                game.startGame();
            }
        }, 
        SHOW_MAPS {
            @Override
            public void processAction() {
                
            }
        },
        RESTART {
            @Override
            public void processAction() {
                game.reloadGame();
            }
        },
        EXIT_GAME {
            @Override
            public void processAction() {
                game.exitGame();
            }
        };
        public abstract void processAction();
    }
    public enum behaviourState {
        ATTACK,
        FALLBACK,
        ORDINARY
    }
    public enum HpType {
        RED {
            @Override
            HP create(float posX, float posY,int tileSize) {
                if(redRender == null) {
                    redRender = new Render(tileSize, "Red.png", 8, 8);
                    redRender.initShader("Simple");
                }
                else if(destroyRedRender == null) {
                    destroyRedRender = new Render(32, "Red.pngR", 8, 8);
                    destroyRedRender.initShader("Simple");
                }
              
                HP obj = new HP(redRender, RED, posX, posY, tileSize,false, true, 0.02f);
                obj.setPhysic(true);
                obj.setDestroyRender(destroyRedRender);
                return obj;
            }
        },
        SILVER {
            @Override
            HP create(float posX, float posY,int tileSize) {
                if(silverRender == null) {
                    silverRender = new Render(tileSize, "Red.png", 8, 8);
                    silverRender.initShader("Simple");
                }
                else if(destroySilverRender == null) {
                    destroySilverRender = new Render(32, "Red.pngR", 8, 8);
                    destroySilverRender.initShader("Simple");
                }
                HP obj = new HP(silverRender, SILVER, posX, posY, tileSize,false, true, 0.02f);
                obj.setPhysic(true);
                obj.setDestroyRender(destroySilverRender);
                return obj;
            }
        };

        abstract HP create(float posX, float posY,int tileSize);
        public static void preloadHP() {
            redRender = new Render(32, "Red.png", 8, 8);
            redRender.initShader("Simple");
            destroyRedRender = new Render(32, "Red.pngR", 8, 8);
            destroyRedRender.initShader("Simple");
            silverRender = new Render(32, "Silver.png", 8, 8);
            silverRender.initShader("Simple");
            destroySilverRender = new Render(32, "Silver.pngR", 8, 8); 
            destroySilverRender.initShader("Simple");
        }
        static Render redRender;
        static Render destroyRedRender;
        static Render silverRender;
        static Render destroySilverRender;
       static HpType processPointType() {
            Random rand = new Random();
            int num = rand.nextInt(10);
            if(num <= 7) return RED;
            else return SILVER;
        }
    }
}
