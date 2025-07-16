package com.lukum;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import java.nio.*;
import java.util.Vector;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;
import com.lukum.gameProps.game;

public class App {  
    private static String sourceDir;
	private static String shaderSource;
	private static String textureSource;
	private static String tileSetSource;
	private static float deltaTime;
	private long window;
	private game g_game = new game();
	public static String getShaderPath() {return shaderSource;}
	public static String getTexturePath() {return textureSource;}
	public static String getSourceDir() {return sourceDir;}
    public static String getTileSetPath() {return tileSetSource;}

    
 	public void run() {
        
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");

		init();
		loop();

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
    
	private void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		
        // Настройка окна
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

		// Create the window
		window = glfwCreateWindow(1920, 1080, "Hello World!", NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");
			
		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> 
            {
				g_game.setKey(key, action);
            });

		 // Центрирование окна
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			glfwGetWindowSize(window, pWidth, pHeight);
            
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		} 
		

        // Настройка контекста OpenGL
        glfwMakeContextCurrent(window);
        glfwSwapInterval(1); // Вертикальная синхронизация
        glfwShowWindow(window);

        GL.createCapabilities();
     
	}

	private void loop() {
		
		GL.createCapabilities();
		

		// Очистка фона черным цветом
		glClearColor(0.f,0.f,0.f,0.f);
		//Включаем смешивание
		glEnable(GL_BLEND);
		//Будет отсекать полупрозрачный цвет
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		g_game.init(window);
		double currenTime, lastTime = 0;
		
		// Основной цикл рендеринга
		while ( !glfwWindowShouldClose(window) ) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // очистка кадрового буфера 
			currenTime = glfwGetTime();
			deltaTime = (float) (currenTime - lastTime);
			lastTime = currenTime; 
             // Отрисовка
    
			g_game.update(deltaTime);

            // Обновление буферов
			glfwSwapBuffers(window); 
			glfwPollEvents();

			if(game.getCurrentGameState().Exit) { // Если активен флаг выхода то закрываем окно и очищаем сцену 
				glfwSetWindowShouldClose(window, true);
				if(!g_game.sceneIsEmpty) {
					g_game.clearScene();
				}
					g_game.deleteMenu();
			} 
		}
	}
	public static float getDeltaTime() 
	{
		return deltaTime;
	}
    public static void main(String[] args) {
         App.sourceDir = System.getProperty("user.dir");
         App.shaderSource = App.sourceDir + "/res/shader/";
		 App.textureSource = App.sourceDir + "/res/Textures/";
		 App.tileSetSource = App.sourceDir + "/res/mapTileSets/";
        new App().run();
    }
    
}
