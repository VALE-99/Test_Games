package me.vale.tutorialland.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import me.vale.tutorialland.entities.*;

import me.vale.tutorialland.spacegame.SpaceGame;
import me.vale.tutorialland.tools.CollisionReact;

import java.util.ArrayList;
import java.util.Random;


public class  MainGameScreen implements Screen {

    public static final float SPEED = 400;
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

    //HEAL
    public static final float MIN_HEAL_SPAWN_TIME = 8.5f; //minimo di tempo di spawn tra un asteroide e l'altro
    public static final float MAX_HEAL_SPAWN_TIME = 10.5f; //max tempo di spawn di un asteroide

    //REVERSE
    public static final float MIN_REVERSE_SPAWN_TIME = 7f; //minimo di tempo di spawn tra un reverse e l'altro
    public static final float MAX_REVERSE_SPAWN_TIME = 12f; //max tempo di spawn di un reverse
    public static final float REVERSE_WAIT_TIME = 15;

    //Shield
    public static final float MIN_SHIELD_SPAWN_TIME = 15f; //minimo di tempo di spawn tra uno shield e l'altro
    public static final float MAX_SHIELD_SPAWN_TIME = 20.5f; //max tempo di spawn di un shield
    public static final float SHIELD_WAIT_TIME = 6.5f;

    public static final int PLAYERSHIELD_WIDTH = 150;
    public static final int PLAYERSHIELD_HEIGHT = 150;

    //public static final int PAUSEBUTTON_WIDTH = 30;
    //public static final int PAUSEBUTTON_HEIGHT = 30;

    public static final float WAIT_COMMAND = 2;



    //Sound & Music
    private final Music music = Gdx.audio.newMusic(Gdx.files.internal("8 Bit Universe.mp3"));
    private final Sound shoot = Gdx.audio.newSound(Gdx.files.internal("shootSound.mp3"));
    private final Sound healFx = Gdx.audio.newSound(Gdx.files.internal("restoreHP.mp3"));
    private final Sound explosionFx = Gdx.audio.newSound(Gdx.files.internal("Explosion Sound Effect.mp3"));
    private final Sound hitFx = Gdx.audio.newSound(Gdx.files.internal("8-Bit Hit Sound Effect.mp3"));
    private final Sound lowHp = Gdx.audio.newSound(Gdx.files.internal("lowHp.mp3"));

    Animation[]rolls;
    private float waitCommandCounter = 0;
    private float waitReverseCounter = 0;
    private float waitShieldCounter = 0;

    float x;
    float y;
    int roll;
    float rollTimer; //traccia il tempo del roll
    float stateTime; //usiamo come stato generale per l'animazione.
                    // La classe animazione
    float asteroidsSpawnTimer;
    float healsSpawnTimer;
    float reverseSpawnTimer;
    float shieldSpawnTimer;

    //float pauseButtonX;
    //float pauseButtonY;
    float countSleepTime;
    Random random;

    public boolean reverseMalus = false;
    public boolean shieldBonus = false;
    public boolean onceLowHp = false;
   // public boolean pause = false;

    float shootTimer;
    SpaceGame game;
    ArrayList<Bullet> bullets;
    ArrayList<Asteroid> asteroids;
    ArrayList<Explosion> explosions;
    ArrayList<Heal> heals;
    ArrayList<Reverse> reverses;
    ArrayList<Shield> shields;

    Texture blank;
    Texture controls;
    //Texture pauseButton;

    BitmapFont scoreFont;

    CollisionReact playerReact;
    CollisionReact shieldReact;
    float health = 1; //0 = dead, 1 = full health
    public static int score;

    //touch ci prende le coordinate di dove avviene la pressione sullo schermo.

    /* Quando creiamo il costruttore di MainGameScreen, passiamo "SpaceGame Game" cosi dentro questa classe siamo in
    grado di accedere alla classe principale "SpaceGame". In questo modo accediamo al batch (dobbiamo definirlo public),
    E poi impostiamo il game di questa classe (this.game), con il game che passiamo dalla classe main.

    Dobbiamo impostare il batch come game.batch, perché il batch appartiene al game passato dalla classe main.
    */

