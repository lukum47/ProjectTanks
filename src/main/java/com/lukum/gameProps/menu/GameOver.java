package com.lukum.gameProps.menu;

import java.util.ArrayList;

import com.lukum.Manager.Enums.ButtonAction;
import com.lukum.Manager.Enums.ScreenAlignment;

public class GameOver extends MainMenu{
    public GameOver() {
        super();
    }
    @Override 
    protected void init() {
        background = new Background(ScreenAlignment.CENTER, "background.png",12, 5, 1920, 1080);
        titles.add(new Title(ScreenAlignment.CENTER, 350, 290, "gameOver.png", 13, 6, 0.03f));
         buttons.add(new Button(ButtonAction.RESTART, ScreenAlignment.CENTER, 70, 40, "start.png"));
         buttons.get(0).setOffset(0,220);
         buttons.add(new Button(ButtonAction.EXIT_GAME, ScreenAlignment.BOTTOM, 100, 40, "exit.png"));
         buttons.get(1).setOffset(0, -80);
    }
  
    public ArrayList<Title> getTitles() {
        return titles;
    }
}
