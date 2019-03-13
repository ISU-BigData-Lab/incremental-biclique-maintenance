package algorithm.DynamicMBE;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import DS.Biclique;
import DS.Bipartition;
import algorithm.MBE.LMBC;
import utils.GraphUtils;

public class New_Baseline {

	private static HashSet<Biclique> BC;
	private static HashSet<Biclique> new_BC;
	private static HashSet<Biclique> sub_BC;
	private static UndirectedGraph<String, DefaultEdge> G;
	private static Bipartition bp;
	
	private static Set<String> H;
	
	private static int newbiclique_count;
	
	public New_Baseline(){
		H = new HashSet<>();
		newbiclique_count = 0;
		new_BC = new HashSet<>();
		sub_BC = new HashSet<>();
	}

	public static void main(String[] args) throws Exception {
		String graph = args[0];
		String bicliqueset = args[1];
		String edge_set = args[2];
		int batch_size = Integer.parseInt(args[3]);
		int biflag = Integer.parseInt(args[4]);
		int size_th = Integer.parseInt(args[5]);
		String out_file = args[6];

		G = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
		bp = new Bipartition();

		readGraph(graph, biflag);

		BufferedReader bbr = new BufferedReader(new FileReader(bicliqueset));
		String line;

		BC = new HashSet<>();

		while ((line = bbr.readLine()) != null) {

			// System.out.println(line);
			String X = line.split("\\*")[0];
			String Y = line.split("\\*")[1];
			String[] splitX = X.split("[,\\[\\]\\s+]");
			String[] splitY = Y.split("[,\\[\\]\\s+]");
			Set<String> A = new TreeSet<>();
			Set<String> B = new TreeSet<>();

			// System.out.println(splitX.length);

			for (String x : splitX) {
				if (x.endsWith("L"))
					A.add(x);
			}

			for (String y : splitY) {
				if (y.endsWith("R"))
					B.add(y);
			}

			System.out.println(A.size());
			Biclique b = new Biclique(A, B);

			System.out.println(b);
			BC.add(b);
		}

		System.out.println("Initial BicliqueSet Reading Complete");
		bbr.close();
		
		int index = 0;
		int id = 0;
		boolean eof_flag = false;
		
		BufferedReader ebr = new BufferedReader(new FileReader(edge_set));
		
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(out_file, true)));


		out.println("new_Baseline");
		
		out.println("iteration\tnumber(new)\tnumber(subsumed)\ttotal-time(ms)");
		
		out.close();

		
		while(true){
			
			index = 0;
			id++;
			
			out = new PrintWriter(new BufferedWriter(new FileWriter(out_file, true)));
			
			Set<String[]> batch = new HashSet<>();
			
			while (index < batch_size) {
				if ((line = ebr.readLine()) != null) {
					
					String u = line.split("\\s+")[0];
					String v = line.split("\\s+")[1];
					G.addEdge(u, v);
					batch.add(new String[] {u, v});
					index++;
					
				}else{
					
					eof_flag = true;
					break;
				}
			}
			
			long start = System.currentTimeMillis();
			
			New_Baseline algo = new New_Baseline();
			algo.run(batch, size_th);
			
			long duration = System.currentTimeMillis() - start;
			
			System.out.println(id + "," + new_BC.size() + "," + sub_BC.size() + "," + duration);
			out.println(id + "," + new_BC.size() + "," + sub_BC.size() + "," + duration);
			out.close();
			
			
			if(eof_flag)
				break;
			
		}

	}

	private void run(Set<String[]> batch, int size_th) throws Exception {
		
		List<String> L = new ArrayList<>();
		List<String> R = new ArrayList<>();
		
		for (String[] e : batch) {
			
			String edge = e[0] + " " + e[1];
			
			H.add(edge);
			
			L.addAll(GraphUtils.getNeighbors(G, e[1]));
			R.addAll(GraphUtils.getNeighbors(G, e[0]));
		}
		
		UndirectedGraph<String, DefaultEdge> subG = GraphUtils.CreateInducedBipartiteGraph(G, L, R);
		
		Bipartition bp = GraphUtils.getBipartition();
		
		LMBC lmbc = new LMBC(subG, bp, size_th);
		
		Set<Biclique> candidateBC = lmbc.collect();
		
		for(Biclique b : candidateBC){
			
			for(String e: H){
				String u = e.split("\\s+")[0];
				String v = e.split("\\s+")[1];
				
				if(b.getX().contains(u) && b.getY().contains(v)){
					new_BC.add(b);
					break;
				}
			}
		}
		
		compute_subsumedbicliques(new_BC, H, size_th);
		
		BC.addAll(new_BC);
		BC.removeAll(sub_BC);
		
	}

	private void compute_subsumedbicliques(HashSet<Biclique> new_BC, Set<String> H, int size_th) throws Exception {
		
		for(Biclique b : new_BC){
			
			Set<String> L = b.getX();
			Set<String> R = b.getY();
			
			UndirectedGraph<String, DefaultEdge> b_minus_H = GraphUtils.CreateInducedBipartiteGraph(G, L, R);
			Bipartition bp = new Bipartition(L,R);
			
			for(String e : H){
				String u = e.split("\\s+")[0];
				String v = e.split("\\s+")[1];
				b_minus_H.removeEdge(u, v);
			}
			
			LMBC lmbc = new LMBC(b_minus_H, bp, size_th);
			
			Set<Biclique> candidateBC = lmbc.collect();
			
			for(Biclique sb : candidateBC){
				if(BC.contains(sb))
					sub_BC.add(sb);
			}
			
		}
		
	}

	private static void readGraph(String graphFile, int biflag) {

		// We assume that the input graph is in adjacency list format. The
		// choice of graph format is so
		// because we start the incremental computation from a graph reduced
		// from the original graph
		// by removing edges from the original graph with creation probability.
		// Hence, there might exists
		// isolated vertices. So, edge list format is not good choice here.
		if (biflag == 1) { // for reading bipartite graph
			G = GraphUtils.readBiGraph(graphFile, 1);
			bp = GraphUtils.getBipartition(G);
		} else { // for reading non-bipartite graph
			G = GraphUtils.readGraph(graphFile, 1);
		}

	}

}
