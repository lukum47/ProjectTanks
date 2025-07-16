package com.lukum.gameProps.menu;

import static org.lwjgl.glfw.GLFW.*;

import java.rmi.server.Unreferenced;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.joml.Vector2f;
import org.lwjgl.Version.BuildType;

import com.lukum.App;
import com.lukum.Manager.Enums.ButtonAction;
import com.lukum.Manager.Enums.ScreenAlignment;
import com.lukum.Render.Animation;
import com.lukum.gameProps.GameMap;

public class MainMenu {
    protected Background background;
    protected ArrayList<Title> titles = new ArrayList<>();
    protected ArrayList<Button> buttons = new ArrayList<>();
    protected int keys[];
    protected float delay;
    protected boolean isActive;
    public MainMenu() {
        isActive = true;
        init();
    }
    protected void init() {
        this.background = new Background(ScreenAlignment.CENTER, "background.png",12, 5, 1920, 1080);
        buttons.add(new Button(ButtonAction.START_GAME, ScreenAlignment.CENTER, 70, 40, "start.png"));
        buttons.get(0).setOffset(0,170);
        buttons.add(new Button(ButtonAction.SHOW_MAPS, ScreenAlignment.BOTTOM, 100, 40, "chooseMap.png"));
        buttons.get(1).setOffset(0,-150);
        buttons.add(new Button(ButtonAction.EXIT_GAME, ScreenAlignment.BOTTOM, 100, 40, "exit.png"));
        buttons.get(2).setOffset(0, -80);
    }
    public void draw() {
        if(background != null) {
            background.draw();
        }
        for(Title it : titles) {
            it.draw();
        }
        for(Button it : buttons) {
            it.draw();
        }
    
    }
    public void processInput(int keys[]) {

        this.keys = keys;
        if(delay <= 0) {
       if(keys[GLFW_KEY_UP] == GLFW_PRESS) {
        delay = 0.2f;
        boolean hasSelected = false;
        for(int i = 0; i < this.buttons.size(); i++) {
            if(buttons.get(i).isChosed()) {
                hasSelected = true;
                if(i == 0) {
                    buttons.get(buttons.size()-1).choose();
                }
                else {
                    buttons.get(i-1).choose();
                }
                buttons.get(i).unchoose();
                break;
            }       
        }
            if(!hasSelected)
            buttons.get(0).choose();
        }
        else if(keys[GLFW_KEY_DOWN] == GLFW_PRESS) {
            delay = 0.2f;
            boolean hasSelected = false;
            for(int i = 0; i < this.buttons.size(); i++) {
                if(buttons.get(i).isChosed()) {
                    hasSelected = true;
                    if(i == buttons.size()-1) {
                        buttons.get(0).choose();
                    }
                    else {
                        buttons.get(i+1).choose();
                    }
                    buttons.get(i).unchoose();
                    break;
                }
            }
                if(!hasSelected)
                buttons.get(0).choose();
            }
        else if(keys[GLFW_KEY_ENTER] == GLFW_PRESS) {
            delay = 0.5f;
            for(int i = 0; i < this.buttons.size(); i++) {
                if(buttons.get(i).isChosed()) {
                    buttons.get(i).press();
                }
            }
        }
        }
        if (delay > 0) delay -= App.getDeltaTime();

        }
       
    public void setActive() {
        this.isActive = true;
    }
    public void deactivate() {
        this.isActive = false;
    }
    public void cleanup() {
        for(Button it : buttons) {
            it.delete();
        }
        buttons.clear();
        for(Title it : titles) {
            it.delete();
        }
        titles.clear();
        if (background != null) {
            background.delete();
        }
    }
    public Background getBackground() {
        return background;
    }

}