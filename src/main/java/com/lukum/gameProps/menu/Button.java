package com.lukum.gameProps.menu;

import org.joml.Vector4f;

import com.lukum.Manager.Enums.ButtonAction;
import com.lukum.Manager.Enums.ScreenAlignment;

public class Button extends Title{
    private boolean isChosed;
    private boolean isPressed;
    private ButtonAction action;
    private Vector4f highlightColor = new Vector4f(0.8f, 0.3f, 0.5f, 0.3f); // Желтый с прозрачностью
    private float highlightIntensity = 0.5f; // Интенсивность подсветки
    public Button(ButtonAction action, ScreenAlignment alignment, int Width, int Height, String textureName) {
        super(alignment, Width, Height, textureName);
        this.action = action;

    }
    public boolean isPressed() {
        return isPressed;
    }
    public void press() {
        isPressed = true;
        action.processAction();
    }
    public void release() {
        isPressed = false;
    }
    public boolean isChosed() {
        return isChosed;
    }
    public void choose() {
        isChosed = true;
        setHighlight(isChosed);
    }
    public void unchoose() {
        isChosed = false;
        setHighlight(isChosed);
    }
    private void setHighlight(boolean enable) {
        renderer.setHighlightColor(enable ? highlightColor : new Vector4f(0, 0, 0, 0));
        renderer.setHighlightIntensity(enable ? highlightIntensity : 0.0f);
    }
}