    public MainGameScreen(SpaceGame game){

        this.game = game;
        y = (float) SpaceGame.HEIGHT / 2 - (float) SHIP_HEIGHT / 2;
        x = (float) SpaceGame.WIDTH / 2 - (float) SHIP_WIDTH / 2;
        bullets = new ArrayList<>();
        asteroids = new ArrayList<>();
        explosions = new ArrayList<>();
        reverses = new ArrayList<>();
        heals = new ArrayList<>();
        shields = new ArrayList<>();
        scoreFont = new BitmapFont(Gdx.files.internal("fonts/score.fnt"));
        playerReact = new CollisionReact(0,0,SHIP_WIDTH,SHIP_HEIGHT);
        shieldReact = new CollisionReact(0,0,PLAYERSHIELD_WIDTH, PLAYERSHIELD_HEIGHT);
        blank = new Texture("blank.png");
       // pauseButton = new Texture("pauseButton.png");

        //pauseButtonX = 0;
        //pauseButtonY = 0;


        //BUTTON


        
        //MUSIC & SOUNDFX

        if(SpaceGame.IS_MOBILE){
            controls = new Texture("controls.png");
        }

        score = 0;
        /*
        Random.nextfloat() genera un numero random compreso tra 0 e 1
        in pratica quello che facciamo e fare un calcolo random + aggiungiamo 0.3s al risultato in modo da avere uno spawn
        randomico ogni 0.3 e 0.6 secondi.
         */
        random = new Random();
        asteroidsSpawnTimer = random.nextFloat() * (MAX_ASTEROID_SPAWN_TIME - MIN_ASTEROID_SPAWN_TIME) + MIN_ASTEROID_SPAWN_TIME;
        healsSpawnTimer = random.nextFloat() * (MAX_HEAL_SPAWN_TIME - MIN_HEAL_SPAWN_TIME) + MIN_HEAL_SPAWN_TIME;
        reverseSpawnTimer = random.nextFloat() * (MAX_REVERSE_SPAWN_TIME - MIN_REVERSE_SPAWN_TIME) + MIN_REVERSE_SPAWN_TIME;
        shieldSpawnTimer = random.nextFloat() * (MAX_SHIELD_SPAWN_TIME - MIN_SHIELD_SPAWN_TIME) + MIN_SHIELD_SPAWN_TIME;

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

        rolls[0] = new Animation<>(SHIP_ANIMATION_SPEED, rollSpriteSheet[2]);//ANIMAZIONE VERSO SINISTRA
        rolls[1] = new Animation<>(SHIP_ANIMATION_SPEED, rollSpriteSheet[1]);
        rolls[2] = new Animation<>(SHIP_ANIMATION_SPEED, rollSpriteSheet[0]);//ANIMAZIONE CENTRALE
        rolls[3] = new Animation<>(SHIP_ANIMATION_SPEED, rollSpriteSheet[3]);
        rolls[4] = new Animation<>(SHIP_ANIMATION_SPEED, rollSpriteSheet[4]);//ANIMAZIONE VERSO DESTRA

        game.ScrollingBackground.setSpeedFixed(false);
    }




    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        music.setVolume(0.1f);
        music.play();

        /*
        countSleepTime += Gdx.graphics.getDeltaTime();

        float touchX = game.cam.getInputInGameWorld().x, touchY = SpaceGame.HEIGHT - game.cam.getInputInGameWorld().y;

        if(touchX > pauseButtonX && touchX < pauseButtonX + PAUSEBUTTON_WIDTH && touchY < Gdx.graphics.getHeight() && touchY > Gdx.graphics.getHeight() - PAUSEBUTTON_HEIGHT){
            pause = true;
        } else {
            pause = false;
        }




        if(pause) {
        try {
            Thread.holdsLock()
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    */


        //shooting code
        shootTimer += delta;
        if ((isLeft() || isRight()) && shootTimer >= SHOOT_WAIT_TIME && SpaceGame.IS_MOBILE) {
            shootTimer = 0;

            int offset = 4;

            if (roll == 1 || roll == 3) { //virata leggera (sia sinistra che destra)
                offset = 8;
            }

            if (roll == 0 || roll == 4) { //virata completa (sia sinistra che destra)
                offset = 16;
            }

            long id = shoot.play();
            shoot.setVolume(id, 0.2f);

            bullets.add(new Bullet(x + SHIP_WIDTH - (float) SHIP_WIDTH / 2, y + 40));

            if (score > 1000) {
                bullets.add(new Bullet(x + offset, y + 40));
                bullets.add(new Bullet(x + SHIP_WIDTH - offset, y + 40));
            }

            if (score > 2500) {
                bullets.add(new Bullet(x + offset + 10, y + 40));
                bullets.add(new Bullet(x + SHIP_WIDTH - offset - 10, y + 40));
            }
        }

