
package org.snake;

import java.util.ArrayList;

public class Player {

    private final int blockSize;
    private final double speed = 60.0;
    private double accumulatedTime;

    public enum Direction {
        LEFT,
        RIGHT,
        UP,
        DOWN;
    }

    private class Vector2 {
        public int x, y;
        public Vector2(int initialX, int initialY) {
            x = initialX;
            y = initialY;
        }
    }

    private ArrayList<Vector2> blockList;
    private Direction direction;

    public Player(int blockSize, int initialX, int initialY) {
        this.blockSize = blockSize;
        reset(initialX, initialY);
    }

    public void reset(int initialX, int initialY) {
        blockList = new ArrayList<Vector2>();
        blockList.add(new Vector2(initialX, initialY));
        direction = Direction.UP;
        accumulatedTime = 0.0;
    }

    public void setDirection(Direction d) {
        direction = d;
    }

    public void addBlock() {
        Vector2 lastVector = blockList.get(blockList.size() - 1);
        blockList.add(new Vector2(lastVector.x, lastVector.y));
    }

    public boolean update(Renderer renderer, double deltaTime) {
        boolean hasUpdated = false;
        accumulatedTime += deltaTime;
        if (accumulatedTime > 1.0 / speed) {
            hasUpdated = true;

            // Reset accumulatedTime
            accumulatedTime = 0.0;

            // Update tail
            for (int i = blockList.size() - 1; i > 0; i--) {
                Vector2 currentVector = blockList.get(i);
                Vector2 nextVector = blockList.get(i - 1);
                currentVector.x = nextVector.x;
                currentVector.y = nextVector.y;
            }

            // Update head
            Vector2 vector0 = blockList.get(0);
            switch (direction) {
            case UP:
                vector0.y += blockSize;
                break;
            case LEFT:
                vector0.x -= blockSize;
                break;
            case RIGHT:
                vector0.x += blockSize;
                break;
            case DOWN:
                vector0.y -= blockSize;
                break;
            }

            // Wrap head if outside bounds
            if (vector0.x < 0) {
                vector0.x = renderer.width - blockSize;
            } else if (vector0.x > renderer.width - blockSize) {
                vector0.x = 0;
            }
            if (vector0.y < 0) {
                vector0.y = renderer.height - blockSize;
            } else if (vector0.y > renderer.height - blockSize) {
                vector0.y = 0;
            }
        }
        return hasUpdated;
    }

    public void draw(Renderer renderer) {
        for (int i = 0; i < blockList.size(); i++) {
            Vector2 vector = blockList.get(i);
            renderer.setColor(0.5f, 0.5f, 1.f, 1.f);
            renderer.drawRectangle(vector.x, vector.y, blockSize, blockSize);
        }
    }

    public int getX() {
        return blockList.get(0).x;
    }

    public int getY() {
        return blockList.get(0).y;
    }

    public boolean isOverlapping() {
        boolean result = false;
        Vector2 vector0 = blockList.get(0);
        for (int i = 1; i < blockList.size() && !result; i++) {
            Vector2 vectorI = blockList.get(i);
            if (vector0.x == vectorI.x && vector0.y == vectorI.y) {
                result = true;
            }
        }
        return result;
    }

    public int size() {
        return blockList.size();
    }

}
