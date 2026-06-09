import java.util.*;

// UVA 11045 - My T-shirt suits me  (fluxo maximo / Edmonds-Karp com matriz de capacidades)
// Vertices:  s = 0 | tamanhos = 1..6 | voluntarios = 7..6+M | t = 7+M
public class Main {
    static int V;
    static int[][] cap;   // cap[u][v] = capacidade residual de u->v (reversa fica implicita)

    // Fluxo maximo = soma dos caminhos aumentantes achados por BFS
    static int maxFlow(int s, int t) {
        int flow = 0;
        while (true) {
            int[] par = new int[V];
            Arrays.fill(par, -1);
            par[s] = s;
            ArrayDeque<Integer> q = new ArrayDeque<>();
            q.add(s);
            while (!q.isEmpty()) {                       // BFS: caminho mais curto com folga
                int u = q.poll();
                for (int v = 0; v < V; v++)
                    if (par[v] == -1 && cap[u][v] > 0) { par[v] = u; q.add(v); }
            }
            if (par[t] == -1) break;                     // nao ha mais caminho s->t: terminou
            int b = Integer.MAX_VALUE;                   // gargalo do caminho
            for (int v = t; v != s; v = par[v]) b = Math.min(b, cap[par[v]][v]);
            for (int v = t; v != s; v = par[v]) {        // atualiza a rede residual
                cap[par[v]][v] -= b;
                cap[v][par[v]] += b;
            }
            flow += b;
        }
        return flow;
    }

    static int size(String s) {
        switch (s) { case "XXL": return 1; case "XL": return 2; case "L": return 3;
                     case "M": return 4; case "S": return 5; default: return 6; } // XS
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int tc = in.nextInt();
        StringBuilder out = new StringBuilder();
        while (tc-- > 0) {
            int N = in.nextInt(), M = in.nextInt();
            int s = 0, t = 7 + M;
            V = 8 + M;
            cap = new int[V][V];
            for (int i = 1; i <= 6; i++) cap[s][i] = N / 6;   // s -> tamanho  (estoque N/6)
            for (int j = 0; j < M; j++) {
                cap[size(in.next())][7 + j] = 1;              // tamanho -> voluntario
                cap[size(in.next())][7 + j] = 1;
                cap[7 + j][t] = 1;                            // voluntario -> t  (1 por pessoa)
            }
            out.append(maxFlow(s, t) == M ? "YES" : "NO").append('\n');
        }
        System.out.print(out);
    }
}
