
package org.snake;

public interface Game {

    public void load(Renderer renderer);
    public void update(Renderer renderer, double deltaTime);
    public void draw(Renderer renderer);
    public void keyPressed(Renderer renderer, int key);

}
