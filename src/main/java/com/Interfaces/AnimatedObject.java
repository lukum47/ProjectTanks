package com.Interfaces;

import com.lukum.Render.Animation;
import com.lukum.Render.Render;
/**
 * Интерфейс для анимированных объектов
 * анимированные объекты включают как спрайтовые 
 * анимации, так и геометрические(повороты танка)
*/
public interface AnimatedObject {
    public Animation getAnimation(); //получить экземпляр класса Animation из объекта
    public Render getRender();
    public void initAnimation(float frameTime); //иницализация анимации
  
}
