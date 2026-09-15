package main;

import entidade.Pokemon;

public class Batalha {

	private Pokemon primeiro;
	private Pokemon segundo;

	public Batalha(Pokemon primeiro, Pokemon segundo) {
		this.primeiro = primeiro;
		this.segundo = segundo;
	}

	public Pokemon getPrimeiro() {
		return primeiro;
	}

	public Pokemon getSegundo() {
		return segundo;
	}

	public int turno(Pokemon atacante, Pokemon defensor) {
		return atacante.atacar(defensor);
	}

	public boolean terminou() {
		return primeiro.isFainted() || segundo.isFainted();
	}

	public Pokemon vencedor() {
		if (primeiro.isFainted() && !segundo.isFainted()) {
			return segundo;
		}
		if (segundo.isFainted() && !primeiro.isFainted()) {
			return primeiro;
		}
		return null;
	}

	public static double chanceDeCaptura(Pokemon selvagem) {
		if (selvagem == null || selvagem.isFainted()) {
			return 0.0;
		}
		double razaoVida = selvagem.getVida() / (double) selvagem.getVidaMaxima();
		double chance = 1.0 - selvagem.getDificuldadeCaptura() * razaoVida;
		return Math.max(0.05, Math.min(0.95, chance));
	}

	public static boolean capturar(Pokemon selvagem, double sorte) {
		return sorte < chanceDeCaptura(selvagem);
	}
}
