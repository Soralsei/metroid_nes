package com.kohio.deflesselle.metroid_nes.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kohio.deflesselle.metroid_nes.Entities.Enemy;
import com.kohio.deflesselle.metroid_nes.Entities.Player;
import com.kohio.deflesselle.metroid_nes.Metroid;
import com.kohio.deflesselle.metroid_nes.Tools.Cutscenes.CutscenePlayer;
import com.kohio.deflesselle.metroid_nes.Tools.Cutscenes.Events.CutsceneEvent;
import com.kohio.deflesselle.metroid_nes.Tools.Cutscenes.Events.ItemGetEvent;
import com.kohio.deflesselle.metroid_nes.Tools.GameInputProcessor;
import com.kohio.deflesselle.metroid_nes.Tools.WorldCollisionListener;
import com.kohio.deflesselle.metroid_nes.Tools.WorldCreator;

import static com.kohio.deflesselle.metroid_nes.Metroid.PPU;

import java.util.LinkedList;

public class GameScreen extends ScreenAdapter implements CutscenePlayer {

    private final Metroid game;
    private final OrthographicCamera cam;
    private final Viewport vPort;

    private final TiledMap map;
    private final MapLayers mapLayers;
    private final WorldCreator creator;
    private final WorldCollisionListener collisionListener;
    private final OrthogonalTiledMapRenderer renderer;
    private final Queue<CutsceneEvent> eventQueue = new Queue<>();
    private CutsceneEvent currentEvent;
    private final TextureAtlas atlas;

    //BOX2D variables
    private final World world;
    private final Box2DDebugRenderer debugRenderer;

    //player
    public final Player samus;

    //Enemies
    public final LinkedList<Enemy> enemies = new LinkedList<>();

    private final int roomWidth;
    private final int roomHeight;

    public Music levelMusic;

    public GameScreen(Metroid game){

        this.game = game;
        cam = new OrthographicCamera();
        vPort = new FitViewport(Metroid.V_WIDTH / PPU, Metroid.V_HEIGHT / PPU, cam);

        atlas = game.assetManager.get("metroid_sprites.atlas");

        map = game.assetManager.get("level_assets/brinstar/brinstar_tilemap.tmx");
        mapLayers = map.getLayers();
        renderer = new OrthogonalTiledMapRenderer(map, 1 / PPU, game.batch);
        levelMusic = game.assetManager.get("music/brinstar_music.mp3");
        levelMusic.setLooping(true);

        MapProperties p = map.getProperties();
        roomWidth = p.get("width", Integer.class);
        roomHeight = p.get("height", Integer.class);

        cam.position.set(40.5f, vPort.getWorldHeight() / 2, 0);

        world = new World(new Vector2(0, -15f), true);
        debugRenderer = new Box2DDebugRenderer();

        creator = new WorldCreator(world, map, this);
        world.setContactListener(collisionListener = new WorldCollisionListener(this));

        samus = new Player(world, atlas);

        Gdx.input.setInputProcessor(new GameInputProcessor(this));
        levelMusic.play();
//        game.assetManager.unload("metroid_sprites.atlas");
//        game.assetManager.unload("level_assets/brinstar/brinstar_tilemap.tmx");
        game.assetManager.unload("music/brinstar_music.mp3");
    }

    @Override
    public void render(float delta) {

        update(delta);
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(cam.combined);
        game.batch.begin();

        //Render background tilemap
        renderer.renderTileLayer((TiledMapTileLayer) map.getLayers().get("background"));
        renderer.renderTileLayer((TiledMapTileLayer) mapLayers.get(1));

        //then render player
        samus.draw(game.batch);
        if (!(currentEvent instanceof ItemGetEvent)) {
            for (Enemy enemy :
                    enemies) {
                if(entityIsOnScreen(enemy))
                    enemy.draw(game.batch);
            }
        }
        //then render foreground
        renderer.renderTileLayer((TiledMapTileLayer) mapLayers.get(2));
        game.batch.end();

        debugRenderer.render(world, cam.combined);

    }

    private boolean entityIsOnScreen(Sprite sprite) {
        float x = sprite.getX();
        float y = sprite.getY();
        float width = sprite.getWidth();
        float height = sprite.getHeight();
//        Gdx.app.log("entityIsOnScreen : sprite", "\nx : " + x + "\ny : " + y + "\nw : " + width + "\nh : " + height);
//        Gdx.app.log("entityIsOnScreen : camera", "\nx : " + cam.position.x + "\ny : " + cam.position.y + "\nw : " + cam.viewportWidth + "\nh : " + cam.viewportHeight);
        return x + width > cam.position.x - cam.viewportWidth / 2
                && y < cam.position.y + cam.viewportHeight / 2
                && x  < cam.position.x + cam.viewportWidth / 2
                && y + height > cam.position.y - cam.viewportHeight / 2;
    }

    public void update(float deltaTime){

        while (currentEvent == null || currentEvent.isFinished()) {
            if (eventQueue.isEmpty()) {
                currentEvent = null;
                levelMusic.play();
                break;
            }
            else {
                currentEvent = eventQueue.removeLast();
                currentEvent.begin(this);
                levelMusic.stop();
            }
        }
        if (currentEvent != null) {
            currentEvent.update(deltaTime);
        }
        else {
            samus.update(deltaTime);
            for (Enemy enemy:
                 enemies) {
                enemy.update(deltaTime);
            }
            world.step(deltaTime, 6, 2);
        }

        cam.position.x = Math.min(
                Math.max(samus.body.getPosition().x, cam.viewportWidth / 2),
                roomWidth - cam.viewportWidth / 2
        );
        cam.position.y = Math.min(
                Math.max(samus.body.getPosition().y, cam.viewportHeight / 2),
                roomHeight - cam.viewportHeight / 2
        );

        cam.update();

        renderer.setView(cam);

    }

    public void setPlayerGrounded(boolean b) {
        samus.setGrounded(b);
    }

    public void setPlayerCanStand(boolean b) {
        samus.canStand = b;
    }

    public void setCurrentState(Player.MoveState state) {
        samus.setCurrentState(state);
    }

    public void fireJumpAction() {
        samus.jump();
    }

    public boolean isPlayerJumping(){
        return samus.isJumping;
    }


    @Override
    public void resize(int width, int height) {
        vPort.update(width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        debugRenderer.dispose();
        game.assetManager.dispose();
        levelMusic.dispose();
        eventQueue.clear();
    }

    public void setJumpKeyHeld(boolean b) {
        samus.jumpKeyHeld = b;
    }

    public void fireMorphBallAction() {
        samus.morphBall();
    }

    public void fireStandAction() {
        samus.stand();
    }

    @Override
    public void queueEvent(CutsceneEvent event) {
        eventQueue.addLast(event);
    }

    public Metroid getGame() {
        return game;
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }
}
