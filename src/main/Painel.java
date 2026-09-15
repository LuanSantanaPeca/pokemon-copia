package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import entidade.Player;
import entidade.Pokemon;
import entidade.Totem;
import tile.TileManager;

public class Painel extends JPanel implements Runnable{
	final int tamOrigTile = 256;
	final int tamOrigPlayerX = 44;
	final int tamOrigPlayerY = 53;
	final double escalaTile = 0.3;
	final double escalaPlayer = 1.3;
	
	public final int tileSize = (int) (tamOrigTile * escalaTile);
	public final int playerTamX = (int) (tamOrigPlayerX * escalaPlayer);
	public final int playerTamY = (int) (tamOrigPlayerY * escalaPlayer);
	
	public final int maxScreenCol = 20;
	public final int maxScreenRow = 10;
	public final int larguraTela = tileSize * maxScreenCol;
	public final int comprimentoTela = tileSize * maxScreenRow;
	
	public final int maxWorldCol = 40;
	public final int maxWorldRow = 40;
	public final int worldWidth = tileSize * maxWorldCol;
	public final int worldHeight = tileSize * maxWorldRow;

	public static final int PLAY = 0;
	public static final int BATTLE = 1;
	public static final int TOTEM = 2;
	public static final int START = 3;
	public int gameState = START;
	
	int fps = 60;
	
	public TileManager map = new TileManager(this);
	public KeyHandler keyH = new KeyHandler();
	Thread gameThread;
	public CollisionChecker cChecker = new CollisionChecker(this);
	public Player player = new Player(this,keyH);
	public Treinador treinador = new Treinador();
	public Pokedex pokedex = new Pokedex();
	public Totem totem = new Totem(this);
	public BattleManager battleManager = new BattleManager(this);
	public TotemManager totemManager = new TotemManager(this);
	public StarterScreen starterScreen = new StarterScreen(this);

	private String overlayMessage = "";
	private int overlayMessageTimer = 0;
	private final Font overlayFont = new Font("Arial", Font.BOLD, 18);
		
	public Painel() {
			
			this.setPreferredSize(new Dimension(larguraTela, comprimentoTela));
			this.setBackground(Color.black);
			this.setDoubleBuffered(true);
			this.addKeyListener(keyH);
			this.setFocusable(true);
		}

	public void startGameThread() {
		gameThread = new Thread(this);
		gameThread.start();
	}
	
	@Override
	public void run() {
		
		double drawInterval = 1000000000/fps;
		double nextDrawtime = System.nanoTime() + drawInterval;
		long timer = 0;
		int drawCount = 0;
		long tempoAtual;
		long ultimaVez = System.nanoTime();
		double delta = 0;
		
		
		while(gameThread != null) {
			
			tempoAtual = System.nanoTime();
			
			delta+= (tempoAtual - ultimaVez) / drawInterval;
			timer += (tempoAtual - ultimaVez);
			ultimaVez = tempoAtual;
			
			if(delta >= 1) {
				update();
				repaint();
				delta--;
				drawCount++;
			}
			
			
			
			if(timer >= 1000000000) {
				System.out.println("fps: " + drawCount);
				drawCount = 0;
				timer = 0;
			}
		}
		
	}
	public void update() {
		if (gameState == START) {
			starterScreen.update();
		} else if (gameState == PLAY) {
			player.update();
			if (gameState == PLAY && player.interactOn && keyH.enterMenu) {
				totemManager.open();
			}
			if (gameState == PLAY && overlayMessageTimer > 0) {
				overlayMessageTimer--;
				if (overlayMessageTimer == 0) {
					overlayMessage = "";
				}
			}
		}
		if (gameState == BATTLE) {
			battleManager.update();
		} else if (gameState == TOTEM) {
			totemManager.update();
		}
		keyH.endFrame();
	}

	public void tryWildEncounter(int tileCol, int tileRow) {
		if (gameState != PLAY) {
			return;
		}
		if (!treinador.hasLivingPokemon()) {
			return;
		}

		// Overlay 1 is tall grass (same tiles that set Entity.canFindPokemon).
		boolean tallGrass = map.mapTileNumO[tileCol][tileRow] == 1;
		double chance = tallGrass ? 0.30 : 0.01;
		if (Math.random() < chance) {
			battleManager.start(Pokemon.randomWild());
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;

		if (gameState == START) {
			starterScreen.draw(g2);
			g2.dispose();
			return;
		}
		
		map.drawBase(g2);
		totem.draw(g2);
		player.draw(g2);
		map.drawOverlay(g2);

		if (gameState == BATTLE) {
			battleManager.draw(g2);
		} else if (gameState == TOTEM) {
			totemManager.draw(g2);
		} else if (overlayMessageTimer > 0) {
			g2.setFont(overlayFont);
			g2.setColor(new Color(0, 0, 0, 180));
			g2.fillRoundRect(larguraTela / 2 - 220, 20, 440, 40, 10, 10);
			g2.setColor(Color.WHITE);
			g2.drawString(overlayMessage, larguraTela / 2 - 200, 46);
		}
		
		g2.dispose();
	}
	
		
}
