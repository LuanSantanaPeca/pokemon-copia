package main;

import java.util.ArrayList;

import entidade.Pokemon;

public class Pokedex {

	public enum State {
		UNKNOWN, SEEN, CAUGHT
	}

	private final ArrayList<Pokemon> registrados = new ArrayList<>();
	private final ArrayList<State> estados = new ArrayList<>();
	private final ArrayList<boolean[]> spritesVistos = new ArrayList<>();

	public Pokedex() {
		for (Pokemon species : Pokemon.allSpecies()) {
			registrados.add(species);
			estados.add(State.UNKNOWN);
			spritesVistos.add(new boolean[Pokemon.SPRITE_COUNT]);
		}
	}

	public ArrayList<Pokemon> getRegistrados() {
		return registrados;
	}

	public void markSeen(Pokemon pokemon) {
		if (pokemon == null) {
			return;
		}
		int index = indiceDe(pokemon.getNome());
		if (index < 0) {
			return;
		}
		marcarSprite(index, pokemon);
		if (estados.get(index) == State.CAUGHT) {
			return;
		}
		estados.set(index, State.SEEN);
	}

	public void markCaught(Pokemon pokemon) {
		if (pokemon == null) {
			return;
		}
		int index = indiceDe(pokemon.getNome());
		if (index < 0) {
			return;
		}
		marcarSprite(index, pokemon);
		estados.set(index, State.CAUGHT);
	}

	public boolean hasSeenSprite(String name, int spriteIndex) {
		int index = indiceDe(name);
		if (index < 0) {
			return false;
		}
		boolean[] seen = spritesVistos.get(index);
		return spriteIndex >= 0 && spriteIndex < seen.length && seen[spriteIndex];
	}

	public State stateOf(String name) {
		int index = indiceDe(name);
		if (index < 0) {
			return State.UNKNOWN;
		}
		return estados.get(index);
	}

	public Pokemon[] species() {
		return registrados.toArray(new Pokemon[0]);
	}

	public boolean jaRegistrado(Pokemon pokemon) {
		return pokemon != null && indiceDe(pokemon.getNome()) >= 0;
	}

	private int indiceDe(String nome) {
		for (int i = 0; i < registrados.size(); i++) {
			if (registrados.get(i).getNome().equals(nome)) {
				return i;
			}
		}
		return -1;
	}

	private void marcarSprite(int index, Pokemon pokemon) {
		boolean[] seen = spritesVistos.get(index);
		int spriteIndex = pokemon.getIndiceSprite();
		if (spriteIndex >= 0 && spriteIndex < seen.length) {
			seen[spriteIndex] = true;
		}
	}
}
