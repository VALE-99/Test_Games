package me.vale.tutorialland.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import me.vale.tutorialland.spacegame.SpaceGame;
import me.vale.tutorialland.entities.Asteroid;
import me.vale.tutorialland.entities.Bullet;

import java.util.ArrayList;
import java.util.Random;

public class  MainGameScreen implements Screen {

    public static final float SPEED = 300;
    public static final float SHIP_ANIMATION_SPEED = 0.5f;
    public static final int SHIP_WIDTH_PIXEL = 17;
    public static final int SHIP_HEIGHT_PIXEL = 32;

    //sono le misure dell'immagine che andremo a disegnare
    public static final int SHIP_WIDTH = SHIP_WIDTH_PIXEL * 3;
    public static final int SHIP_HEIGHT = SHIP_HEIGHT_PIXEL * 3;

    //BULLETS
    public static final float ROLL_TIMER_SWITCH_TIME = 0.25f; //tempo che si impiega tra un roll e l'altro dell'animazione
    public static final float SHOOT_WAIT_TIME = 0.3f;

    //ASTEROID
    public static final float MIN_ASTEROID_SPAWN_TIME = 0.3f; //minimo di tempo di spawn tra un asteroide e l'altro
    public static final float MAX_ASTEROID_SPAWN_TIME = 0.6f; //max tempo di spawn di un asteroide




    Animation[] rolls;

    float x;
    float y;
    int roll;
    float rollTimer; //traccia il tempo del roll
    float stateTime; //usiamo come stato generale per l'animazione.
                    // La classe animazione
    float asteroidsSpawnTimer;

    Random random;

    float shootTimer;
    SpaceGame game;

    ArrayList<Bullet> bullets;
    ArrayList<Asteroid> asteroids;

    BitmapFont scoreFont;
    int score;

    /* Quando creiamo il costruttore di MainGameScreen, passiamo "SpaceGame Game" cosi dentro questa classe siamo in
    grado di accedere alla classe principale "SpaceGame". In questo modo accediamo al batch (dobbiamo definirlo public),
    E poi impostiamo il game di questa classe (this.game), con il game che passiamo dalla classe main.

    Dobbiamo impostare il batch come game.batch, perchè il batch appartiene al game passato dalla classe main.
    */

    public MainGameScreen (SpaceGame game){

        this.game = game;
        y = 15;
        x = (float) SpaceGame.WIDTH / 2 - (float) SHIP_WIDTH / 2;
        bullets = new ArrayList<Bullet>();
        asteroids = new ArrayList<Asteroid>();
        scoreFont = new BitmapFont(Gdx.files.internal("fonts/score.fnt"));

        score = 0;
        /*
        Random.nextfloat() genera un numero random compreso tra 0 e 1
        in pratica quello che facciamo e fare un calcolo random + aggiungiamo 0.3s al risultato in modo da avere uno spawn
        randomico ogni 0.3 e 0.6 secondi.
         */
        random = new Random();
        asteroidsSpawnTimer = random.nextFloat() * (MAX_ASTEROID_SPAWN_TIME - MIN_ASTEROID_SPAWN_TIME) + MIN_ASTEROID_SPAWN_TIME;

        shootTimer = 0;

        roll = 2;
        rollTimer = 0;
        rolls = new Animation[5];

        /*
        utilizziamo un array di regioni per gestire le animazioni, altrimenti non sarebbero animazioni
        se avessimo una sola texture.
         */
        TextureRegion[][] rollSpriteSheet = TextureRegion.split(new Texture("ship.png"), SHIP_WIDTH_PIXEL, SHIP_HEIGHT_PIXEL);

        /* Costruttori: CTRL + SPACE per vedere i diversi costruttori. Quando creiamo un nuovo oggetto le varie cose
        che passiamo servono per creare l'oggetto.
         */

        rolls[0] = new Animation(SHIP_ANIMATION_SPEED, rollSpriteSheet[2]);//ANIMAZIONE VERSO SINISTRA
        rolls[1] = new Animation(SHIP_ANIMATION_SPEED, rollSpriteSheet[1]);
        rolls[2] = new Animation(SHIP_ANIMATION_SPEED, rollSpriteSheet[0]);//ANIMAZIONE CENTRALE
        rolls[3] = new Animation(SHIP_ANIMATION_SPEED, rollSpriteSheet[3]);
        rolls[4] = new Animation(SHIP_ANIMATION_SPEED, rollSpriteSheet[4]);//ANIMAZIONE VERSO DESTRA



    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        //shooting code
        shootTimer += delta;
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && shootTimer >= SHOOT_WAIT_TIME) {
            shootTimer = 0;

            int offset = 4;

            if(roll == 1 || roll == 3){ //virata leggera (sia sinistra che destra)
                offset = 8;
            }

            if(roll == 0 || roll == 4){ //virata completa (sia sinistra che destra)
                offset = 16;
            }

            bullets.add(new Bullet(x + offset, y + 40));
            bullets.add(new Bullet(x + SHIP_WIDTH - offset, y + 40));
        }

        //Asteroids Spawn Code
        //Gdx.graphics.getWidth() - Asteroid.WIDTH <- per evitare asteroidi tagliati sul bordo destro dello schermo
        asteroidsSpawnTimer -= delta;
        if(asteroidsSpawnTimer <= 0){
            asteroidsSpawnTimer = random.nextFloat() * (MAX_ASTEROID_SPAWN_TIME - MIN_ASTEROID_SPAWN_TIME) + MIN_ASTEROID_SPAWN_TIME;
            asteroids.add(new Asteroid(random.nextInt(Gdx.graphics.getWidth() - Asteroid.WIDTH)));
        }

