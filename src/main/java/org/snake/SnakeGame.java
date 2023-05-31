
package org.snake;

import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;

public class SnakeGame implements Game {

    private final int blockSize = 50;
    private Random random;

    private Player player;

    private int appleX, appleY;

    private void generateNewApple(Renderer renderer) {
        appleX = random.nextInt(renderer.width / blockSize) * blockSize;
        appleY = random.nextInt(renderer.height / blockSize) * blockSize;
    }

    public void load(Renderer renderer) {
        renderer.setBackgroundColor(0.f, 0.f, 0.f, 1.f);
        player = new Player(blockSize, blockSize, blockSize);
        random = new Random();
        generateNewApple(renderer);
    }

    public void update(Renderer renderer, double deltaTime) {
        if (player.getX() == appleX && player.getY() == appleY) {
            generateNewApple(renderer);
            player.addBlock();
        }
        if (player.update(renderer, deltaTime)) {
            if (player.isOverlapping()) {
                System.out.println("Final player size: " + String.valueOf(player.size()));
                player.reset(blockSize, blockSize);
                generateNewApple(renderer);
            }
        }
    }

    public void draw(Renderer renderer) {
        // Draw apple
        renderer.setColor(1.f, 0.5f, 0.5f, 1.f);
        renderer.drawRectangle(appleX, appleY, blockSize, blockSize);
        // Draw snake
        player.draw(renderer);
    }

    public void keyPressed(Renderer renderer, int key) {
        switch (key) {
        case GLFW_KEY_UP:
            player.setDirection(Player.Direction.UP);
            break;
        case GLFW_KEY_LEFT:
            player.setDirection(Player.Direction.LEFT);
            break;
        case GLFW_KEY_RIGHT:
            player.setDirection(Player.Direction.RIGHT);
            break;
        case GLFW_KEY_DOWN:
            player.setDirection(Player.Direction.DOWN);
            break;
        case GLFW_KEY_SPACE:
            player.addBlock();
            break;
        }
    }

}
