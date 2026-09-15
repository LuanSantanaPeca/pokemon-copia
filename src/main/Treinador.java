package main;

import java.util.ArrayList;
import java.util.List;

import entidade.Pokemon;

public class Treinador {

	public static final int MAX_EQUIPE = 6;
	public static final int POTION_HEAL = 20;

	public ArrayList<Pokemon> equipe = new ArrayList<>();
	public ArrayList<Pokemon> storage = new ArrayList<>();
	public int potions = 5;
	public int revives = 2;

	public Treinador() {
	}

	public boolean isTeamFull() {
		return equipe.size() >= MAX_EQUIPE;
	}

	public boolean addPokemon(Pokemon pokemon) {
		if (isTeamFull()) {
			return false;
		}
		equipe.add(pokemon);
		return true;
	}

	public void storePokemon(Pokemon pokemon) {
		storage.add(pokemon);
	}

	public boolean sendToStorage(int teamIndex) {
		if (teamIndex < 0 || teamIndex >= equipe.size() || equipe.size() <= 1) {
			return false;
		}
		storage.add(equipe.remove(teamIndex));
		return true;
	}

	public boolean withdrawFromStorage(int storageIndex) {
		if (storageIndex < 0 || storageIndex >= storage.size() || isTeamFull()) {
			return false;
		}
		equipe.add(storage.remove(storageIndex));
		return true;
	}

	public boolean swapTeamAndStorage(int teamIndex, int storageIndex) {
		if (teamIndex < 0 || teamIndex >= equipe.size() || storageIndex < 0 || storageIndex >= storage.size()) {
			return false;
		}
		Pokemon fromTeam = equipe.get(teamIndex);
		equipe.set(teamIndex, storage.get(storageIndex));
		storage.set(storageIndex, fromTeam);
		return true;
	}

	public boolean swapTeamSlots(int firstIndex, int secondIndex) {
		if (firstIndex < 0 || secondIndex < 0 || firstIndex >= equipe.size() || secondIndex >= equipe.size()
				|| firstIndex == secondIndex) {
			return false;
		}
		Pokemon first = equipe.get(firstIndex);
		equipe.set(firstIndex, equipe.get(secondIndex));
		equipe.set(secondIndex, first);
		return true;
	}

	public boolean hasLivingPokemon() {
		for (Pokemon pokemon : equipe) {
			if (!pokemon.isFainted()) {
				return true;
			}
		}
		return false;
	}

	public int firstLivingIndex() {
		for (int i = 0; i < equipe.size(); i++) {
			if (!equipe.get(i).isFainted()) {
				return i;
			}
		}
		return -1;
	}

	public List<Integer> livingIndicesExcept(int excludedIndex) {
		List<Integer> indices = new ArrayList<>();
		for (int i = 0; i < equipe.size(); i++) {
			if (i != excludedIndex && !equipe.get(i).isFainted()) {
				indices.add(i);
			}
		}
		return indices;
	}

	public List<Integer> faintedIndices() {
		List<Integer> indices = new ArrayList<>();
		for (int i = 0; i < equipe.size(); i++) {
			if (equipe.get(i).isFainted()) {
				indices.add(i);
			}
		}
		return indices;
	}

	public void healAll() {
		for (Pokemon pokemon : equipe) {
			pokemon.fullHeal();
		}
		for (Pokemon pokemon : storage) {
			pokemon.fullHeal();
		}
	}
}
