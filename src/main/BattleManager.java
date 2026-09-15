package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import entidade.Pokemon;

public class BattleManager {

	private enum Phase {
		INTRO, MENU, ITEM_MENU, REVIVE_LIST, SWAP_LIST, FORCE_SWAP, MESSAGE
	}

	private enum PlayerAction {
		ATTACK, CAPTURE, POTION, REVIVE, SWAP, RUN
	}

	private static final String[] MAIN_ACTIONS = { "Atacar", "Capturar", "Itens", "Fugir", "Trocar" };
	private static final int INTRO_FRAMES = 45;

	Painel gp;
	public Pokemon wild;
	public Pokemon playerActive;
	public int playerIndex;

	private Phase phase = Phase.INTRO;
	private int introFrame = 0;
	private int menuIndex = 0;
	private int subIndex = 0;
	private List<Integer> listOptions = new ArrayList<>();
	private int reviveTarget = -1;
	private int swapTarget = -1;

	private final List<String> messages = new ArrayList<>();
	private int messageIndex = 0;
	private boolean endAfterMessages = false;
	private boolean forceSwapAfterMessages = false;
	private boolean caughtThisTurn = false;

	private final Font uiFont = new Font("Arial", Font.BOLD, 16);
	private final Font smallFont = new Font("Arial", Font.PLAIN, 14);

	public BattleManager(Painel gp) {
		this.gp = gp;
	}

	public void start(Pokemon wildPokemon) {
		this.wild = wildPokemon;
		playerIndex = gp.treinador.firstLivingIndex();
		if (playerIndex < 0) {
			return;
		}
		playerActive = gp.treinador.equipe.get(playerIndex);
		gp.pokedex.markSeen(wild);
		introFrame = 0;
		menuIndex = 0;
		subIndex = 0;
		messages.clear();
		messageIndex = 0;
		endAfterMessages = false;
		forceSwapAfterMessages = false;
		caughtThisTurn = false;
		phase = Phase.INTRO;
		gp.gameState = Painel.BATTLE;
	}

	public void update() {
		if (gp.gameState != Painel.BATTLE) {
			return;
		}

		switch (phase) {
		case INTRO:
			introFrame++;
			if (introFrame >= INTRO_FRAMES) {
				phase = Phase.MENU;
			}
			break;
		case MENU:
			updateMainMenu();
			break;
		case ITEM_MENU:
			updateItemMenu();
			break;
		case REVIVE_LIST:
			updateReviveList();
			break;
		case SWAP_LIST:
			updateSwapList(false);
			break;
		case FORCE_SWAP:
			updateSwapList(true);
			break;
		case MESSAGE:
			updateMessages();
			break;
		}
	}

	private void updateMainMenu() {
		int cols = 2;
		int rows = 3;
		int col = menuIndex % cols;
		int row = menuIndex / cols;

		if (gp.keyH.leftMenu) {
			col = Math.max(0, col - 1);
		}
		if (gp.keyH.rightMenu) {
			col = Math.min(cols - 1, col + 1);
		}
		if (gp.keyH.upMenu) {
			row = Math.max(0, row - 1);
		}
		if (gp.keyH.downMenu) {
			row = Math.min(rows - 1, row + 1);
		}

		int next = row * cols + col;
		if (next > 4) {
			next = 4;
		}
		menuIndex = next;

		if (gp.keyH.enterMenu) {
			confirmMainAction();
		}
	}

	private void confirmMainAction() {
		switch (menuIndex) {
		case 0:
			resolveTurn(PlayerAction.ATTACK, -1);
			break;
		case 1:
			resolveTurn(PlayerAction.CAPTURE, -1);
			break;
		case 2:
			subIndex = 0;
			phase = Phase.ITEM_MENU;
			break;
		case 3:
			resolveTurn(PlayerAction.RUN, -1);
			break;
		case 4:
			listOptions = gp.treinador.livingIndicesExcept(playerIndex);
			if (listOptions.isEmpty()) {
				beginMessages(List.of("Nenhum outro Pokemon para trocar!"), false, false);
			} else {
				subIndex = 0;
				phase = Phase.SWAP_LIST;
			}
			break;
		}
	}