        if (shootTimer >= SHOOT_WAIT_TIME && !SpaceGame.IS_MOBILE && Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            shootTimer = 0;

            int offset = 4;

            if (roll == 1 || roll == 3) { //virata leggera (sia sinistra che destra)
                offset = 8;
            }

            if (roll == 0 || roll == 4) { //virata completa (sia sinistra che destra)
                offset = 16;
            }

            long id = shoot.play();
            shoot.setVolume(id, 0.2f);

            bullets.add(new Bullet(x + SHIP_WIDTH - (float) SHIP_WIDTH / 2, y + 40));

            if (score > 1000) {
                bullets.add(new Bullet(x + offset, y + 40));
                bullets.add(new Bullet(x + SHIP_WIDTH - offset, y + 40));
            }

            if (score > 2500) {
                bullets.add(new Bullet(x + offset + 10, y + 40));
                bullets.add(new Bullet(x + SHIP_WIDTH - offset - 10, y + 40));
            }
        }

        //Asteroids Spawn Code
        //SpaceGame.WIDTH - Asteroid.WIDTH <- per evitare asteroidi tagliati sul bordo destro dello schermo
        asteroidsSpawnTimer -= delta;
        if (asteroidsSpawnTimer <= 0) {
            asteroidsSpawnTimer = random.nextFloat() * (MAX_ASTEROID_SPAWN_TIME - MIN_ASTEROID_SPAWN_TIME) + MIN_ASTEROID_SPAWN_TIME;
            asteroids.add(new Asteroid(random.nextInt(SpaceGame.WIDTH - Asteroid.WIDTH)));
        }

        //heal spawn code
        healsSpawnTimer -= delta;
        if (healsSpawnTimer <= 0) {
            healsSpawnTimer = random.nextFloat() * (MAX_HEAL_SPAWN_TIME - MIN_HEAL_SPAWN_TIME) + MIN_HEAL_SPAWN_TIME;
            heals.add(new Heal(random.nextInt(SpaceGame.WIDTH - Heal.WIDTH)));
        }

        //reverse spawn code
        reverseSpawnTimer -= delta;
        if (reverseSpawnTimer <= 0) {
            reverseSpawnTimer = random.nextFloat() * (MAX_REVERSE_SPAWN_TIME - MIN_REVERSE_SPAWN_TIME) + MIN_REVERSE_SPAWN_TIME;
            reverses.add(new Reverse(random.nextInt(SpaceGame.WIDTH - Reverse.WIDTH)));
        }

        //shield spawn code
        shieldSpawnTimer -= delta;
        if (shieldSpawnTimer <= 0 && !shieldBonus) {
            shieldSpawnTimer = random.nextFloat() * (MAX_SHIELD_SPAWN_TIME - MIN_SHIELD_SPAWN_TIME) + MIN_SHIELD_SPAWN_TIME;
            shields.add(new Shield(random.nextInt(SpaceGame.WIDTH - Shield.WIDTH), SpaceGame.HEIGHT));
        }

        //PlayerShield
        PlayerShield playerShield = new PlayerShield(x - (float) PlayerShield.WIDTH / 2 - PlayerShield.WIDTH, y + 30);


        //Update asteroids
        ArrayList<Asteroid> asteroidsToRemove = new ArrayList<>();
        for (Asteroid asteroid : asteroids) {
            asteroid.update(delta);
            if (asteroid.remove) {
                asteroidsToRemove.add(asteroid);
            }
        }


        //update bullets (loop dentro la lista bullets, per ogni bullet presente al suo interno).
        ArrayList<Bullet> bulletsToRemove = new ArrayList<>();
        for (Bullet bullet : bullets) {
            bullet.update(delta);
            if (bullet.remove) {
                bulletsToRemove.add(bullet); //se un bullet deve essere rimosso viene inserito nella lista delle rimozioni
            }
        }

        //update heal
        ArrayList<Heal> healsToRemove = new ArrayList<>();
        for (Heal heal : heals) {
            heal.update(delta);
            if (heal.remove) {
                healsToRemove.add(heal);
            }
        }
        heals.removeAll(healsToRemove);

        //update explosions
        ArrayList<Explosion> explosionsToRemove = new ArrayList<>();
        for (Explosion explosion : explosions) {
            explosion.update(delta);
            if (explosion.remove) {
                explosionsToRemove.add(explosion);
            }
        }
        explosions.removeAll(explosionsToRemove);


