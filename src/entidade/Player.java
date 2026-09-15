package entidade;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.KeyHandler;
import main.Painel;

public class Player extends Entity{
	Painel gp;
	KeyHandler keyH;
	
	public final int screenX;
	public final int screenY;
	private int lastTileCol;
	private int lastTileRow;
	
	public Player(Painel gp, KeyHandler keyH) {
		this.gp = gp;
		this.keyH = keyH;
		
		screenX = gp.larguraTela/2 - (gp.playerTamX/2);
		screenY = gp.comprimentoTela/2 - (gp.playerTamY/2);
		
		solidArea = new Rectangle(5, 16, 30, 40);
		
		setDefaultValues();
		getImgJogador();
	}
	public void setDefaultValues() {
		worldX = gp.tileSize * 10;
		worldY = gp.tileSize * 10;
		speed = 5;
		direction = "down";
		canFindPokemon = true;
		lastTileCol = worldX / gp.tileSize;
		lastTileRow = worldY / gp.tileSize;
	}
	
	
	public void getImgJogador() {
		try {
			up1 = ImageIO.read(getClass().getResourceAsStream("/player/up1.png"));
			up2 = ImageIO.read(getClass().getResourceAsStream("/player/up2.png"));
			up3 = ImageIO.read(getClass().getResourceAsStream("/player/up3.png"));
			down1 = ImageIO.read(getClass().getResourceAsStream("/player/down1.png"));
			down2 = ImageIO.read(getClass().getResourceAsStream("/player/down2.png"));
			down3 = ImageIO.read(getClass().getResourceAsStream("/player/down3.png"));
			
			left1 = ImageIO.read(getClass().getResourceAsStream("/player/left1.png"));
			left2 = ImageIO.read(getClass().getResourceAsStream("/player/left2.png"));
			left3 = ImageIO.read(getClass().getResourceAsStream("/player/left3.png"));
			right1 = ImageIO.read(getClass().getResourceAsStream("/player/right1.png"));
			right2 = ImageIO.read(getClass().getResourceAsStream("/player/right2.png"));
			right3 = ImageIO.read(getClass().getResourceAsStream("/player/right3.png"));
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void update() {
		if(gp.gameState != Painel.PLAY) {
			return;
		}

		boolean moving = keyH.upPressed == true || keyH.downPressed == true ||
		   keyH.leftPressed == true || keyH.rightPressed == true;

		if(moving) {
			if(keyH.upPressed == true) {
				direction = "up";
			}
			else if(keyH.downPressed == true) {
				direction = "down";
			}
			else if(keyH.leftPressed == true) {
				direction = "left";
			}
			else if(keyH.rightPressed == true) {
				direction = "right";
			}
			
			collisionOn = false;
			gp.cChecker.checkTile(this);
			gp.cChecker.checkTotem(this, true);
			
			if(collisionOn == false) {
				switch(direction) {
					case "up": worldY -= speed; break;
					case "down": worldY += speed; break;
					case "left": worldX -= speed; break;
					case "right": worldX += speed; break;
				}
				checkEncounterOnNewTile();
			}
			
			spriteCounter++;
			if(spriteCounter > 15) {
				if(spriteNun >= 1 && spriteNun < 4) {
					spriteNun++;
				}else if(spriteNun == 4 ){
					spriteNun = 1;
				}
				spriteCounter = 0;
			}
		} else {
			gp.cChecker.checkTotem(this, false);
		}
	}

	private void checkEncounterOnNewTile() {
		int tileCol = (worldX + solidArea.x + solidArea.width / 2) / gp.tileSize;
		int tileRow = (worldY + solidArea.y + solidArea.height / 2) / gp.tileSize;
		if (tileCol < 0 || tileRow < 0 || tileCol >= gp.maxWorldCol || tileRow >= gp.maxWorldRow) {
			return;
		}
		if (tileCol == lastTileCol && tileRow == lastTileRow) {
			return;
		}
		lastTileCol = tileCol;
		lastTileRow = tileRow;
		gp.tryWildEncounter(tileCol, tileRow);
	}
	
	public void draw(Graphics g2) {
		BufferedImage imagem = null;
		
		switch(direction) {
		case "up":
			if(spriteNun == 1 || spriteNun == 3) {
				imagem = up1;
			}
			if(spriteNun == 2) {
				imagem = up2;
			}
			if(spriteNun == 4) {
				imagem = up3;
			}
			break;
		case "down":
			if(spriteNun == 1 || spriteNun == 3) {
				imagem = down1;
			}
			if(spriteNun == 2) {
				imagem = down2;
			}
			if(spriteNun == 4) {
				imagem = down3;
			}
			break;
		case "left":
			if(spriteNun == 1 || spriteNun == 3) {
				imagem = left1;
			}
			if(spriteNun == 2) {
				imagem = left2;
			}
			if(spriteNun == 4) {
				imagem = left3;
			}
			break;
		case "right":
			if(spriteNun == 1 || spriteNun == 3) {
				imagem = right1;
			}
			if(spriteNun == 2) {
				imagem = right2;
			}
			if(spriteNun == 4) {
				imagem = right3;
			}
			break;
		}
		
		g2.drawImage(imagem, screenX, screenY, gp.playerTamX, gp.playerTamY, null);
		
	}
}