package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import entidade.Pokemon;

public class TotemManager {

	private enum Phase {
		MAIN, ORGANIZE, PICK_TEAM, PICK_STORAGE, PICK_TEAM_SECOND, POKEDEX, MESSAGE
	}

	private enum OrganizeAction {
		SWAP, SEND, WITHDRAW, REORDER
	}

	private static final String[] MAIN_ACTIONS = { "Restaurar Pokemon", "Organizar equipe", "Pokedex", "Sair" };
	private static final String[] ORGANIZE_ACTIONS = {
			"Trocar com o totem",
			"Enviar para o totem",
			"Trazer para a equipe",
			"Reordenar equipe",
			"Voltar"
	};

	Painel gp;
	private Phase phase = Phase.MAIN;
	private OrganizeAction organizeAction = OrganizeAction.SWAP;
	private int menuIndex = 0;
	private int listIndex = 0;
	private int firstTeamIndex = -1;
	private boolean ignoreNextEnter = false;
	private String message = "";

	private final Font uiFont = new Font("Arial", Font.BOLD, 16);
	private final Font smallFont = new Font("Arial", Font.PLAIN, 14);
	private final Font questionFont = new Font("Arial", Font.BOLD, 96);

	public TotemManager(Painel gp) {
		this.gp = gp;
	}

	public void open() {
		gp.gameState = Painel.TOTEM;
		phase = Phase.MAIN;
		menuIndex = 0;
		listIndex = 0;
		firstTeamIndex = -1;
		ignoreNextEnter = true;
		message = "";
	}

	public void update() {
		if (gp.gameState != Painel.TOTEM) {
			return;
		}
		if (ignoreNextEnter) {
			ignoreNextEnter = false;
			return;
		}

		switch (phase) {
		case MAIN:
			moveCursor(MAIN_ACTIONS.length);
			if (gp.keyH.enterMenu) {
				confirmMain();
			}
			break;
		case ORGANIZE:
			moveCursor(ORGANIZE_ACTIONS.length);
			if (gp.keyH.enterMenu) {
				confirmOrganize();
			}
			break;
		case PICK_TEAM:
			updateTeamPick();
			break;
		case PICK_STORAGE:
			updateStoragePick();
			break;
		case PICK_TEAM_SECOND:
			updateTeamPick();
			break;
		case POKEDEX:
			updatePokedex();
			break;
		case MESSAGE:
			if (gp.keyH.enterMenu) {
				phase = Phase.MAIN;
				menuIndex = 0;
			}
			break;
		}
	}

	private void moveCursor(int count) {
		if (count <= 0) {
			return;
		}
		if (gp.keyH.upMenu || gp.keyH.leftMenu) {
			menuIndex = (menuIndex - 1 + count) % count;
		}
		if (gp.keyH.downMenu || gp.keyH.rightMenu) {
			menuIndex = (menuIndex + 1) % count;
		}
	}

	private void confirmMain() {
		switch (menuIndex) {
		case 0:
			gp.treinador.healAll();
			showMessage("Seus Pokemon foram totalmente restaurados!");
			break;
		case 1:
			phase = Phase.ORGANIZE;
			menuIndex = 0;
			break;
		case 2:
			phase = Phase.POKEDEX;
			menuIndex = 0;
			break;
		case 3:
			gp.gameState = Painel.PLAY;
			break;
		}
	}

	private void confirmOrganize() {
		switch (menuIndex) {
		case 0:
			if (gp.treinador.storage.isEmpty()) {
				showMessage("Nao ha Pokemon guardados no totem.");
			} else {
				organizeAction = OrganizeAction.SWAP;
				startTeamPick();
			}
			break;
		case 1:
			if (gp.treinador.equipe.size() <= 1) {
				showMessage("A equipe precisa de pelo menos um Pokemon.");
			} else {
				organizeAction = OrganizeAction.SEND;
				startTeamPick();
			}
			break;
		case 2:
			if (gp.treinador.storage.isEmpty()) {
				showMessage("Nao ha Pokemon guardados no totem.");
			} else if (gp.treinador.isTeamFull()) {
				showMessage("A equipe esta cheia. Use Trocar com o totem.");
			} else {
				organizeAction = OrganizeAction.WITHDRAW;
				startStoragePick();
			}
			break;
		case 3:
			if (gp.treinador.equipe.size() < 2) {
				showMessage("E preciso ter pelo menos dois Pokemon na equipe.");
			} else {
				organizeAction = OrganizeAction.REORDER;
				startTeamPick();
			}
			break;
		case 4:
			phase = Phase.MAIN;
			menuIndex = 1;
			break;
		}
	}

