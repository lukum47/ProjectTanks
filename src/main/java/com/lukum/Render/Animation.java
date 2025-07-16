package com.lukum.Render;

import java.util.ArrayList;
import java.util.Map;

import com.Interfaces.AnimatedObject;
import com.lukum.Manager.Animator;


import com.lukum.gameProps.game;
import com.lukum.gameProps.units.gameObject;


public class Animation 
{
    private float frameTime;
    private float timer;
    private boolean isLooped;
    private int frameNums;
    private int currentLine;
    private int lineNums;
    private int currentFrame;
    private boolean lastFrame;
    private boolean lastFrameStop;
    private Render renderer;
   public Animation(AnimatedObject target, float frameTime) 
   {
    this.frameTime = frameTime;
    this.renderer = target.getRender();
    this.frameNums = renderer.getAtlas().getCols();
    this.lineNums = renderer.getAtlas().getRows();
    setCurrentLine(0);
    startAnimation();
   }
   public void setCurrentLine(int line)
   {
    this.currentLine = line;
   }
   public boolean isFrameLast() {
        return lastFrame;
   }
   public void updateAtlas() {
    this.frameNums = renderer.getAtlas().getCols();
    this.lineNums = renderer.getAtlas().getRows();
    setCurrentLine(0);
    currentFrame = 0;
   }
   public void updateRender(Render render) {
    renderer = render;
    this.frameNums = renderer.getAtlas().getCols();
    this.lineNums = renderer.getAtlas().getRows();
    resetAnimation();
   }
    public void update(float deltaTime)
    {
        if(isLooped) {
        timer += deltaTime;
        if(timer >= frameTime) 
        {
            timer = 0;
            nextFrame();
        }
    }
    }
    public void setFrameTime(float frameTime) {
        this.frameTime = frameTime;
    }
    public void resetAnimation() {
        currentFrame = 0;
        currentLine = 0;
        lastFrame = false;
        timer = 0;
        startAnimation();
    }
    public void stopAtLastFrame(boolean condition) {
        lastFrameStop = condition;
    }
    private void nextFrame() 
    {
        if(currentFrame == frameNums-1) {

            nextLine();
        }
        currentFrame = (currentFrame + 1) % frameNums;
        setCurrentFrame();
        if(currentFrame == frameNums-1) {
            if(currentLine == lineNums-1) {
                if(lastFrameStop) stopAnimation();
                lastFrame = true;
                
            }
        }
    }
    public void changeFrameManually() {
        if(currentFrame == frameNums-1) {
            return;
        }
        else nextFrame();
    }
    public void changeLineManually() {
        if(currentLine == lineNums-1) {
            return;
        }
        else nextLine();
    }
    private void nextLine() {
        if(currentLine < lineNums-1)
        currentLine++;
        else {
        currentLine = 0;
        lastFrame = false;
        }
    }
    private void setCurrentFrame() 
    {
        renderer.setSprite(currentLine, currentFrame);
    }
    public boolean isLooped() {
        return isLooped;
    }

    public void startAnimation()
    {
        isLooped = true;
       
    }
    public void stopAnimation()
    {
        isLooped = false;
    }
}