package com.lukum.gameProps.menu;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Vector2f;
import org.joml.Vector2i;

import com.lukum.App;
import com.lukum.Manager.Enums.ScreenAlignment;
import com.lukum.Render.Atlas;
import com.lukum.Render.Render;
import com.lukum.Render.Sprite;

public class Counter {
private String textureAtlasName = "digit.png";
private Vector2f position;
private int value;
Atlas digitAtlas;
private Map<String, Vector2i> digits = new HashMap<>();
private int tileSize;
private ArrayList<Title> digitLine = new ArrayList<>();
public Counter(float worldCoordX, float worldCoordY, int tileSize, int numOfDigigts) {
    this.tileSize = tileSize;
    digitAtlas = new Atlas(App.getTexturePath() + "/" + textureAtlasName, 5, 2);
    int digitCounter = 0;
    for(int i = 0; i < digitAtlas.getRows(); i++) {
        for(int j = 0; j < digitAtlas.getCols(); j++) {
            digits.put(String.valueOf(digitCounter), new Vector2i(i,j));
            digitCounter++;
        }
    }
    position = new Vector2f(worldCoordX, worldCoordY);
    placeDigits(numOfDigigts);
    value = 0;
}
private void placeDigits(int numOfDigigts) {
    Vector2f pos = new Vector2f(position.x, position.y);
    for(int i = 0; i < numOfDigigts; i++) {
        Render render = new Render(tileSize, digitAtlas);
        render.initShader("Simple");
        Title title = new Title(pos.x, pos.y, render, true);
            //title.getAnimation().stopAnimation();
            digitLine.add(title);
            pos.x += tileSize;
        }
}
public void resetDigits() {
    for(Title it:digitLine) {
        Vector2i digitCoords = digits.get("0");
        it.getRender().setSprite(digitCoords.x, digitCoords.y);
    }
    value = 0;
}
public void increase() {
    value++;
    setCurrentNumber(value);
}
public void decrease() {
    value--;
    if(value<0) {
        value = 0;
    }
    setCurrentNumber(value);
}
public void setCurrentNumber(int num) {
    if(num == 0) {
        resetDigits();
        return;
    }
    value = num;
    Deque<Integer> stack = new ArrayDeque<>();
    int temp = num;
    while(temp > 0) {
        stack.push(temp%10);
        temp = temp/10;
    }
    if(stack.size() > digitLine.size()) {
        int indx = digitLine.size()-1;
        while (indx >= 0) {
            Vector2i tileCoords = digits.get(String.valueOf(0));
            digitLine.get(indx).getRender().setSprite(tileCoords.x, tileCoords.y);
            indx--;
        }
        return;
    }
    int indx = digitLine.size()-1;
    while (!stack.isEmpty()) {
        temp = stack.pop();
        Vector2i tileCoords = digits.get(String.valueOf(temp));
        digitLine.get(indx).getRender().setSprite(tileCoords.x, tileCoords.y);
        indx--;
    }
   
}
public void draw() {
    for(Title it : digitLine) {
        it.draw();
    }
}
}
