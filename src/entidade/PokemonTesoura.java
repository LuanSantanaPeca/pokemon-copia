package entidade;

import java.awt.Color;

public class PokemonTesoura extends Pokemon {

	public PokemonTesoura() {
		this(randomSpriteIndex());
	}

	public PokemonTesoura(int spriteIndex) {
		super("Tes-ou-ra", 22, 11, 16, Tipo.TESOURA, 0.35, new Color(244, 67, 54), spriteIndex);
	}

	@Override
	protected double calcularVantagem(Pokemon alvo) {
		if (alvo.getTipo() == Tipo.PAPEL) {
			return 1.5;
		}
		if (alvo.getTipo() == Tipo.PEDRA) {
			return 0.7;
		}
		return 1.0;
	}

	@Override
	public Pokemon copy() {
		PokemonTesoura clone = new PokemonTesoura(indiceSprite);
		clone.copiarEstadoDe(this);
		return clone;
	}
}
