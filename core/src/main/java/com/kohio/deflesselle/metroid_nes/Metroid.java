package com.kohio.deflesselle.metroid_nes;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.kohio.deflesselle.metroid_nes.Screens.GameScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Metroid extends Game {

    public SpriteBatch batch;
	public AssetManager assetManager;
	private GameScreen gameScreen;

	public static final int V_WIDTH = 240;
	public static final int V_HEIGHT = 224;

	public static final float PPU = 16;

	public static final short GROUND_BIT = 1;
	public static final short PLAYER_BIT = 2;
	public static final short FEET_BIT = 4;
	public static final short HEAD_BIT = 8;
	public static final short ITEM_BIT = 16;
	public static final short DOOR_BIT = 32;
	public static final short ENEMY_BIT = 64;
	public static final short ZOOMER_GROUND_BIT = 128;
	public static final short ZOOMER_WALL_BIT = 256;
	public static final short DESTROYED_BIT = 1024;

	public static final short MORPH_BALL = 1;
	public static final short MISSILE = 2;
	public static final short LONG_BEAM = 4;
	public static final short BOMBS = 8;
	public static final short ICE_BEAM = 16;
	public static final short HI_BOOTS = 32;
	public static final short VARIA_S = 64;
	public static final short SCREW_A = 128;
	public static final short WAVE_BEAM = 128;

	@Override
	public void create() {
		batch = new SpriteBatch();
		assetManager = new AssetManager();
		//Loading assets
		//Samus sprites
		assetManager.load("metroid_sprites.atlas", TextureAtlas.class);
		//load tiled map
		assetManager.setLoader(TiledMap.class, new TmxMapLoader());
		assetManager.load("level_assets/brinstar/brinstar_tilemap.tmx", TiledMap.class);
		assetManager.load("music/item_get_fanfare.mp3", Music.class);
		assetManager.load("music/brinstar_music.mp3", Music.class);
		setScreen(new ScreenAdapter());
	}

	@Override
	public void render() {
		if (assetManager.update() && gameScreen == null && getScreen() != gameScreen){
			setScreen(gameScreen = new GameScreen(this));
		}
		super.render();
	}

	@Override
	public void dispose() {
		batch.dispose();
	}
}