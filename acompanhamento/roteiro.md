# Ficha de Acompanhamento — UVA 11045 (My T-shirt suits me)

**Grupo J — TP3 (Fluxo Máximo)**

> Ficha curta de raciocínio (sem código completo), conforme pedido na atividade
> de acompanhamento.

## 1. Resumo do problema (em linguagem própria)

Temos camisetas de 6 tamanhos, com a **mesma quantidade de cada tamanho**
(`N/6`), e um grupo de voluntários. Cada voluntário só usa **dois tamanhos**
específicos. Queremos saber se dá para entregar uma camiseta a **cada
voluntário** respeitando seus tamanhos e o estoque disponível.

## 2. Entrada e saída

- **Entrada:** nº de casos; depois, por caso, `N` (camisetas, múltiplo de 6) e
  `M` (voluntários); em seguida `M` linhas com 2 tamanhos cada.
- **Saída:** `YES` se todos podem ser atendidos, `NO` caso contrário.
- **O que precisa ser calculado:** existe um emparelhamento que atende os `M`
  voluntários respeitando o estoque `N/6` por tamanho? → equivale a perguntar se
  o **fluxo máximo** da rede vale `M`.

## 3. Modelagem da rede de fluxo

- **Origem `s`** → 6 **vértices de tamanho** → `M` **vértices de voluntário** →
  **sorvedouro `t`**.
- Capacidades:
  - `s → tamanho_i` = **`N/6`** (estoque do tamanho);
  - `tamanho_i → voluntário_j` = **1** (existe só se `j` aceita `i`);
  - `voluntário_j → t` = **1** (cada um recebe no máximo uma camiseta).
- **Por que essas capacidades?** `N/6` limita quantas camisetas de cada tamanho
  podem sair; o `1` na saída de cada voluntário garante que ninguém recebe duas;
  o `1` entre tamanho e voluntário marca uma alocação possível.
- **Por que `s` e `t` fazem sentido?** Cada unidade de fluxo que sai de `s` é uma
  camiseta entregue; cada unidade que chega em `t` é um voluntário satisfeito.

## 4. Ford-Fulkerson ou Edmonds-Karp?

Escolhemos **Edmonds-Karp** (BFS). As capacidades são pequenas (`N/6 ≤ 6`) e a
rede é minúscula (`≤ 38` vértices), então Ford-Fulkerson com DFS também
funcionaria. Mas Edmonds-Karp é **previsível** e tem custo polinomial garantido
`O(V·E²)`, independente do valor do fluxo — escolha mais segura, sem prejuízo de
desempenho aqui.

## 5. Instância pequena (escolhida do enunciado)

Caso 3 do exemplo, e mais um caso `NO` para contraste:

**Instância A (YES):** `N = 6` (⇒ 1 camiseta de cada tamanho), `M = 3`
- v1: `L`, `M`
- v2: `L`, `S`
- v3: `S`, `XS`

**Instância B (NO — caso 2 do exemplo):** `N = 6` (1 de cada), `M = 4`
- v1: `S`,`XL`  v2: `L`,`S`  v3: `L`,`XL`  v4: `L`,`XL`

## 6. Execução manual passo a passo (Instância A)

Estoque: 1 de cada tamanho. Procuramos caminhos `s → tamanho → voluntário → t`
(todos têm comprimento 4) por BFS, saturando 1 unidade por vez.

1. **Caminho 1:** `s → L → v1 → t`. Gargalo = `min(1,1,1) = 1`.
   Residual: `L` esgotado, `v1` atendido. **Fluxo = 1.**
2. **Caminho 2:** `s → S → v2 → t` (v2 aceita L e S; L já esgotou, então usa S).
   Gargalo = 1. Residual: `S` esgotado, `v2` atendido. **Fluxo = 2.**
3. **Caminho 3:** `s → XS → v3 → t` (v3 aceita S e XS; S esgotou, usa XS).
   Gargalo = 1. **Fluxo = 3.**
4. **Parada:** não há mais caminho de `s` a `t` no residual (`v1, v2, v3` já têm
   sua aresta para `t` saturada). **Fluxo máximo = 3.**

**Papel da aresta reversa:** se no passo 2 a BFS tivesse mandado `v2` pegar `L`
(em vez de `S`), `v1` ficaria sem opção; o algoritmo então **empurraria fluxo
pela aresta reversa** `v?→L` e realocaria, devolvendo `L` a quem só tinha `L`.
É esse mecanismo que garante encontrar o ótimo, independente da ordem.

**Instância B (NO), resumida:** só aparecem os tamanhos `S, L, XL` nas
preferências, e há 1 camiseta de cada. No máximo 3 voluntários podem ser
servidos (um por tamanho disponível), mas são `M = 4`. O fluxo trava em **3 < 4**
⇒ **`NO`**.

## 7. Verificação da resposta final

- Instância A: fluxo `= 3 = M` ⇒ **`YES`**. ✔
- Instância B: fluxo `= 3 < 4 = M` ⇒ **`NO`**. ✔

A resposta sai diretamente da comparação `fluxo == M`. Se quiséssemos exibir o
emparelhamento, bastaria listar as arestas `tamanho → voluntário` que ficaram com
fluxo positivo.
