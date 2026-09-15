package entidade;

import java.awt.Color;

public class PokemonPedra extends Pokemon {

	public PokemonPedra() {
		this(randomSpriteIndex());
	}

	public PokemonPedra(int spriteIndex) {
		super("Arrocha", 40, 12, 6, Tipo.PEDRA, 0.9, new Color(121, 85, 72), spriteIndex);
	}

	@Override
	protected double calcularVantagem(Pokemon alvo) {
		if (alvo.getTipo() == Tipo.TESOURA) {
			return 1.5;
		}
		if (alvo.getTipo() == Tipo.PAPEL) {
			return 0.7;
		}
		return 1.0;
	}

	@Override
	public Pokemon copy() {
		PokemonPedra clone = new PokemonPedra(indiceSprite);
		clone.copiarEstadoDe(this);
		return clone;
	}
}
