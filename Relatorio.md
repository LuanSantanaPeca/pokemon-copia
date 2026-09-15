# Relatório — Pokémon (Pedra, Papel e Tesoura)
**Disciplina:** Programação Orientada a Objetos (Java)  

## 1. Hierarquia de tipos

A classe base `Pokemon` concentra o que toda criatura tem em comum: nome, tipo, vida e ataque. Esses atributos são `protected` e a vida só muda por métodos (`receberDano()`, `atacar()`, `heal()`). O construtor da superclasse inicializa o estado; as subclasses apenas informam os valores da espécie.

Três subclasses, uma por tipo, especializam o cálculo de vantagem:

```
Pokemon
 ├── PokemonPedra     (Arrocha)
 ├── PokemonPapel     (Palpel)
 └── PokemonTesoura   (Tes-ou-ra)
```

O restante do jogo (`Batalha`, `BattleManager`, `Treinador`, `Pokedex`) trabalha só com a referência `Pokemon`. Por polimorfismo, `atacar()` dispara a implementação correta sem o combate testar `if (tipo == PEDRA)`.

## 2. Como a vantagem de tipo foi implementada

O ciclo é Pedra → Tesoura → Papel → Pedra:

| Atacante | Forte contra (×1,5) | Fraco contra (×0,7) |
|----------|---------------------|---------------------|
| Pedra    | Tesoura             | Papel               |
| Papel    | Pedra               | Tesoura             |
| Tesoura  | Papel               | Pedra               |

Cada subclasse sobrescreve `calcularVantagem(Pokemon alvo)`. O método `atacar()` da classe base usa esse multiplicador e aplica `receberDano()` no alvo. A classe `Batalha` só chama `atacar()`; para incluir um quarto tipo basta criar outra subclasse. O laço de batalha não precisa ser reescrito.

A captura também fica em `Batalha`: a chance cresce quando a vida restante do selvagem cai (`1 - dificuldade × vidaAtual/vidaMáxima`), limitada entre 5% e 95%.