	private void updatePokedex() {
		int count = gp.pokedex.species().length + 1;
		moveCursor(count);
		if (gp.keyH.enterMenu && menuIndex >= gp.pokedex.species().length) {
			phase = Phase.MAIN;
			menuIndex = 2;
		}
	}

	private void startTeamPick() {
		listIndex = 0;
		firstTeamIndex = -1;
		phase = Phase.PICK_TEAM;
	}

	private void startStoragePick() {
		listIndex = 0;
		phase = Phase.PICK_STORAGE;
	}

	private void updateTeamPick() {
		int backIndex = gp.treinador.equipe.size();
		moveList(backIndex + 1);
		if (!gp.keyH.enterMenu) {
			return;
		}
		if (listIndex >= backIndex) {
			cancelPick();
			return;
		}
		if (phase == Phase.PICK_TEAM && organizeAction == OrganizeAction.REORDER && firstTeamIndex < 0) {
			firstTeamIndex = listIndex;
			phase = Phase.PICK_TEAM_SECOND;
			return;
		}
		if (phase == Phase.PICK_TEAM_SECOND) {
			if (gp.treinador.swapTeamSlots(firstTeamIndex, listIndex)) {
				showMessage("A equipe foi reordenada.");
			} else {
				showMessage("Escolha dois Pokemon diferentes.");
				phase = Phase.ORGANIZE;
				menuIndex = 3;
			}
			return;
		}
		if (organizeAction == OrganizeAction.SEND) {
			Pokemon sent = gp.treinador.equipe.get(listIndex);
			if (gp.treinador.sendToStorage(listIndex)) {
				showMessage(sent.getNome() + " foi enviado ao totem.");
			} else {
				showMessage("A equipe precisa de pelo menos um Pokemon.");
			}
			return;
		}
		if (organizeAction == OrganizeAction.SWAP) {
			firstTeamIndex = listIndex;
			startStoragePick();
		}
	}

	private void updateStoragePick() {
		int backIndex = gp.treinador.storage.size();
		moveList(backIndex + 1);
		if (!gp.keyH.enterMenu) {
			return;
		}
		if (listIndex >= backIndex) {
			cancelPick();
			return;
		}
		if (organizeAction == OrganizeAction.WITHDRAW) {
			Pokemon withdrawn = gp.treinador.storage.get(listIndex);
			if (gp.treinador.withdrawFromStorage(listIndex)) {
				showMessage(withdrawn.getNome() + " entrou na equipe.");
			} else {
				showMessage("A equipe esta cheia.");
			}
			return;
		}
		if (organizeAction == OrganizeAction.SWAP) {
			Pokemon fromTeam = gp.treinador.equipe.get(firstTeamIndex);
			Pokemon fromStorage = gp.treinador.storage.get(listIndex);
			if (gp.treinador.swapTeamAndStorage(firstTeamIndex, listIndex)) {
				showMessage("Trocou " + fromTeam.getNome() + " por " + fromStorage.getNome() + ".");
			} else {
				showMessage("Nao foi possivel trocar.");
			}
		}
	}

	private void moveList(int count) {
		if (count <= 0) {
			return;
		}
		if (gp.keyH.upMenu || gp.keyH.leftMenu) {
			listIndex = (listIndex - 1 + count) % count;
		}
		if (gp.keyH.downMenu || gp.keyH.rightMenu) {
			listIndex = (listIndex + 1) % count;
		}
	}