	private void updateItemMenu() {
		if (gp.keyH.upMenu || gp.keyH.leftMenu) {
			subIndex = (subIndex + 2) % 3;
		}
		if (gp.keyH.downMenu || gp.keyH.rightMenu) {
			subIndex = (subIndex + 1) % 3;
		}
		if (gp.keyH.enterMenu) {
			if (subIndex == 0) {
				if (gp.treinador.potions <= 0) {
					beginMessages(List.of("Sem pocoes restantes!"), false, false);
				} else if (playerActive.getVida() >= playerActive.getVidaMaxima()) {
					beginMessages(List.of(playerActive.getNome() + " ja esta com a vida cheia!"), false, false);
				} else if (playerActive.isFainted()) {
					beginMessages(List.of("Pocoes nao revivem um Pokemon desmaiado!"), false, false);
				} else {
					resolveTurn(PlayerAction.POTION, -1);
				}
			} else if (subIndex == 1) {
				listOptions = gp.treinador.faintedIndices();
				if (gp.treinador.revives <= 0) {
					beginMessages(List.of("Sem reviver restantes!"), false, false);
				} else if (listOptions.isEmpty()) {
					beginMessages(List.of("Nenhum Pokemon desmaiado para reviver!"), false, false);
				} else {
					subIndex = 0;
					phase = Phase.REVIVE_LIST;
				}
			} else {
				phase = Phase.MENU;
			}
		}
	}

	private void updateReviveList() {
		moveListCursor();
		if (gp.keyH.enterMenu) {
			if (subIndex >= listOptions.size()) {
				phase = Phase.ITEM_MENU;
				subIndex = 1;
			} else {
				resolveTurn(PlayerAction.REVIVE, listOptions.get(subIndex));
			}
		}
	}

	private void updateSwapList(boolean forced) {
		moveListCursor();
		if (gp.keyH.enterMenu) {
			if (!forced && subIndex >= listOptions.size()) {
				phase = Phase.MENU;
			} else if (subIndex < listOptions.size()) {
				int target = listOptions.get(subIndex);
				if (forced) {
					performSwap(target);
					beginMessages(List.of("Va, " + playerActive.getNome() + "!"), false, false);
				} else {
					resolveTurn(PlayerAction.SWAP, target);
				}
			}
		}
	}

	private void moveListCursor() {
		int max = listOptions.size() + (phase == Phase.FORCE_SWAP ? 0 : 1);
		if (max <= 0) {
			return;
		}
		if (gp.keyH.upMenu || gp.keyH.leftMenu) {
			subIndex = (subIndex - 1 + max) % max;
		}
		if (gp.keyH.downMenu || gp.keyH.rightMenu) {
			subIndex = (subIndex + 1) % max;
		}
	}

	private void updateMessages() {
		if (gp.keyH.enterMenu) {
			messageIndex++;
			if (messageIndex >= messages.size()) {
				finishMessages();
			}
		}
	}

	private void beginMessages(List<String> lines, boolean endBattle, boolean forceSwap) {
		messages.clear();
		messages.addAll(lines);
		messageIndex = 0;
		endAfterMessages = endBattle;
		forceSwapAfterMessages = forceSwap;
		if (messages.isEmpty()) {
			finishMessages();
			return;
		}
		phase = Phase.MESSAGE;
	}

	private void finishMessages() {
		if (endAfterMessages) {
			endBattle();
			return;
		}
		if (forceSwapAfterMessages) {
			openForceSwap();
			return;
		}
		phase = Phase.MENU;
		menuIndex = 0;
	}

	private void openForceSwap() {
		listOptions = gp.treinador.livingIndicesExcept(playerIndex);
		if (listOptions.isEmpty() || !gp.treinador.hasLivingPokemon()) {
			endBattle();
			return;
		}
		subIndex = 0;
		phase = Phase.FORCE_SWAP;
	}

	private void resolveTurn(PlayerAction action, int extraIndex) {
		reviveTarget = extraIndex;
		swapTarget = extraIndex;
		caughtThisTurn = false;
		List<String> log = new ArrayList<>();
		boolean playerFirst = playerActive.getVelocidade() >= wild.getVelocidade();

		if (action == PlayerAction.RUN) {
			beginMessages(List.of("Fugiu em seguranca!"), true, false);
			return;
		}

		if (playerFirst) {
			performPlayerAction(action, log);
			if (wild.isFainted()) {
				log.add("O " + wild.getNome() + " selvagem desmaiou!");
				beginMessages(log, true, false);
				return;
			}
			if (caughtThisTurn) {
				beginMessages(log, true, false);
				return;
			}
			if (!playerActive.isFainted()) {
				performWildAttack(log);
			}
		} else {
			performWildAttack(log);
			if (!playerActive.isFainted() && !wild.isFainted()) {
				performPlayerAction(action, log);
				if (wild.isFainted()) {
					log.add("O " + wild.getNome() + " selvagem desmaiou!");
					beginMessages(log, true, false);
					return;
				}
				if (caughtThisTurn) {
					beginMessages(log, true, false);
					return;
				}
			}
		}

		boolean playerFainted = playerActive.isFainted();
		boolean canSwap = gp.treinador.hasLivingPokemon() && !gp.treinador.livingIndicesExcept(playerIndex).isEmpty();
		if (playerFainted && !canSwap) {
			log.add("Todos os seus Pokemon desmaiaram!");
			beginMessages(log, true, false);
			return;
		}
		beginMessages(log, false, playerFainted);
	}

