package com.lukum.Manager;

import java.util.ArrayList;
import java.util.Map;

import com.Interfaces.AnimatedObject;
import com.lukum.App;
import com.lukum.Render.Sprite;
import com.lukum.gameProps.units.Player;
import com.lukum.gameProps.units.gameObject;
public class Animator 
{
   private static Animator instanceAnim;
   private Animator() {}

   public static Animator getInstance()
    {
        if(instanceAnim == null)  {
         instanceAnim = new Animator();
         }
        return instanceAnim;
    }
  public void animateObject(AnimatedObject object) 
  {
    object.getAnimation().update(App.getDeltaTime());
    
  }

}