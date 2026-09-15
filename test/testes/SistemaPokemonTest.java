package testes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import entidade.Pokemon;
import entidade.PokemonPapel;
import entidade.PokemonPedra;
import entidade.PokemonTesoura;
import main.Batalha;
import main.Pokedex;
import main.Treinador;

public class SistemaPokemonTest {

	@Test
	void danoComVantagemPedraContraTesoura() {
		Pokemon pedra = new PokemonPedra(0);
		Pokemon tesoura = new PokemonTesoura(0);
		int dano = pedra.atacar(tesoura);
		assertEquals(18, dano);
		assertEquals(tesoura.getVidaMaxima() - 18, tesoura.getVida());
	}

	@Test
	void danoSemVantagemMesmoTipo() {
		Pokemon atacante = new PokemonPapel(0);
		Pokemon defensor = new PokemonPapel(0);
		int dano = atacante.atacar(defensor);
		assertEquals(10, dano);
		assertEquals(defensor.getVidaMaxima() - 10, defensor.getVida());
	}

	@Test
	void danoComDesvantagemPedraContraPapel() {
		Pokemon pedra = new PokemonPedra(0);
		Pokemon papel = new PokemonPapel(0);
		int dano = pedra.atacar(papel);
		assertEquals(8, dano);
	}

	@Test
	void capturaMaisFacilComVidaBaixa() {
		Pokemon cheio = new PokemonPedra(0);
		Pokemon ferido = new PokemonPedra(0);
		ferido.receberDano(ferido.getVidaMaxima() - 1);
		assertTrue(Batalha.chanceDeCaptura(ferido) > Batalha.chanceDeCaptura(cheio));
		assertTrue(Batalha.capturar(ferido, 0.5));
		assertFalse(Batalha.capturar(cheio, 0.5));
	}

	@Test
	void capturaFalhaSePokemonDesmaiou() {
		Pokemon selvagem = new PokemonTesoura(0);
		selvagem.receberDano(selvagem.getVidaMaxima());
		assertEquals(0.0, Batalha.chanceDeCaptura(selvagem));
		assertFalse(Batalha.capturar(selvagem, 0.0));
	}

	@Test
	void pokedexNaoDuplicaEntradas() {
		Pokedex pokedex = new Pokedex();
		Pokemon visto = new PokemonPapel(0);
		assertTrue(pokedex.jaRegistrado(visto));
		assertEquals(3, pokedex.getRegistrados().size());
		pokedex.markSeen(visto);
		pokedex.markSeen(visto.copy());
		assertEquals(3, pokedex.getRegistrados().size());
		assertEquals(Pokedex.State.SEEN, pokedex.stateOf(visto.getNome()));
	}

	@Test
	void vidaSoMudaPorMetodos() {
		Pokemon pokemon = new PokemonPedra(0);
		int vidaInicial = pokemon.getVida();
		pokemon.receberDano(5);
		assertEquals(vidaInicial - 5, pokemon.getVida());
		pokemon.heal(3);
		assertEquals(vidaInicial - 2, pokemon.getVida());
		pokemon.fullHeal();
		assertEquals(pokemon.getVidaMaxima(), pokemon.getVida());
	}

	@Test
	void equipeDoTreinadorTemNoMaximoSeis() {
		Treinador treinador = new Treinador();
		for (int i = 0; i < 6; i++) {
			assertTrue(treinador.addPokemon(new PokemonPedra(0)));
		}
		assertFalse(treinador.addPokemon(new PokemonPapel(0)));
		assertEquals(6, treinador.equipe.size());
	}
}
