
/* Copyright (c) 2023 Caleb Butler
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