	private void performPlayerAction(PlayerAction action, List<String> log) {
		switch (action) {
		case ATTACK:
			int damage = playerActive.atacar(wild);
			log.add(playerActive.getNome() + " atacou! Causou " + damage + " de dano.");
			break;
		case CAPTURE:
			tryCapture(log);
			break;
		case POTION:
			gp.treinador.potions--;
			playerActive.heal(Treinador.POTION_HEAL);
			log.add("Usou uma Pocao! " + playerActive.getNome() + " recuperou vida.");
			break;
		case REVIVE:
			gp.treinador.revives--;
			Pokemon fainted = gp.treinador.equipe.get(reviveTarget);
			fainted.fullHeal();
			log.add("Usou um Reviver! " + fainted.getNome() + " foi restaurado.");
			break;
		case SWAP:
			Pokemon previous = playerActive;
			performSwap(swapTarget);
			log.add(previous.getNome() + ", volte! Va, " + playerActive.getNome() + "!");
			break;
		case RUN:
			break;
		}
	}

	private void tryCapture(List<String> log) {
		if (wild.isFainted()) {
			log.add("Nao ha nada para capturar!");
			return;
		}
		if (Batalha.capturar(wild, Math.random())) {
			Pokemon caught = wild.copy();
			caughtThisTurn = true;
			gp.pokedex.markCaught(caught);
			if (gp.treinador.addPokemon(caught)) {
				log.add("Conseguiu! " + wild.getNome() + " foi capturado!");
			} else {
				gp.treinador.storePokemon(caught);
				log.add("Equipe cheia! " + wild.getNome() + " foi enviado ao totem.");
			}
		} else {
			log.add(wild.getNome() + " escapou!");
		}
	}

	private void performWildAttack(List<String> log) {
		int damage = wild.atacar(playerActive);
		log.add("O " + wild.getNome() + " selvagem atacou! Causou " + damage + " de dano.");
		if (playerActive.isFainted()) {
			log.add(playerActive.getNome() + " desmaiou!");
		}
	}

	private void performSwap(int newIndex) {
		playerIndex = newIndex;
		playerActive = gp.treinador.equipe.get(playerIndex);
	}

	private void endBattle() {
		wild = null;
		gp.gameState = Painel.PLAY;
		phase = Phase.INTRO;
	}

	public void draw(Graphics2D g2) {
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, gp.larguraTela, gp.comprimentoTela);
		g2.setComposite(AlphaComposite.SrcOver);

		float t = Math.min(1f, introFrame / (float) INTRO_FRAMES);
		int midY = gp.comprimentoTela / 2;
		int pokemonSize = midY;
		int playerTargetX = 48;
		int playerTargetY = midY;
		int wildTargetX = gp.larguraTela - pokemonSize - 48;
		int wildTargetY = midY - pokemonSize;
		int playerX = Math.round(-pokemonSize + (playerTargetX + pokemonSize) * t);
		int wildX = Math.round(gp.larguraTela - (gp.larguraTela - wildTargetX) * t);

		drawPokemon(g2, playerActive, playerX, playerTargetY, pokemonSize, true);
		drawPokemon(g2, wild, wildX, wildTargetY, pokemonSize, false);

