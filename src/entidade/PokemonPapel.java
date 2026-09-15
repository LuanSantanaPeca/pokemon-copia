package entidade;

import java.awt.Color;

public class PokemonPapel extends Pokemon {

	public PokemonPapel() {
		this(randomSpriteIndex());
	}

	public PokemonPapel(int spriteIndex) {
		super("Palpel", 30, 10, 10, Tipo.PAPEL, 0.6, new Color(76, 175, 80), spriteIndex);
	}

	@Override
	protected double calcularVantagem(Pokemon alvo) {
		if (alvo.getTipo() == Tipo.PEDRA) {
			return 1.5;
		}
		if (alvo.getTipo() == Tipo.TESOURA) {
			return 0.7;
		}
		return 1.0;
	}

	@Override
	public Pokemon copy() {
		PokemonPapel clone = new PokemonPapel(indiceSprite);
		clone.copiarEstadoDe(this);
		return clone;
	}
}
