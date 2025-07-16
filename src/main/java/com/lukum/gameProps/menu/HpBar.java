package com.lukum.gameProps.menu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.joml.Vector2f;
import org.joml.Vector2i;

import com.Interfaces.AnimatedObject;
import com.lukum.Manager.Animator;
import com.lukum.Manager.Enums.ScreenAlignment;
import com.lukum.Manager.Enums.WallParam;
import com.lukum.Render.Render;
import com.lukum.Render.Transform;
import com.lukum.gameProps.game;
import com.lukum.gameProps.environment.Wall;
import com.lukum.gameProps.units.Tank;

public class HpBar {
    private List<Title> hpBar = new ArrayList<>();
    private Tank owner;
    private boolean barEmpty;
    private int offsetX, offsetY;
    private String atlasName;
    private int rows, cols;
    private int width, height;
    ScreenAlignment alignment;
    public HpBar(Tank owner, ScreenAlignment alignment, int Width, int Height, String atlasName, int cols, int rows, int offsetX, int offsetY) {
        this.owner = owner;
        this.alignment = alignment;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.atlasName = atlasName;
        this.rows = rows;
        this.cols = cols;
        this.width = Width;
        this.height = Height;
        Vector2i pos = new Vector2i(-300, 0);
        for(int i = 0; i < owner.getHealth(); i++) {
            Title title = new Title(alignment, Width, Height, atlasName, cols, rows, 0.02f);
            title.setOffset(pos.x, pos.y);
            title.getAnimation().stopAnimation();
            hpBar.add(title);
            pos.x += offsetX;
            pos.y += offsetY;
        }

    }
    public boolean getBarStatus() {
        return barEmpty;
    }
    public void addHeart(int heartCount) {
        if (heartCount <= 0) return;
        
        // Находим последний активный сердечек
        Title lastActive = null;
        for (int i = hpBar.size() - 1; i >= 0; i--) {
            Title t = hpBar.get(i);
            if (!t.isDeleted()) {
                lastActive = t;
                break;
            }
        }
    
        // Рассчитываем стартовую позицию
        Vector2i pos;
        if (lastActive == null) return; 
            pos = new Vector2i(lastActive.currentOffset).add(offsetX, offsetY);
        
    
        // Создаём новые сердечки
        for (int i = 0; i < heartCount; i++) {
            Title title = new Title(alignment, width, height, atlasName, cols, rows, 0.02f);
            title.setOffset(pos.x, pos.y);
            title.getAnimation().stopAnimation();
            hpBar.add(title);
            pos.add(offsetX, offsetY);
        }
    }
    public void update(Animator animator) {
        if(owner.isHitted() || owner.isDestroy()) {
            if(hpBar.size() == 0) {
                barEmpty = true;
                 return;
            }
            hpBar.get(hpBar.size()-1).destroy();
        }
         Iterator<Title> iterator = hpBar.iterator();
    while (iterator.hasNext()) {
        Title obj = iterator.next();
        if (obj == null || obj.isDeleted()) {
            iterator.remove();
            continue;
        }
        if(obj.isAnimated()) {
            animator.animateObject((AnimatedObject)obj);
        }
        obj.draw();
        if(obj.isDestroy) {
            obj.destroy();
        }
    }
    }
}