		if (phase != Phase.INTRO) {
			drawHud(g2);
		}
	}

	private void drawPokemon(Graphics2D g2, Pokemon pokemon, int x, int y, int size, boolean playerSide) {
		if (pokemon == null) {
			return;
		}
		pokemon.drawSprite(g2, x, y, size, playerSide);

		String label = playerSide ? pokemon.getNome() : pokemon.getNome() + " selvagem";
		String hp = "HP " + pokemon.getVida() + "/" + pokemon.getVidaMaxima();
		String type = pokemon.typeLabel();
		FontMetrics uiMetrics = g2.getFontMetrics(uiFont);
		FontMetrics smallMetrics = g2.getFontMetrics(smallFont);
		int gap = 6;
		int pad = 6;

		if (playerSide) {
			int labelX = x;
			int typeY = y - gap - smallMetrics.getDescent();
			int hpY = typeY - smallMetrics.getHeight();
			int nameY = hpY - smallMetrics.getAscent() - gap - uiMetrics.getDescent();
			int textW = Math.max(uiMetrics.stringWidth(label),
					Math.max(smallMetrics.stringWidth(hp), smallMetrics.stringWidth(type)));
			int boxX = labelX - pad;
			int boxY = nameY - uiMetrics.getAscent() - pad;
			int boxW = textW + pad * 2;
			int boxH = (typeY + smallMetrics.getDescent()) - boxY + pad;
			g2.setColor(new Color(0, 0, 0, 128));
			g2.fillRect(boxX, boxY, boxW, boxH);
			g2.setColor(new Color(255, 255, 255, 220));
			g2.setFont(uiFont);
			g2.drawString(label, labelX, nameY);
			g2.setFont(smallFont);
			g2.drawString(hp, labelX, hpY);
			g2.drawString(type, labelX, typeY);
		} else {
			int nameY = y + uiMetrics.getAscent();
			int hpY = nameY + smallMetrics.getHeight();
			int typeY = hpY + smallMetrics.getHeight();
			int edge = x - gap;
			int nameX = edge - uiMetrics.stringWidth(label);
			int hpX = edge - smallMetrics.stringWidth(hp);
			int typeX = edge - smallMetrics.stringWidth(type);
			int boxRight = edge + pad;
			int boxLeft = Math.min(nameX, Math.min(hpX, typeX)) - pad;
			int boxY = nameY - uiMetrics.getAscent() - pad;
			int boxH = (typeY + smallMetrics.getDescent()) - boxY + pad;
			g2.setColor(new Color(0, 0, 0, 128));
			g2.fillRect(boxLeft, boxY, boxRight - boxLeft, boxH);
			g2.setColor(new Color(255, 255, 255, 220));
			g2.setFont(uiFont);
			g2.drawString(label, nameX, nameY);
			g2.setFont(smallFont);
			g2.drawString(hp, hpX, hpY);
			g2.drawString(type, typeX, typeY);
		}
	}

	private void drawHud(Graphics2D g2) {
		int boxX = gp.larguraTela / 2 - 220;
		int boxY = gp.comprimentoTela - 150;
		int boxW = 440;
		int boxH = 130;

		g2.setColor(new Color(20, 20, 20, 210));
		g2.fillRoundRect(boxX, boxY, boxW, boxH, 12, 12);
		g2.setColor(Color.WHITE);
		g2.drawRoundRect(boxX, boxY, boxW, boxH, 12, 12);
		g2.setFont(uiFont);

		if (phase == Phase.MESSAGE) {
			if (messages.isEmpty()) {
				return;
			}
			g2.drawString(messages.get(Math.min(messageIndex, messages.size() - 1)), boxX + 16, boxY + 50);
			g2.setFont(smallFont);
			g2.drawString("Enter para continuar", boxX + 16, boxY + 100);
			return;
		}

		if (phase == Phase.MENU) {
			for (int i = 0; i < MAIN_ACTIONS.length; i++) {
				int col = i % 2;
				int row = i / 2;
				int textX = boxX + 24 + col * 150;
				int textY = boxY + 32 + row * 32;
				if (i == menuIndex) {
					g2.setColor(Color.YELLOW);
					g2.drawString("> " + MAIN_ACTIONS[i], textX, textY);
				} else {
					g2.setColor(Color.WHITE);
					g2.drawString(MAIN_ACTIONS[i], textX, textY);
				}
			}
			return;
		}

		if (phase == Phase.ITEM_MENU) {
			drawList(g2, boxX, boxY, new String[] {
					"Pocao x" + gp.treinador.potions,
					"Reviver x" + gp.treinador.revives,
					"Voltar"
			}, subIndex);
			return;
		}

		List<String> labels = new ArrayList<>();
		for (int index : listOptions) {
			Pokemon p = gp.treinador.equipe.get(index);
			labels.add(p.getNome() + "  HP " + p.getVida() + "/" + p.getVidaMaxima());
		}
		if (phase != Phase.FORCE_SWAP) {
			labels.add("Voltar");
		}
		String title = phase == Phase.REVIVE_LIST ? "Reviver qual Pokemon?"
				: phase == Phase.FORCE_SWAP ? "Escolha um Pokemon!"
				: "Trocar para qual Pokemon?";
		g2.drawString(title, boxX + 16, boxY + 24);
		drawList(g2, boxX, boxY + 16, labels.toArray(new String[0]), subIndex);
	}

	private void drawList(Graphics2D g2, int boxX, int boxY, String[] labels, int selected) {
		g2.setFont(smallFont);
		for (int i = 0; i < labels.length; i++) {
			int textY = boxY + 40 + i * 22;
			if (i == selected) {
				g2.setColor(Color.YELLOW);
				g2.drawString("> " + labels[i], boxX + 16, textY);
			} else {
				g2.setColor(Color.WHITE);
				g2.drawString(labels[i], boxX + 16, textY);
			}
		}
	}
}
