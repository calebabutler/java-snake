
package org.snake;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33C.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Renderer {

    public final int width, height;

    private final Game game;

    private long window;
    private int shaderProgram;
    private int positionLocation;
    private int colorLocation;
    private boolean isRunning;

    private float backgroundR, backgroundG, backgroundB, backgroundA;

    public Renderer(Game game, int width, int height) {
        this.game = game;
        this.width = width;
        this.height = height;
        isRunning = false;
    }

    public void run() {
        isRunning = true;
        init();
        loop();
        glDeleteProgram(shaderProgram);
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
        isRunning = false;
    }

    public void drawTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {
        if (isRunning) {
            float[] vertices = {
                ((float) x1) / ((float) width) * 2.f - 1.f,
                ((float) y1) / ((float) height) * 2.f - 1.f,
                ((float) x2) / ((float) width) * 2.f - 1.f,
                ((float) y2) / ((float) height) * 2.f - 1.f,
                ((float) x3) / ((float) width) * 2.f - 1.f,
                ((float) y3) / ((float) height) * 2.f - 1.f
            };

            glUseProgram(shaderProgram);

            int vao = glGenVertexArrays();
            int vbo = glGenBuffers();

            glBindVertexArray(vao);
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_STREAM_DRAW);
    
            glVertexAttribPointer(positionLocation, 2, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(0);

            glDrawArrays(GL_TRIANGLES, 0, 3);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);

            glDeleteBuffers(vbo);
            glDeleteVertexArrays(vao);
        } else {
            System.err.println("Cannot draw when the renderer is not running.");
        }
    }

    public void drawRectangle(int x, int y, int w, int h) {
        drawTriangle(x, y, x + w, y, x, y + h);
        drawTriangle(x + w, y, x, y + h, x + w, y + h);
    }

    public void setColor(float r, float g, float b, float a) {
        if (isRunning) {
            glUseProgram(shaderProgram);
            glUniform4f(colorLocation, r, g, b, a);
        } else {
            System.err.println("Cannot draw when the renderer is not running.");
        }
    }

    public void setBackgroundColor(float r, float g, float b, float a) {
        backgroundR = r;
        backgroundG = g;
        backgroundB = b;
        backgroundA = a;
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

        window = glfwCreateWindow(width, height, "Snake", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create GLFW window");
        }

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) {
                game.keyPressed(this, key);
            }
        });

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);

        GL.createCapabilities();
        System.err.println("GL_VENDOR: " + glGetString(GL_VENDOR));
        System.err.println("GL_RENDERER: " + glGetString(GL_RENDERER));
        System.err.println("GL_VERSION: " + glGetString(GL_VERSION));

        glViewport(0, 0, 1000, 1000);

        final String vertexShaderSource
            = "#version 330 core\n"
            + "in vec2 position;"
            + "void main() {"
            + "gl_Position = vec4(position, 0.0, 1.0);"
            + "}";

        final int vertexShader = glCreateShader(GL_VERTEX_SHADER);

        glShaderSource(vertexShader, vertexShaderSource);
        glCompileShader(vertexShader);

        final String fragmentShaderSource
            = "#version 330 core\n"
            + "uniform vec4 color;"
            + "out vec4 FragColor;"
            + "void main() {"
            + "FragColor = color;"
            + "}";

        final int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);

        glShaderSource(fragmentShader, fragmentShaderSource);
        glCompileShader(fragmentShader);

        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        positionLocation = glGetAttribLocation(shaderProgram, "position");
        colorLocation = glGetUniformLocation(shaderProgram, "color");

        glUseProgram(shaderProgram);
        glUniform4f(colorLocation, 1.0f, 1.0f, 1.0f, 1.0f);

        backgroundR = 0.5f;
        backgroundG = 0.5f;
        backgroundB = 0.5f;
        backgroundA = 1.0f;
    }

    private void loop() {
        double currentTime = 0.0;
        double lastTime = 0.0;
        double deltaTime = 0.0;
        game.load(this);
        while (!glfwWindowShouldClose(window)) {
            currentTime = glfwGetTime();
            deltaTime = currentTime - lastTime;
            lastTime = currentTime;
            game.update(this, deltaTime);
            glClearColor(backgroundR, backgroundG, backgroundB, backgroundA);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            game.draw(this);
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

}