	private void cancelPick() {
		phase = Phase.ORGANIZE;
		menuIndex = switch (organizeAction) {
		case SWAP -> 0;
		case SEND -> 1;
		case WITHDRAW -> 2;
		case REORDER -> 3;
		};
		firstTeamIndex = -1;
	}

	private void showMessage(String text) {
		message = text;
		phase = Phase.MESSAGE;
	}

	public void draw(Graphics2D g2) {
		if (phase == Phase.POKEDEX) {
			drawPokedex(g2);
			return;
		}

		int boxW = 520;
		int boxH = 280;
		int boxX = gp.larguraTela / 2 - boxW / 2;
		int boxY = gp.comprimentoTela / 2 - boxH / 2;

		g2.setColor(new Color(20, 20, 20, 220));
		g2.fillRoundRect(boxX, boxY, boxW, boxH, 12, 12);
		g2.setColor(Color.WHITE);
		g2.drawRoundRect(boxX, boxY, boxW, boxH, 12, 12);
		g2.setFont(uiFont);
		g2.drawString("Totem", boxX + 16, boxY + 28);

		if (phase == Phase.MESSAGE) {
			g2.setFont(smallFont);
			g2.drawString(message, boxX + 16, boxY + 90);
			g2.drawString("Enter para continuar", boxX + 16, boxY + boxH - 24);
			return;
		}

		if (phase == Phase.MAIN) {
			drawList(g2, boxX, boxY + 20, MAIN_ACTIONS, menuIndex);
			return;
		}

		if (phase == Phase.ORGANIZE) {
			g2.setFont(smallFont);
			g2.drawString("Equipe: " + gp.treinador.equipe.size() + "/6   No totem: " + gp.treinador.storage.size(),
					boxX + 16, boxY + 52);
			drawList(g2, boxX, boxY + 40, ORGANIZE_ACTIONS, menuIndex);
			return;
		}

		if (phase == Phase.PICK_TEAM || phase == Phase.PICK_TEAM_SECOND) {
			String title = phase == Phase.PICK_TEAM_SECOND
					? "Escolha o segundo Pokemon da equipe"
					: organizeAction == OrganizeAction.SEND
							? "Enviar qual Pokemon ao totem?"
							: organizeAction == OrganizeAction.SWAP
									? "Escolha um Pokemon da equipe"
									: "Reordenar: escolha o primeiro Pokemon";
			drawPokemonPick(g2, boxX, boxY, title, gp.treinador.equipe, listIndex);
			return;
		}

		if (phase == Phase.PICK_STORAGE) {
			String title = organizeAction == OrganizeAction.WITHDRAW
					? "Trazer qual Pokemon para a equipe?"
					: "Escolha um Pokemon do totem";
			drawPokemonPick(g2, boxX, boxY, title, gp.treinador.storage, listIndex);
		}
	}

	private void drawPokemonPick(Graphics2D g2, int boxX, int boxY, String title, List<Pokemon> list, int selected) {
		g2.setFont(smallFont);
		g2.drawString(title, boxX + 16, boxY + 52);
		List<String> labels = new ArrayList<>();
		for (Pokemon pokemon : list) {
			labels.add(pokemon.getNome() + "  HP " + pokemon.getVida() + "/" + pokemon.getVidaMaxima() + "  " + pokemon.typeLabel());
		}
		labels.add("Voltar");
		drawList(g2, boxX, boxY + 36, labels.toArray(new String[0]), selected);
	}

	private void drawList(Graphics2D g2, int boxX, int boxY, String[] labels, int selected) {
		g2.setFont(smallFont);
		for (int i = 0; i < labels.length; i++) {
			int textY = boxY + 48 + i * 24;
			if (i == selected) {
				g2.setColor(Color.YELLOW);
				g2.drawString("> " + labels[i], boxX + 16, textY);
			} else {
				g2.setColor(Color.WHITE);
				g2.drawString(labels[i], boxX + 16, textY);
			}
		}
	}

