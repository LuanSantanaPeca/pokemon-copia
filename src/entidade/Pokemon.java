package entidade;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

public abstract class Pokemon {

	public enum Tipo {
		PEDRA, PAPEL, TESOURA
	}

	public static final int SPRITE_COUNT = 3;
	public static final int SPRITE_ASSUSTADO = 0;
	public static final int SPRITE_FELIZ = 1;
	public static final int SPRITE_FOFO = 2;

	private static final BufferedImage[][] SPRITES = new BufferedImage[Tipo.values().length][SPRITE_COUNT];
	private static final String[][] SPRITE_FILES = {
			{ "pedra-assustada.png", "pedra-feliz.png", "pedra-fofa.png" },
			{ "papel-assustado.png", "papel-feliz.png", "papel-fofo.png" },
			{ "tesoura-assustada.png", "tesoura-feliz.png", "tesoura-fofa.png" }
	};

	static {
		loadSprites();
	}

	protected String nome;
	protected Tipo tipo;
	protected int vida;
	protected int vidaMaxima;
	protected int ataque;

	protected int velocidade;
	protected double dificuldadeCaptura;
	protected Color cor;
	protected int indiceSprite;

	protected Pokemon(String nome, int vidaMaxima, int ataque, int velocidade, Tipo tipo, double dificuldadeCaptura,
			Color cor, int indiceSprite) {
		this.nome = nome;
		this.vidaMaxima = vidaMaxima;
		this.vida = vidaMaxima;
		this.ataque = ataque;
		this.velocidade = velocidade;
		this.tipo = tipo;
		this.dificuldadeCaptura = dificuldadeCaptura;
		this.cor = cor;
		this.indiceSprite = clampSpriteIndex(indiceSprite);
	}

	public int atacar(Pokemon alvo) {
		int dano = calcularDano(alvo);
		alvo.receberDano(dano);
		return dano;
	}

	protected int calcularDano(Pokemon alvo) {
		return Math.max(1, (int) Math.round(ataque * calcularVantagem(alvo)));
	}

	protected abstract double calcularVantagem(Pokemon alvo);

	public void receberDano(int quantidade) {
		if (quantidade < 0) {
			quantidade = 0;
		}
		vida = Math.max(0, vida - quantidade);
	}

	public abstract Pokemon copy();

	protected void copiarEstadoDe(Pokemon origem) {
		this.vida = origem.vida;
	}

	public String getNome() {
		return nome;
	}

	public Tipo getTipo() {
		return tipo;
	}

	public int getVida() {
		return vida;
	}

	public int getVidaMaxima() {
		return vidaMaxima;
	}

	public int getAtaque() {
		return ataque;
	}

	public int getVelocidade() {
		return velocidade;
	}

	public double getDificuldadeCaptura() {
		return dificuldadeCaptura;
	}

	public Color getCor() {
		return cor;
	}

	public int getIndiceSprite() {
		return indiceSprite;
	}

	public String typeLabel() {
		return typeLabel(tipo);
	}

	public static String typeLabel(Tipo tipo) {
		switch (tipo) {
		case PEDRA:
			return "Pedra";
		case PAPEL:
			return "Papel";
		case TESOURA:
			return "Tesoura";
		default:
			return tipo.name();
		}
	}

	public static String strongAgainst(Tipo tipo) {
		Pokemon atacante = create(tipo, SPRITE_FELIZ);
		for (Tipo other : Tipo.values()) {
			if (atacante.calcularVantagem(create(other, SPRITE_FELIZ)) > 1.0) {
				return typeLabel(other);
			}
		}
		return "-";
	}

	public static String weakAgainst(Tipo tipo) {
		Pokemon atacante = create(tipo, SPRITE_FELIZ);
		for (Tipo other : Tipo.values()) {
			if (atacante.calcularVantagem(create(other, SPRITE_FELIZ)) < 1.0) {
				return typeLabel(other);
			}
		}
		return "-";
	}

	public BufferedImage sprite() {
		return spriteOf(tipo, indiceSprite);
	}

