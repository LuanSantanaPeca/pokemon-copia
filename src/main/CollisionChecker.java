package main;

import java.awt.Rectangle;

import entidade.Entity;
import entidade.Totem;

public class CollisionChecker{
	
	Painel gp;

	public CollisionChecker(Painel gp) {
		this.gp = gp;
	}
	
	public void checkTile(Entity entity) {
		entity.canFindPokemon = false;

		int entityLeftWorldX = entity.worldX + entity.solidArea.x;
		int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
		int entityTopWorldY = entity.worldY + entity.solidArea.y;
		int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;
		
		int entityLeftCol = entityLeftWorldX/gp.tileSize;
		int entityRightCol = entityRightWorldX/gp.tileSize;
		int entityTopRow = entityTopWorldY/gp.tileSize;
		int entityBottomRow = entityBottomWorldY/gp.tileSize;
		
		int tileNum1, tileNum2, tileNum3, tileNum4;
		
		switch(entity.direction) {
		case "up":
			entityTopRow = (entityTopWorldY - entity.speed)/gp.tileSize;
			tileNum1 = gp.map.mapTileNumO[entityLeftCol][entityTopRow];
			tileNum2 = gp.map.mapTileNumO[entityRightCol][entityTopRow];
			tileNum3 = gp.map.mapTileNumB[entityLeftCol][entityTopRow];
			tileNum4 = gp.map.mapTileNumB[entityRightCol][entityTopRow];
			
			if(gp.map.tiles[tileNum1].collision == true || gp.map.tiles[tileNum2].collision == true ||
			   gp.map.tiles[tileNum3].collision == true || gp.map.tiles[tileNum4].collision == true) {
				entity.collisionOn = true;
			}
			if(gp.map.tiles[tileNum1].findPokemon == true || gp.map.tiles[tileNum2].findPokemon == true ||
			   gp.map.tiles[tileNum3].findPokemon == true || gp.map.tiles[tileNum4].findPokemon == true) {
				entity.canFindPokemon = true;
			}
			break;
		case "down":
			entityBottomRow = (entityBottomWorldY + entity.speed)/gp.tileSize;
			tileNum1 = gp.map.mapTileNumO[entityLeftCol][entityBottomRow];
			tileNum2 = gp.map.mapTileNumO[entityRightCol][entityBottomRow];
			tileNum3 = gp.map.mapTileNumB[entityLeftCol][entityBottomRow];
			tileNum4 = gp.map.mapTileNumB[entityRightCol][entityBottomRow];
			
			if(gp.map.tiles[tileNum1].collision == true || gp.map.tiles[tileNum2].collision == true ||
			   gp.map.tiles[tileNum3].collision == true || gp.map.tiles[tileNum4].collision == true) {
				entity.collisionOn = true;
			}
			if(gp.map.tiles[tileNum1].findPokemon == true || gp.map.tiles[tileNum2].findPokemon == true ||
			   gp.map.tiles[tileNum3].findPokemon == true || gp.map.tiles[tileNum4].findPokemon == true) {
				entity.canFindPokemon = true;
			}
			break;
		case "left":
			entityLeftCol = (entityLeftWorldX - entity.speed)/gp.tileSize;
			tileNum1 = gp.map.mapTileNumO[entityLeftCol][entityTopRow];
			tileNum2 = gp.map.mapTileNumO[entityLeftCol][entityBottomRow];
			tileNum3 = gp.map.mapTileNumB[entityLeftCol][entityTopRow];
			tileNum4 = gp.map.mapTileNumB[entityLeftCol][entityBottomRow];
			
			if(gp.map.tiles[tileNum1].collision == true || gp.map.tiles[tileNum2].collision == true ||
			   gp.map.tiles[tileNum3].collision == true || gp.map.tiles[tileNum4].collision == true) {
				entity.collisionOn = true;
			}
			if(gp.map.tiles[tileNum1].findPokemon == true || gp.map.tiles[tileNum2].findPokemon == true ||
			   gp.map.tiles[tileNum3].findPokemon == true || gp.map.tiles[tileNum4].findPokemon == true) {
				entity.canFindPokemon = true;
			}
			break;
		case "right":
			entityRightCol = (entityRightWorldX + entity.speed)/gp.tileSize;
			tileNum1 = gp.map.mapTileNumO[entityRightCol][entityTopRow];
			tileNum2 = gp.map.mapTileNumO[entityRightCol][entityBottomRow];
			tileNum3 = gp.map.mapTileNumB[entityRightCol][entityTopRow];
			tileNum4 = gp.map.mapTileNumB[entityRightCol][entityBottomRow];
			
			if(gp.map.tiles[tileNum1].collision == true || gp.map.tiles[tileNum2].collision == true ||
			   gp.map.tiles[tileNum3].collision == true || gp.map.tiles[tileNum4].collision == true) {
				entity.collisionOn = true;
			}
			if(gp.map.tiles[tileNum1].findPokemon == true || gp.map.tiles[tileNum2].findPokemon == true ||
			   gp.map.tiles[tileNum3].findPokemon == true || gp.map.tiles[tileNum4].findPokemon == true) {
				entity.canFindPokemon = true;
			}
			break;
		}
	}

	public void checkTotem(Entity entity, boolean moving) {
		entity.interactOn = false;
		Totem totem = gp.totem;
		if (totem == null) {
			return;
		}

		Rectangle totemHit = new Rectangle(
				totem.worldX + totem.solidArea.x,
				totem.worldY + totem.solidArea.y,
				totem.solidArea.width,
				totem.solidArea.height);

		int dx = 0;
		int dy = 0;
		if (moving && entity.direction != null) {
			switch (entity.direction) {
			case "up":
				dy = -entity.speed;
				break;
			case "down":
				dy = entity.speed;
				break;
			case "left":
				dx = -entity.speed;
				break;
			case "right":
				dx = entity.speed;
				break;
			}
		}

		Rectangle predicted = new Rectangle(
				entity.worldX + entity.solidArea.x + dx,
				entity.worldY + entity.solidArea.y + dy,
				entity.solidArea.width,
				entity.solidArea.height);
		if (predicted.intersects(totemHit)) {
			entity.collisionOn = true;
		}

		Rectangle current = new Rectangle(
				entity.worldX + entity.solidArea.x,
				entity.worldY + entity.solidArea.y,
				entity.solidArea.width,
				entity.solidArea.height);
		Rectangle interactZone = new Rectangle(
				totemHit.x - 12,
				totemHit.y - 12,
				totemHit.width + 24,
				totemHit.height + 24);
		if (current.intersects(interactZone) || predicted.intersects(interactZone)) {
			entity.interactOn = true;
		}
	}
}
