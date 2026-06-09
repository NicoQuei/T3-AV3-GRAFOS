# T3 — Fluxo Máximo · UVA 11045 — My T-shirt suits me

**Disciplina:** Resolução de Problemas com Grafos — Prof. Me. Ricardo Carubbi
**Trabalho Prático 3 — Unidade 3 (Fluxo Máximo)**
**Grupo J**

- **Problema:** UVA 11045 — *My T-shirt suits me*
- **Link:** <https://onlinejudge.org/index.php?option=onlinejudge&Itemid=8&page=show_problem&problem=1986>
- **PDF do enunciado:** <https://onlinejudge.org/external/110/11045.pdf>
- **Linguagem:** Java (OpenJDK 21)

> **Integrantes:**
> - Nícolas Queiroga — `github.com/nicoquei`
> - Mauricio Oliveira
> - João Lucas

---

## 1. O problema

Victor precisa distribuir **N camisetas** entre **M voluntários**, uma por
voluntário. Há **6 tamanhos** (XXL, XL, L, M, S, XS), `N` é múltiplo de 6 e
existe a **mesma quantidade de cada tamanho**, ou seja, `N/6` camisetas por
tamanho. Cada voluntário aceita **exatamente dois tamanhos**. Devemos decidir se
é possível dar a **todos** os voluntários uma camiseta de um tamanho que lhe
sirva.

- **Entrada:** primeira linha com o número de casos de teste. Para cada caso,
  uma linha com `N` e `M` (`N` múltiplo de 6, `1 ≤ N ≤ 36`, `1 ≤ M ≤ 30`,
  `N ≥ M`), seguida de `M` linhas, cada uma com os dois tamanhos aceitos pelo
  voluntário.
- **Saída:** `YES` se existe distribuição que atende todos os voluntários,
  `NO` caso contrário.

## 2. Como executar

A solução é um único arquivo. No diretório `src/`:

```bash
# Forma padrão (com JDK instalado)
cd src
javac Main.java
java Main < ../dados/entradas_do_problema.txt

# Alternativa sem compilar (Java 11+ roda arquivo único direto)
java Main.java < ../dados/entradas_do_problema.txt
```

Saída esperada para `dados/entradas_do_problema.txt`:

```text
YES
NO
YES
```

## 3. Modelagem como rede de fluxo

O problema é um **emparelhamento bipartido com múltiplas cópias por tamanho**,
reduzido para **fluxo máximo**. Em vez de emparelhar 1-para-1, cada tamanho tem
`N/6` cópias, então usamos capacidades para representar o estoque.

```text
            cap = N/6                cap = 1                 cap = 1
   (s) ─────────────────► [tamanho] ───────────► [voluntário] ─────────► (t)
        (uma aresta por          (só se o voluntário      (cada voluntário
         tamanho, 6 no total)     aceita o tamanho)        recebe no máx. 1)
```

- **Origem `s`:** ponto de partida do fluxo; representa o "depósito" de
  camisetas. Uma unidade que sai de `s` é uma camiseta entregue.
- **Sorvedouro `t`:** chegar em `t` significa **um voluntário atendido**. O
  total de fluxo que chega em `t` é o número de voluntários servidos.
- **Vértices de tamanho (6):** um por tamanho (XXL, XL, L, M, S, XS). A camada
  intermediária que limita a oferta.
- **Vértices de voluntário (M):** um por voluntário. A camada que limita a
  demanda (cada um quer 1 camiseta).
- **Arestas e capacidades:**
  - `s → tamanho_i` com capacidade **`N/6`** → há `N/6` camisetas daquele
    tamanho disponíveis.
  - `tamanho_i → voluntário_j` com capacidade **1** → existe só se `j` aceita
    `i`; uma camiseta de um tamanho serve um voluntário uma única vez.
  - `voluntário_j → t` com capacidade **1** → cada voluntário recebe **no máximo
    uma** camiseta.

Cada **1 unidade de fluxo** corresponde a uma decisão válida: "a camiseta de
tamanho *i* foi dada ao voluntário *j*".

### Numeração dos vértices