        //Update Reverse
        ArrayList<Reverse> reversesToRemove = new ArrayList<>();
        for (Reverse reverse : reverses) {
            reverse.update(delta);
            if (reverse.remove) {
                reversesToRemove.add(reverse);
            }
        }

        //update shield
        ArrayList<Shield> shieldsToRemove = new ArrayList<>();
        for (Shield shield : shields) {
            shield.update(delta);
            if (shield.remove) {
                shieldsToRemove.add(shield);
            }
        }

        reverses.removeAll(reversesToRemove);


        //movement code

        if (isUp()) {
            y += SPEED * Gdx.graphics.getDeltaTime();

            //bordo superiore
            if (y + SHIP_HEIGHT + 1 > SpaceGame.HEIGHT) {
                y = SpaceGame.HEIGHT - SHIP_HEIGHT - 1;
            }
        }
        if (isDown()) {
            y -= SPEED * Gdx.graphics.getDeltaTime();

            if (y < 0) {
                y = 0;
            }
        }

// movimento verso sinistra


        if (isLeft()) {
            x -= SPEED * Gdx.graphics.getDeltaTime();

            //bordo sinistro
            if (x < 0) {
                x = 0;
            }

            //aggiorniamo la virata a sinistra se siamo ancora in virata verso destra (movimento più fluido e naturale)
            if (isJustLeft() && !isRight() && roll > 0) {
                rollTimer = 0;
                roll--;
            }

            //Update roll
                /*
                controlliamo che rollTimer sia maggiore del Roll_timer_switch_time,
                ovvero se il tempo in cui il giocatore decide di mantenere premuto il
                tasto destro o sinistro è maggiore nel tempo che abbiamo impostato per lo switch
                avvera' l'animazione del verso in cui ci stiamo spostando.
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
        if (isRight()) {
            x += SPEED * Gdx.graphics.getDeltaTime();


            //bordo destro
            //SpaceGame.WIDTH <- screen width
            if (x + SHIP_WIDTH > SpaceGame.WIDTH) {
                x = SpaceGame.WIDTH - SHIP_WIDTH;
            }

            //aggiorniamo la virata a sinistra se siamo ancora in virata verso destra (movimento più fluido e naturale)
            if (isJustRight() && !isLeft() && roll > 0) {
                rollTimer = 0;
                if (roll < 4) {
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
        } else {
            if (roll > 2) {
                rollTimer -= Gdx.graphics.getDeltaTime();
                if (Math.abs(rollTimer) > ROLL_TIMER_SWITCH_TIME && roll > 0) {
                    rollTimer -= ROLL_TIMER_SWITCH_TIME;
                    roll--;
                }
            }
        }


        // dopo aver mosso la navicella, aggiorniamo le collisioni (collisionReact),
        // stessa cosa per lo scudo nel caso sia attivo il power up
        playerReact.move(x, y);
        if (shieldBonus)
            shieldReact.move(x - (float) PLAYERSHIELD_WIDTH / 3 + 10, y);



        /* Dopo gli update di ogni oggetto, controlliamo le collisioni
           Eseguiamo un loop innestato, in modo che controlliamo se un proiettile ha una collisione con un
           qualsiasi proiettile presente nello schermo di gioco.
         */
        for (Bullet bullet : bullets) {
            for (Asteroid asteroid : asteroids) {
                if (bullet.getCollisionReact().collidesWith(asteroid.getCollisionReact())) { //avviene una collisione
                    bulletsToRemove.add(bullet);
                    asteroidsToRemove.add(asteroid);
                    explosions.add(new Explosion(asteroid.getX(), asteroid.getY()));
                    score += 100;
                    long idExplosion = explosionFx.play();
                    explosionFx.setVolume(idExplosion, 0.3f);
                }
            }
        }
        asteroids.removeAll(asteroidsToRemove); //rimuoviamo tutti gli asteroidi presenti nell' ArrayList da rimuovere
        bullets.removeAll(bulletsToRemove); //rimuoviamo tutti i proiettili presenti nell' ArrayList da rimuovere

        if (!shieldBonus) {
            for (Asteroid asteroid : asteroids) {
                if (asteroid.getCollisionReact().collidesWith(playerReact)) {
                    asteroidsToRemove.add(asteroid);
                    if (!shieldBonus) {
                        health -= 0.1;
                        hitFx.play();
                    }

                    //if health is depleted go to game over screen
                    if (health <= 0) {
                        this.dispose(); //causes the JFrame window to be destroyed and cleaned up by the operating system
                        game.setScreen(new GameOverScreen(game, score));
                        return;
                    }
                }
            }
            asteroids.removeAll(asteroidsToRemove);


            for (Heal heal : heals) {
                if (heal.getCollisionReact().collidesWith(playerReact)) {
                    healsToRemove.add(heal);

                    if (health <= 0.8)
                        health += 0.2;

                    if (health == 0.9)
                        health = 1;

                    long idHeal = healFx.play();
                    healFx.setVolume(idHeal, 1f);
                }

            }
            heals.removeAll(healsToRemove);


            for (Reverse reverse : reverses) {
                if (reverse.getCollisionReact().collidesWith(playerReact)) {
                    reversesToRemove.add(reverse);
                    if (!shieldBonus) {
                        reverseMalus = true;
                    }
                    waitReverseCounter = 0;
                }
            }

            reverses.removeAll(reversesToRemove);


            //Shield

            for (Shield shield : shields) {
                if (shield.getCollisionReact().collidesWith(playerReact)) {
                    shieldsToRemove.add(shield);
                    shieldBonus = true;
                    waitShieldCounter = 0;
                }
            }
            shields.removeAll(shieldsToRemove);
        }

        waitReverseCounter += Gdx.graphics.getDeltaTime();
        if (waitReverseCounter >= REVERSE_WAIT_TIME) {
            waitReverseCounter = 0;
            reverseMalus = false;
        }

        waitShieldCounter += Gdx.graphics.getDeltaTime();
        if (waitShieldCounter >= SHIELD_WAIT_TIME) {
            waitShieldCounter = 0;
            shieldBonus = false;
        }


        //rimuoviamo qualsiasi cosa collida sul playerShield
        if (shieldBonus) {
            for (Asteroid asteroid : asteroids) {
                if (asteroid.getCollisionReact().collidesWith(shieldReact)) { //avviene una collisione
                    asteroidsToRemove.add(asteroid);
                    explosions.add(new Explosion(asteroid.getX(), asteroid.getY()));
                    score += 25;
                    long idExplosion = explosionFx.play();
                    explosionFx.setVolume(idExplosion, 0.3f);
                }
            }
            asteroids.removeAll(asteroidsToRemove);


            for (Reverse reverse : reverses) {
                if (reverse.getCollisionReact().collidesWith(shieldReact)) { //avviene una collisione
                    reversesToRemove.add(reverse);
                    score += 15;
                }
            }
            reverses.removeAll(reversesToRemove);

            for (Heal heal : heals) {
                if (heal.getCollisionReact().collidesWith(shieldReact)) { //avviene una collisione
                    healsToRemove.add(heal);
                    if (health <= 0.8)
                        health += 0.2;

                    if (health == 0.9)
                        health = 1;

                    long idHeal = healFx.play();
                    healFx.setVolume(idHeal, 1f);
                }
            }
            heals.removeAll(healsToRemove);
        }


        stateTime += delta;


        // il rendering funziona a layer, quindi l'ultima cosa che verrà reinderizzata sarà sopra le altre
        ScreenUtils.clear(0, 0, 0, 1);
        game.batch.begin();


        game.ScrollingBackground.updateAndRender(delta, game.batch);

       // game.batch.draw(pauseButton,pauseButtonX,Gdx.graphics.getHeight()-PAUSEBUTTON_HEIGHT);

        for (Bullet bullet : bullets) {
            bullet.render(game.batch);
        }

        for (Asteroid asteroid : asteroids) {
            asteroid.render(game.batch);
        }

        for (Explosion explosion : explosions) {
            explosion.render(game.batch);
        }

        for (Heal heal : heals) {
            heal.render(game.batch);
        }

        for (Heal heal : heals) {
            heal.render(game.batch);
        }

        for (Reverse reverse : reverses) {
            reverse.render(game.batch);
        }

        for (Shield shield : shields) {
            shield.render(game.batch);
        }


        if (shieldBonus) {
            playerShield.render(game.batch);
        }

        //color and soundFx of SpaceShip life
        if (health > 0.6f) {
            game.batch.setColor(Color.GREEN);
        } else if (health > 0.2f) {
            game.batch.setColor(Color.YELLOW);
            onceLowHp = false;
        } else {
            game.batch.setColor(Color.RED);
            if (!onceLowHp) {
                lowHp.play();
                onceLowHp = true;
            }
        }

        game.batch.draw(blank, 0, 0, SpaceGame.WIDTH * health, 5);
        game.batch.setColor(Color.WHITE);

        game.batch.draw((TextureRegion) rolls[roll].getKeyFrame(stateTime, true), x, y, SHIP_WIDTH, SHIP_HEIGHT);


        GlyphLayout scoreLayout = new GlyphLayout(scoreFont, "" + score);
        scoreFont.draw(game.batch, scoreLayout, (float) SpaceGame.WIDTH / 2 - scoreLayout.width / 2, SpaceGame.HEIGHT - scoreLayout.height - 10);

        if (SpaceGame.IS_MOBILE && waitCommandCounter < WAIT_COMMAND) {
            //draw left side
            waitCommandCounter += delta;
            game.batch.setColor(Color.RED);
            game.batch.draw(controls, 0, 0, (float) SpaceGame.WIDTH / 2, SpaceGame.HEIGHT, 0, 0, SpaceGame.WIDTH / 2, SpaceGame.HEIGHT, false, false);

            //draw right side
            game.batch.setColor(Color.BLUE);
            game.batch.draw(controls, (float) SpaceGame.WIDTH / 2, 0, (float) SpaceGame.WIDTH / 2, SpaceGame.HEIGHT, 0, 0, SpaceGame.WIDTH / 2, SpaceGame.HEIGHT, true, false);
            game.batch.setColor(Color.WHITE);
        }

        game.batch.end();

    }

