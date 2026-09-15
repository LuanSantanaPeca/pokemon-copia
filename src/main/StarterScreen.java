package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import entidade.Pokemon;

public class StarterScreen {

	Painel gp;
	private int selected = 0;
	private final Pokemon[] starters = new Pokemon[] { Pokemon.rocco(), Pokemon.papelon(), Pokemon.tesourin() };
	private final Font titleFont = new Font("Arial", Font.BOLD, 28);
	private final Font uiFont = new Font("Arial", Font.BOLD, 18);
	private final Font hintFont = new Font("Arial", Font.PLAIN, 14);

	public StarterScreen(Painel gp) {
		this.gp = gp;
	}

	public void update() {
		int count = starters.length;
		if (gp.keyH.leftMenu || gp.keyH.upMenu) {
			selected = (selected - 1 + count) % count;
		}
		if (gp.keyH.rightMenu || gp.keyH.downMenu) {
			selected = (selected + 1) % count;
		}
		if (gp.keyH.enterMenu) {
			Pokemon chosen = starters[selected];
			gp.treinador.equipe.add(chosen);
			gp.pokedex.markCaught(chosen);
			gp.gameState = Painel.PLAY;
		}
	}

	public void draw(Graphics2D g2) {
		g2.setColor(new Color(12, 16, 22));
		g2.fillRect(0, 0, gp.larguraTela, gp.comprimentoTela);

		g2.setFont(titleFont);
		g2.setColor(Color.WHITE);
		String title = "Escolha seu Pokemon inicial";
		int titleW = g2.getFontMetrics().stringWidth(title);
		g2.drawString(title, gp.larguraTela / 2 - titleW / 2, 70);

		Pokemon[] species = starters;
		int cardW = 280;
		int cardH = 380;
		int gap = 36;
		int totalW = species.length * cardW + (species.length - 1) * gap;
		int startX = gp.larguraTela / 2 - totalW / 2;
		int cardY = 120;

		for (int i = 0; i < species.length; i++) {
			int x = startX + i * (cardW + gap);
			boolean chosen = i == selected;
			g2.setColor(new Color(28, 32, 40, 230));
			g2.fillRoundRect(x, cardY, cardW, cardH, 16, 16);
			g2.setColor(chosen ? Color.YELLOW : Color.WHITE);
			g2.drawRoundRect(x, cardY, cardW, cardH, 16, 16);

			int icon = 200;
			int iconX = x + (cardW - icon) / 2;
			int iconY = cardY + 36;
			species[i].drawIcon(g2, iconX, iconY, icon);

			g2.setFont(uiFont);
			g2.setColor(chosen ? Color.YELLOW : Color.WHITE);
			String type = species[i].typeLabel();
			int typeW = g2.getFontMetrics().stringWidth(type);
			g2.drawString(type, x + (cardW - typeW) / 2, iconY + icon + 48);
		}

		g2.setFont(hintFont);
		g2.setColor(new Color(200, 200, 200));
		String hint = "A/D para escolher   Enter para confirmar";
		int hintW = g2.getFontMetrics().stringWidth(hint);
		g2.drawString(hint, gp.larguraTela / 2 - hintW / 2, gp.comprimentoTela - 36);
	}
}
