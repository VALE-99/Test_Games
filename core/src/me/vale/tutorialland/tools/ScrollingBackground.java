package me.vale.tutorialland.tools;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.vale.tutorialland.screen.MainGameScreen;
import me.vale.tutorialland.spacegame.SpaceGame;

/* La classe ScrollingBackground ci serve per dare l'illusione che lo sfondo sia continuo, quando in realtà prendiamo la stessa
immagine due volte, e facciamo scorrere la prima e prima che finisca facciamo scorrere la seconda, in modo da dare senso di continuità
*/

public class ScrollingBackground {

    public static final int DEFAULT_SPEED = 150;
    public static final int ACCELERATION = 50;
    public static final int GOAL_REACH_ACCELLERATION = 200;

    public static Texture image;
    float y1, y2;
    int speed; //velocità in pixel / s
    int goalSpeed; //velocità massima di movimento da raggiungere
    float imageScale;
    boolean speedFixed;

    public ScrollingBackground() {

        image = new Texture("spaceBackgrondMoon.jpg");

        y1 = 0;
        y2 = image.getHeight() - 150;
        speed = 0;
        goalSpeed = DEFAULT_SPEED;
        imageScale = (float) SpaceGame.WIDTH / image.getWidth(); //serve per mantenere in proporzione la schermata di gioco
        speedFixed = true;
    }

    public void updateAndRender(float deltaTime, SpriteBatch batch) {
        //Speed adjustemnt to reach goal

        if(MainGameScreen.score > 1000) {
            image.dispose();
            image = new Texture("drawSpace.jpg");
        }


        if (speed < goalSpeed) {
            speed += GOAL_REACH_ACCELLERATION * deltaTime;
            if (speed > goalSpeed) {
                speed = goalSpeed;
            }
        } else if (speed > goalSpeed) {
            speed -= GOAL_REACH_ACCELLERATION * deltaTime;
            if (speed < goalSpeed) {
                speed = goalSpeed;
            }
        }

        if(!speedFixed){
            speed += ACCELERATION * deltaTime;
        }

        y1 -= speed * deltaTime;
        y2 -= speed * deltaTime;

               /*
               Se l'immagine è completamente fuori dallo schermo dobbiamo controllare che la "Y + image.getHeight" sia minore uguale a 0
               se l'immagine raggiunge la fine dello schermo e non è visibile, mettila in alto */

        if (y1 + image.getHeight() * imageScale  <= 0) {
            y1 = y2 + image.getHeight() * imageScale ;
        }
        if (y2 + image.getHeight() * imageScale  <= 0) {
            y2 = y1 + image.getHeight() * imageScale ;
        }

        //Render
        batch.draw(image, 0, y1, SpaceGame.WIDTH,image.getHeight() * imageScale );
        batch.draw(image, 0, y2, SpaceGame.WIDTH,image.getHeight() * imageScale );

    }

    public void setSpeed (int goalSpeed){
        this.goalSpeed = goalSpeed;
    }

    public void setSpeedFixed (boolean speedFixed){
        this.speedFixed = speedFixed;
    }

}



