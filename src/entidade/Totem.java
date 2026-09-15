package entidade;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import main.Painel;

public class Totem {

	Painel gp;
	public int worldX, worldY;
	public int width, height;
	public Rectangle solidArea;
	private BufferedImage image;

	public Totem(Painel gp) {
		this.gp = gp;
		worldX = gp.tileSize * 20;
		worldY = gp.tileSize * 20;
		width = gp.tileSize;
		height = gp.tileSize;
		solidArea = new Rectangle(8, 8, width - 16, height - 16);
		loadImage();
	}

	private void loadImage() {
		try (InputStream stream = getClass().getResourceAsStream("/map/totem.png")) {
			if (stream != null) {
				image = ImageIO.read(stream);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void draw(Graphics2D g2) {
		int screenX = worldX - gp.player.worldX + gp.player.screenX;
		int screenY = worldY - gp.player.worldY + gp.player.screenY;

		if (worldX + width > gp.player.worldX - gp.player.screenX
				&& worldX - width < gp.player.worldX + gp.player.screenX
				&& worldY + height > gp.player.worldY - gp.player.screenY
				&& worldY - height < gp.player.worldY + gp.player.screenY) {
			if (image != null) {
				g2.drawImage(image, screenX, screenY, width, height, null);
			} else {
				g2.setColor(new Color(255, 196, 0));
				g2.fillRect(screenX, screenY, width, height);
				g2.setColor(new Color(140, 80, 0));
				g2.fillRect(screenX + 18, screenY + 10, width - 36, height - 20);
			}
		}
	}
}