    /*
    Impostiamo i metodi booleani per sapere se l'utente sta toccando la parte sinistra o destra dello schermo
    dato che su smartphone non è possibile la gestione tramite la tastiera.

    Il metodo "isRight()" ci ritorna 1 se è premuta la freccia destra da tastiera, oppure dividendo l'asse X a metà, la pressione deve risultare maggiore
    del punto di divisione (parte destra).
     */

    private boolean isRight(){
        if(reverseMalus){
            return Gdx.input.isKeyPressed(Input.Keys.LEFT) || (Gdx.input.isTouched() && game.cam.getInputInGameWorld().x < (float) SpaceGame.WIDTH /2);
        }
        else{
            return Gdx.input.isKeyPressed(Input.Keys.RIGHT) || (Gdx.input.isTouched() && game.cam.getInputInGameWorld().x >= (float) SpaceGame.WIDTH / 2);
        }

    }

    private boolean isLeft(){
        if(reverseMalus) {
            return Gdx.input.isKeyPressed(Input.Keys.RIGHT) || (Gdx.input.isTouched() && game.cam.getInputInGameWorld().x >= (float) SpaceGame.WIDTH / 2);
        }
        else{
            return Gdx.input.isKeyPressed(Input.Keys.LEFT) || (Gdx.input.isTouched() && game.cam.getInputInGameWorld().x < (float) SpaceGame.WIDTH /2);
        }

    }

