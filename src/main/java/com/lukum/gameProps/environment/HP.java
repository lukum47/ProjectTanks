package com.lukum.gameProps.environment;

import com.lukum.App;
import com.lukum.Manager.Enums.HpType;
import com.lukum.Manager.Enums.UnitType;
import com.lukum.Render.Render;
import com.lukum.gameProps.units.animatedGameObject;

public class HP extends animatedGameObject {
    private HpType hpType;
    private float destroyTime = 15;
    public HP(Render render, HpType hpType, float worldX, float worldY, int tileSize,
            boolean isTerminatable, boolean ceepAlive, float frameTime) {
        super(render, UnitType.HP, worldX, worldY, tileSize, isTerminatable, ceepAlive, frameTime);
                this.hpType = hpType;
                transform.scale(0.04f, 0.04f, tileSize);
    }
    public void updateDestroyTime() {
        destroyTime -= App.getDeltaTime();
        if(destroyTime <= 0) {
            startDestroy();
        }
    }
    public HpType getHPType() {
        return hpType;
    }
    @Override
    public void draw(float deltaTime) {
        updateDestroyTime();
        super.draw(deltaTime);
    }
}   