| Vértice          | Índice      |
| ---------------- | ----------- |
| origem `s`       | `0`         |
| tamanho *i*      | `1 + i` (i de 0 a 5) |
| voluntário *j*   | `7 + j` (j de 0 a M-1) |
| sorvedouro `t`   | `7 + M`     |

## 4. Algoritmo utilizado

Usamos **Edmonds-Karp** — a variante do método de Ford-Fulkerson que escolhe os
caminhos aumentantes por **BFS** (caminho com menor número de arestas).

**Por que Edmonds-Karp e não Ford-Fulkerson com DFS?** Ford-Fulkerson puro
também resolveria, pois as capacidades aqui são pequenas (`N/6 ≤ 6`). Optamos por
Edmonds-Karp por ser **previsível** e ter complexidade polinomial garantida
`O(V·E²)`, independente do valor do fluxo, evitando sequências ruins de caminhos
aumentantes. Como a rede é minúscula (`≤ 38` vértices), a escolha não traz custo
relevante e ganha em segurança contra TLE.

### Papel do grafo residual

A rede residual é representada por uma **matriz de capacidades** `cap[u][v]`. A
cada caminho aumentante encontrado:

1. calcula-se o **gargalo** (menor `cap[u][v]` ao longo do caminho);
2. subtrai-se o gargalo no sentido direto (`cap[u][v] -= b`) e soma-se no
   **sentido reverso** (`cap[v][u] += b`).

O termo `cap[v][u]` — que começa em 0 — é a **aresta reversa implícita**: ele
permite **desfazer** uma alocação anterior. Se uma camiseta foi dada a um
voluntário de um jeito que bloqueia outro, o fluxo pode "voltar" por esse
caminho reverso e reorganizar a distribuição. É isso que garante a
**otimalidade** do fluxo. O algoritmo para quando **não existe mais caminho de
`s` a `t`** no grafo residual (a BFS não alcança `t`).

## 5. Conversão do fluxo na resposta

O **valor do fluxo máximo** é exatamente o número de voluntários que conseguem
uma camiseta de um tamanho aceito (cada um contribui no máximo 1, pela aresta
`voluntário → t`). Logo:

- `fluxo == M` → todos atendidos → **`YES`**
- `fluxo  < M` → impossível atender todos → **`NO`**

O **emparelhamento em si** (qual tamanho foi para qual voluntário) pode ser lido,
se preciso, observando as arestas `tamanho_i → voluntário_j` que ficaram com
fluxo positivo (capacidade da reversa maior que zero). Para este problema, porém,
só a comparação `fluxo == M` é exigida.

## 6. Análise de complexidade

- **Vértices:** `V = 8 + M ≤ 38`.
- **Matriz de capacidades:** `V × V`, ou seja `O(V²)` de memória — no máximo
  `38 × 38 ≈ 1.4 K` inteiros por caso de teste.
- **Cada BFS** percorre a matriz em `O(V²)`. O número de caminhos aumentantes é
  no máximo o valor do fluxo (`≤ M`, pois cada caminho carrega pelo menos 1
  unidade), então o custo por caso é `O(M · V²) ≤ 30 · 38² ≈ 43 K` operações —
  praticamente instantâneo. O custo total cresce linearmente com o número de
  casos de teste.
- O limite clássico de Edmonds-Karp, `O(V · E²)`, também se aplica e é
  igualmente trivial com esses tamanhos.

## 7. Casos especiais tratados

- **Múltiplas cópias por tamanho:** a aresta `s → tamanho` tem capacidade `N/6`
  (não unitária) — é o cerne do problema.
- **Voluntário que lista o mesmo tamanho duas vezes:** atribuir
  `cap[tamanho][voluntário] = 1` duas vezes é idempotente (continua 1); e mesmo
  que não fosse, a capacidade 1 em `voluntário → t` impede contar duas camisetas.
- **Sobra de camisetas (`N > M`):** normal; nem todo estoque precisa ser usado.
- **Vários casos de teste:** a matriz `cap` é recriada do zero a cada caso.
- **Sem risco de overflow:** capacidades pequenas (`≤ 6`), `int` é suficiente;
  não é necessário um valor de `INF`.

## 8. Evidência de Accepted

Submissão aceita no UVA Online Judge (problema 11045):

![Accepted — UVA 11045](evidencias/accepted.png)