    private boolean isJustRight(){
        return Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || (Gdx.input.justTouched() && game.cam.getInputInGameWorld().x >= (float) SpaceGame.WIDTH /2);

    }

    private boolean isJustLeft(){
        return Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || (Gdx.input.justTouched() && game.cam.getInputInGameWorld().x < (float) SpaceGame.WIDTH /2);

    }


    /* lo '0' nell'asse x parte dal bordo inferiore, mentre nell'asse Y parte dal bordo superiore, per questo motivo nelle funzioni isUp() e isDown(),
     controlliamo la Y sia minore della metà per salire, e maggiore della metà per scendere. (il valore della Y risulta specchiato, ovvero sotto-sopra).
     Sono presenti le funzioni di stampa dei valori X e Y per vedere la posizione in base al puntatore del mouse
     */
    private boolean isUp(){
        return Gdx.input.isKeyPressed(Input.Keys.UP) || (Gdx.input.isTouched() && game.cam.getInputInGameWorld().y < (float) SpaceGame.HEIGHT /2);

    }

    private boolean isDown(){
        return Gdx.input.isKeyPressed(Input.Keys.DOWN) || (Gdx.input.isTouched() && game.cam.getInputInGameWorld().y > (float) SpaceGame.HEIGHT /2);

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


    /*
    Utilizziamo il metodo dispose per disfarci degli oggetti presenti, in modo da non creare dei doppioni ed avere memory leak, dovuti
    a più schermate create durante un'unica sessione.
     */
    @Override
    public void dispose() {
    shoot.dispose();
    music.dispose();
    healFx.dispose();
    explosionFx.dispose();
    hitFx.dispose();
    lowHp.dispose();
    }
}