        //Update asteroids
        ArrayList<Asteroid> asteroidsToRemove = new ArrayList<Asteroid>();
        for (Asteroid asteroid : asteroids){
            asteroid.update(delta);
            if(asteroid.remove){
                asteroidsToRemove.add(asteroid);
            }
        }


        //update bullets (loop dentro la lista bullets, per ogni bullet presente al suo interno).
        ArrayList<Bullet> bulletsToRemove = new ArrayList<Bullet>();
        for (Bullet bullet : bullets) {
            bullet.update(delta);
            if (bullet.remove) {
                bulletsToRemove.add(bullet); //se un bullet deve essere rimosso viene inserito nella lista delle rimozioni
            }
        }

        //movement code


        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            y += SPEED * Gdx.graphics.getDeltaTime();

            //bordo superiore
            if (y + SHIP_HEIGHT + 1 > Gdx.graphics.getHeight()) {
                y = Gdx.graphics.getHeight() - SHIP_HEIGHT - 1;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            y -= SPEED * Gdx.graphics.getDeltaTime();

            if (y < 0) {
                y = 0;
            }
        }

// movimento verso sinistra


        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            x -= SPEED * Gdx.graphics.getDeltaTime();

            //bordo sinistro
            if (x < 0) {
                x = 0;
            }

            //aggiorniamo la virata a sinistra se siamo ancora in virata verso destra (movimento più fluido e naturale)
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.RIGHT) && roll > 0) {
                rollTimer = 0;
                roll--;
            }

            //Update roll
                /*
                controlliamo che rollTimer sia maggiore del Roll_timer_switch_time,
                ovvero se il tempo in cui il giocatore decide di mantenere premuto il
                tasto destro o sinistro è maggiore nel tempo che abbiamo impostato per lo switch
                avverà l'animazione del verso in cui ci stiamo spostando.
                 */
            rollTimer -= Gdx.graphics.getDeltaTime();
            if (Math.abs(rollTimer) > ROLL_TIMER_SWITCH_TIME && roll > 0) {
                rollTimer -= ROLL_TIMER_SWITCH_TIME;
                roll--;
            }
        } else {
            if (roll < 2) {
                rollTimer += Gdx.graphics.getDeltaTime();
                if (Math.abs(rollTimer) > ROLL_TIMER_SWITCH_TIME && roll < 4) {
                    rollTimer -= ROLL_TIMER_SWITCH_TIME;
                    roll++;

                }
            }
        }


// movimento verso destra
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            x += SPEED * Gdx.graphics.getDeltaTime();


            //bordo destro
            //Gdx.graphics.getWidth() <- screen width
            if (x + SHIP_WIDTH > Gdx.graphics.getWidth()) {
                x = Gdx.graphics.getWidth() - SHIP_WIDTH;
            }

            //aggiorniamo la virata a sinistra se siamo ancora in virata verso destra (movimento più fluido e naturale)
            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) && !Gdx.input.isKeyPressed(Input.Keys.LEFT) && roll > 0) {
                rollTimer = 0;
                if(roll < 4){
                roll++;
                }
            }

            //update roll
            rollTimer += Gdx.graphics.getDeltaTime();
            if (Math.abs(rollTimer) > ROLL_TIMER_SWITCH_TIME && roll < 4) {
                rollTimer -= ROLL_TIMER_SWITCH_TIME;
                roll++;
            }
            /*
            se roll è < 2 stiamo ancora virando verso sinistra.
             */
        }
        else {
            if (roll > 2) {
                rollTimer -= Gdx.graphics.getDeltaTime();
                if (Math.abs(rollTimer) > ROLL_TIMER_SWITCH_TIME && roll > 0) {
                    rollTimer -= ROLL_TIMER_SWITCH_TIME;
                    roll--;
                }
            }
        }

        /* Dopo gli update di ogni oggetto, controlliamo le collisioni
           Eseguiamo un loop innestato, in modo che controlliamo se un proiettile ha una collisione con un
           qualsiasi proiettile presente nello schermo di gioco.
         */
        for(Bullet bullet : bullets){
            for (Asteroid asteroid : asteroids){
                if(bullet.getCollisionReact().collidesWith(asteroid.getCollisionReact())){ //avviene una collisione
                    bulletsToRemove.add(bullet);
                    asteroidsToRemove.add(asteroid);
                    score += 100;
                }
            }
        }
        asteroids.removeAll(asteroidsToRemove); //rimuoviamo tutti gli asteroidi presenti nell' ArrayList da rimuovere
        bullets.removeAll(bulletsToRemove); //rimuoviamo tutti i proiettili presenti nell' ArrayList da rimuovere

        stateTime += delta;

        // il rendering funziona a layer, quindi l'ultima cosa che verrà reinderizzata sarà sopra le altre
        ScreenUtils.clear(0, 0, 0, 1);
        game.batch.begin();

        GlyphLayout scoreLayout = new GlyphLayout(scoreFont, "" + score);
        scoreFont.draw(game.batch, scoreLayout, Gdx.graphics.getWidth() / 2 - scoreLayout.width / 2, Gdx.graphics.getHeight() - scoreLayout.height - 10);

        for (Bullet bullet : bullets) {
            bullet.render(game.batch);
        }

        for (Asteroid asteroid : asteroids){
            asteroid.render(game.batch);
        }

        game.batch.draw((TextureRegion) rolls[roll].getKeyFrame(stateTime, true), x, y, SHIP_WIDTH, SHIP_HEIGHT);

        game.batch.end();


    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
