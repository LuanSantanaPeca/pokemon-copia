package tile;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import main.Painel;

public class TileManager {
	
	Painel gp;
	public Tile[] tiles;
	public int mapTileNumB[][];
	public int mapTileNumO[][];
	
	public TileManager(Painel gp) {
		this.gp = gp;
		
		tiles = new Tile[15];
		mapTileNumB = new int[gp.maxWorldCol][gp.maxWorldRow];
		mapTileNumO = new int[gp.maxWorldCol][gp.maxWorldRow];
		
		getTileImage();
		loadBaseMap();
		loadOverlayMap();
	}
	
	public void getTileImage() {
		try{
			tiles[0] = new Tile();
			tiles[0].imagem = ImageIO.read(getClass().getResourceAsStream("/map/base.png"));
			
			tiles[1] = new Tile();
			tiles[1].imagem = ImageIO.read(getClass().getResourceAsStream("/map/grama1.png"));
			tiles[1].findPokemon = true;
			
			tiles[10] = new Tile();
			tiles[10].imagem = ImageIO.read(getClass().getResourceAsStream("/map/base.png"));
			tiles[10].collision = true;
			/*
			tiles[3] = new Tile();
			tiles[3].imagem = ImageIO.read(getClass().getResourceAsStream("/tiles/lava1.png"));
			
			tiles[4] = new Tile();
			tiles[4].imagem = ImageIO.read(getClass().getResourceAsStream("/tiles/agua1.png"));
			
			tiles[5] = new Tile();
			tiles[5].imagem = ImageIO.read(getClass().getResourceAsStream("/tiles/pedra1.png"));
			
			tiles[6] = new Tile();
			tiles[6].imagem = ImageIO.read(getClass().getResourceAsStream("/tiles/floresta1.png"));
			
			tiles[7] = new Tile();
			tiles[7].imagem = ImageIO.read(getClass().getResourceAsStream("/tiles/parede1.png"));
			
			tiles[8] = new Tile();
			tiles[8].imagem = ImageIO.read(getClass().getResourceAsStream("/tiles/parede2.png"));
			
			tiles[9] = new Tile();
			tiles[9].imagem = ImageIO.read(getClass().getResourceAsStream("/tiles/portao1.png"));
			
			tiles[10] = new Tile();
			tiles[10].imagem = ImageIO.read(getClass().getResourceAsStream("/tiles/torre1.png"));
			*/
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void drawBase(Graphics2D g2) {
		
		int worldCol = 0;
		int worldRow = 0;
		
		while(worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {
			
			int tileNum = mapTileNumB[worldCol][worldRow];
			
			int worldX = worldCol * gp.tileSize;
			int worldY = worldRow * gp.tileSize;
			
			int screenX = worldX - gp.player.worldX + gp.player.screenX;
			int screenY = worldY - gp.player.worldY + gp.player.screenY;
			
			if(worldX + gp.tileSize > gp.player.worldX - gp.player.screenX && 
			   worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
			   worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
			   worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {
				g2.drawImage(tiles[tileNum].imagem, screenX, screenY, gp.tileSize, gp.tileSize, null);
			}
			worldCol++;
			
			if(worldCol == gp.maxWorldCol) {
				worldCol = 0;
				worldRow++;
			}
		}
		
	}
	public void loadBaseMap() {
		try {
			InputStream is = getClass().getResourceAsStream("/map/baseMapData.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			
			int col = 0;
			int row = 0;
			
			while(col < gp.maxWorldCol && row < gp.maxWorldRow) {
				
				String line = br.readLine();
				while(col < gp.maxWorldCol) {
					String numeros[] = line.split(" ");
					
					int nun = Integer.parseInt(numeros[col]);
					
					mapTileNumB[col][row] = nun;
					col++;
				}
				if(col == gp.maxWorldCol) {
					col = 0;
					row++;
				}
			}
			br.close();
			
			
		}catch(Exception e) {
			
		}
	}
	
	public void drawOverlay(Graphics2D g2) {
		
		int worldCol = 0;
		int worldRow = 0;
		
		while(worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {
			
			int tileNum = mapTileNumO[worldCol][worldRow];
			
			int worldX = worldCol * gp.tileSize;
			int worldY = worldRow * gp.tileSize;
			
			int screenX = worldX - gp.player.worldX + gp.player.screenX;
			int screenY = worldY - gp.player.worldY + gp.player.screenY;
			
			if (tileNum != 0) {
				if(worldX + gp.tileSize > gp.player.worldX - gp.player.screenX && 
						   worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
						   worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
						   worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {
					g2.drawImage(tiles[tileNum].imagem, screenX, screenY, gp.tileSize, gp.tileSize, null);
				}
			}
			worldCol++;
			
			if(worldCol == gp.maxWorldCol) {
				worldCol = 0;
				worldRow++;
			}
		}
	}
	public void loadOverlayMap() {
		try {
			InputStream is = getClass().getResourceAsStream("/map/overlayMapData.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			
			int col = 0;
			int row = 0;
			
			while(col < gp.maxWorldCol && row < gp.maxWorldRow) {
				
				String line = br.readLine();
				while(col < gp.maxWorldCol) {
					String numeros[] = line.split(" ");
					
					int nun = Integer.parseInt(numeros[col]);
					
					mapTileNumO[col][row] = nun;
					col++;
				}
				if(col == gp.maxWorldCol) {
					col = 0;
					row++;
				}
			}
			br.close();
			
			
		}catch(Exception e) {
			
		}
	}
}