	public static BufferedImage spriteOf(Tipo tipo, int spriteIndex) {
		if (tipo == null) {
			return null;
		}
		return SPRITES[tipo.ordinal()][clampSpriteIndex(spriteIndex)];
	}

	public void drawIcon(Graphics2D g2, int x, int y, int size) {
		drawSprite(g2, tipo, indiceSprite, x, y, size, false);
	}

	public void drawSprite(Graphics2D g2, int x, int y, int size, boolean flipHorizontal) {
		drawSprite(g2, tipo, indiceSprite, x, y, size, flipHorizontal);
	}

	public static void drawSprite(Graphics2D g2, Tipo tipo, int spriteIndex, int x, int y, int size,
			boolean flipHorizontal) {
		BufferedImage image = spriteOf(tipo, spriteIndex);
		if (image == null) {
			g2.setColor(fallbackColor(tipo));
			g2.fillRect(x, y, size, size);
			g2.setColor(Color.BLACK);
			g2.drawRect(x, y, size, size);
			return;
		}
		Object previous = g2.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		if (flipHorizontal) {
			g2.drawImage(image, x + size, y, -size, size, null);
		} else {
			g2.drawImage(image, x, y, size, size, null);
		}
		if (previous != null) {
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, previous);
		}
	}

	public boolean isFainted() {
		return vida <= 0;
	}

	public void heal(int amount) {
		vida = Math.min(vidaMaxima, vida + amount);
	}

	public void fullHeal() {
		vida = vidaMaxima;
	}

	public static Pokemon rocco() {
		return rocco(randomSpriteIndex());
	}

	public static Pokemon rocco(int spriteIndex) {
		return new PokemonPedra(spriteIndex);
	}

	public static Pokemon papelon() {
		return papelon(randomSpriteIndex());
	}

	public static Pokemon papelon(int spriteIndex) {
		return new PokemonPapel(spriteIndex);
	}

	public static Pokemon tesourin() {
		return tesourin(randomSpriteIndex());
	}

	public static Pokemon tesourin(int spriteIndex) {
		return new PokemonTesoura(spriteIndex);
	}

	public static Pokemon createRandom(Tipo tipo) {
		return create(tipo, randomSpriteIndex());
	}

	public static Pokemon create(Tipo tipo, int spriteIndex) {
		switch (tipo) {
		case PEDRA:
			return rocco(spriteIndex);
		case PAPEL:
			return papelon(spriteIndex);
		case TESOURA:
			return tesourin(spriteIndex);
		default:
			return papelon(spriteIndex);
		}
	}

	public static Pokemon[] allSpecies() {
		return new Pokemon[] { rocco(SPRITE_FELIZ), papelon(SPRITE_FELIZ), tesourin(SPRITE_FELIZ) };
	}

	public static Pokemon randomWild() {
		Tipo[] types = Tipo.values();
		return createRandom(types[(int) (Math.random() * types.length)]);
	}

	protected static int randomSpriteIndex() {
		return (int) (Math.random() * SPRITE_COUNT);
	}

	private static int clampSpriteIndex(int spriteIndex) {
		if (spriteIndex < 0) {
			return 0;
		}
		if (spriteIndex >= SPRITE_COUNT) {
			return SPRITE_COUNT - 1;
		}
		return spriteIndex;
	}

	private static Color fallbackColor(Tipo tipo) {
		if (tipo == Tipo.PEDRA) {
			return new Color(121, 85, 72);
		}
		if (tipo == Tipo.TESOURA) {
			return new Color(244, 67, 54);
		}
		return new Color(76, 175, 80);
	}

	private static void loadSprites() {
		for (int type = 0; type < SPRITE_FILES.length; type++) {
			for (int variant = 0; variant < SPRITE_FILES[type].length; variant++) {
				String path = "/pokemon/" + SPRITE_FILES[type][variant];
				try (InputStream stream = Pokemon.class.getResourceAsStream(path)) {
					if (stream != null) {
						SPRITES[type][variant] = ImageIO.read(stream);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