	private void drawPokedex(Graphics2D g2) {
		int boxW = 720;
		int boxH = 460;
		int boxX = gp.larguraTela / 2 - boxW / 2;
		int boxY = gp.comprimentoTela / 2 - boxH / 2;

		g2.setColor(new Color(20, 20, 20, 230));
		g2.fillRoundRect(boxX, boxY, boxW, boxH, 12, 12);
		g2.setColor(Color.WHITE);
		g2.drawRoundRect(boxX, boxY, boxW, boxH, 12, 12);
		g2.setFont(uiFont);
		g2.drawString("Pokedex", boxX + 16, boxY + 28);

		Pokemon[] species = gp.pokedex.species();
		int selected = Math.min(menuIndex, species.length);
		if (menuIndex < species.length) {
			Pokemon pokemon = species[menuIndex];
			Pokedex.State state = gp.pokedex.stateOf(pokemon.getNome());
			int spriteSize = 112;
			int gap = 8;
			int iconX = boxX + 28;
			int iconY = boxY + 56;
			Font spriteQuestion = questionFont.deriveFont(52f);

			for (int i = 0; i < Pokemon.SPRITE_COUNT; i++) {
				int spriteX = iconX + i * (spriteSize + gap);
				g2.setColor(new Color(8, 8, 8, 180));
				g2.fillRect(spriteX, iconY, spriteSize, spriteSize);
				g2.setColor(Color.WHITE);
				g2.drawRect(spriteX, iconY, spriteSize, spriteSize);

				if (gp.pokedex.hasSeenSprite(pokemon.getNome(), i)) {
					Pokemon.drawSprite(g2, pokemon.getTipo(), i, spriteX, iconY, spriteSize, false);
				} else {
					g2.setFont(spriteQuestion);
					g2.setColor(Color.WHITE);
					FontMetrics qm = g2.getFontMetrics();
					String q = "?";
					g2.drawString(q, spriteX + (spriteSize - qm.stringWidth(q)) / 2,
							iconY + (spriteSize + qm.getAscent()) / 2 - 8);
				}
			}

			boolean caught = state == Pokedex.State.CAUGHT;
			boolean seen = state != Pokedex.State.UNKNOWN;
			String name = seen ? pokemon.getNome() : "?";
			String type = caught ? pokemon.typeLabel() : "?";
			String hp = caught ? String.valueOf(pokemon.getVidaMaxima()) : "?";
			String attack = caught ? String.valueOf(pokemon.getAtaque()) : "?";
			String speed = caught ? String.valueOf(pokemon.getVelocidade()) : "?";
			String strong = caught ? Pokemon.strongAgainst(pokemon.getTipo()) : "?";
			String weak = caught ? Pokemon.weakAgainst(pokemon.getTipo()) : "?";

			g2.setFont(uiFont);
			g2.setColor(Color.WHITE);
			int spritesWidth = Pokemon.SPRITE_COUNT * spriteSize + (Pokemon.SPRITE_COUNT - 1) * gap;
			int textX = iconX + spritesWidth + 24;
			int textY = iconY + 24;
			int line = 28;
			g2.drawString("Nome: " + name, textX, textY);
			g2.drawString("Tipo: " + type, textX, textY + line);
			g2.drawString("Vida: " + hp, textX, textY + line * 2);
			g2.drawString("Ataque: " + attack, textX, textY + line * 3);
			g2.drawString("Velocidade: " + speed, textX, textY + line * 4);
			g2.drawString("Forte contra: " + strong, textX, textY + line * 5);
			g2.drawString("Fraco contra: " + weak, textX, textY + line * 6);
		} else {
			g2.setFont(smallFont);
			g2.setColor(Color.WHITE);
			g2.drawString("Escolha uma entrada para ver os dados.", boxX + 28, boxY + 140);
		}

		String[] labels = new String[species.length + 1];
		for (int i = 0; i < species.length; i++) {
			Pokedex.State state = gp.pokedex.stateOf(species[i].getNome());
			labels[i] = state == Pokedex.State.UNKNOWN ? "?" : species[i].getNome();
		}
		labels[species.length] = "Voltar";
		drawList(g2, boxX, boxY + boxH - 160, labels, selected);
	}
}
