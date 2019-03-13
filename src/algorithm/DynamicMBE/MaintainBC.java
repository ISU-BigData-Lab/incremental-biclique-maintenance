package algorithm.DynamicMBE;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import DS.Biclique;
import DS.Bipartition;
import algorithm.MBE.LMBC;
//import net.sourceforge.sizeof.SizeOf;
import utils.GraphUtils;

/**
 * In this implementation, we consider algorithm due to Liu et al. for enumerating maximal bicliques from the subgraph. However, when
 * a set of new edges are added to the graph, considering each new edge separately for generating new maximal bicliques
 * might result in generating a new maximal clique more than once. We maintain ordering of new edges and when a new
 * maximal biclique is generated we explicitly check if it contains a already considered new edge. We only consider those new maximal 
 * bicliques that do not contain previously considered new edges. 
 * 
 * For enumerating subsumed maximal bicliques, we enumerate subsumed maximal bicliques from new maximal bicliques in the way similar 
 * to the approach we have considered in enumerating subsumed maximal cliques from new maximal cliques.
 * @author Apurba Das
 *
 */

public class MaintainBC {

	private static long newBiCliqueCount;
	private static long newBiCliqueSizes;
	
	
	private static long subBicliqueCount;
	private static int subBicliqueSizes;

	//private static Graph G; // dynamic graph
	
	private static UndirectedGraph<String, DefaultEdge> G;

	//private static Graph H; // for storing batch of edges (a batch of new edges
							// can be considered a new subgraph)
	
	private static Set<String> H;

	private static Set<Biclique> BC; //for maintaining all maximal bicliques of a graph
	
	private static Set<Biclique> subBC;	//for maintaining all subsumed bicliques for some batch addition
	private static long subtime;
	
	private static long timegconstruct;
	
	private static Map<String, Set<String>> Ei;
	
	private static long graph_sc, bclique_sc, sub_sc, other_sc;

	public MaintainBC() {
		
		
		subBC = new HashSet<>();

		//H = new Graph();
		
		H = new HashSet<>();
		
		newBiCliqueCount = 0;
		newBiCliqueSizes = 0;
		
		subBicliqueCount = 0;
		subBicliqueSizes = 0;
		subtime = 0;
		timegconstruct = 0;
		
		other_sc = 0;
		sub_sc = 0;
	}

	public void run(Set<String[]> batch, int size_threshold) throws Exception {
		
		Ei = new HashMap<>();

		// update the graph
		for (String[] e : batch) {
			//System.out.println("Addition of new edge: " + e[0] + " " + e[1]);
			G.addEdge(e[0], e[1]);
			
			graph_sc += e[0].length()*2;
			graph_sc += e[1].length()*2;
			
			String edge = e[0] + " " + e[1];
			H.add(edge);
			
			other_sc += edge.length()*2;
		}

		for (String[] e : batch) {

			String u = e[0];
			String v = e[1];
			//Set<String> A = G.getNeighbors(v); // e[1] is v
			//Set<String> B = G.getNeighbors(u); // e[0] is u
			
			long start = System.currentTimeMillis();
			List<String> A = GraphUtils.getNeighbors(G, v);
			List<String> B = GraphUtils.getNeighbors(G, u);

			//Graph subG = G.CreateInducedBipartiteGraph(A, B);
			
			UndirectedGraph<String, DefaultEdge> subG = GraphUtils.CreateInducedBipartiteGraph(G, A, B);

			Bipartition bp = GraphUtils.getBipartition();
			
			timegconstruct += (System.currentTimeMillis() - start);
			
			//Bipartition bp = new Bipartition(A,B);

			LMBC lmbc = new LMBC(subG, bp, size_threshold);
			
			//LMBC lmbc = new LMBC(G,bp);

			Set<Biclique> candidateBC = lmbc.collect();

			for (Biclique b : candidateBC) {
				boolean consider = true;
				for (String i : Ei.keySet()) {
					for (String j : Ei.get(i)) {
						if (b.getX().contains(i) && b.getY().contains(j)) {
							consider = false;
							break;
						}
					}
					if (!consider)
						break;
				}

				if (consider) {
					// CliqueSet.add(c);
					newBiCliqueCount++;
					//System.out.println("new biclique: " + b);
					int x = b.getX().size();
					int y = b.getY().size();
					newBiCliqueSizes += x * y;
					BC.add(b);	//update the biclique set for next graph update by adding new biclique
					bclique_sc += (b.toString().length() - 4 - (b.getX().size()-1)*2 - (b.getY().size()-1)*2)*2;
					long t1 = System.currentTimeMillis();
					//System.out.println("bclique_sc_before_sub(main): " + bclique_sc);
					EnumSB.run(BC, b, H, size_threshold);
					
					bclique_sc -= EnumSB.getdeductedspacecost();
					//System.out.println("bclique_sc_after_sub(main): " + bclique_sc);
					subtime += (System.currentTimeMillis() - t1);
					subBicliqueCount += EnumSB.getSubCount();
					subBicliqueSizes += EnumSB.getSubSizes();
					
				}

			}

			// ading edge (u,v)
			//because one ebd of an edge in one bipartition and another in the other bipartition, there is no need to add edge (v,u).
			if (Ei.get(u) == null) {
				Set<String> L = new TreeSet<>();
				L.add(v);
				Ei.put(u, L);
				
				other_sc += u.length()*2;
				other_sc += v.length()*2;
				// spacecost3 += 2;
			} else {
				Set<String> L = Ei.get(u);
				L.add(v);
				Ei.put(u, L);
				other_sc += v.length()*2;
				// spacecost3 += 1;
			}

		}
		

	}

	/**
	 * The input graph to this program is either in the adjace3ncny list format or in edge list format.
	 * If the input graph is in the adjacency list format, for bipartite graphs, the vertices should be appended by L or R
	 * based on the partition a vertex belongs to. If the input graph is in edge list format, there is no need to explicitly specify L or R
	 * with the vertices.
	 * 
	 * However, there should be a flag indicating bipartitie/non-bipartite; and adjacency list/edge list format for ease of parsing the input files.
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		if (args.length < 7) {
			System.out.println("usage : java program graph biclique_set edge_set batch_size size_threshold out_file");
			System.out.println("graph: represented as set of edges in a txt file");
			System.out.println("biclique_set: set of all maximal cliques of graph in a text file");
			System.out.println("edge_set: edges to be added: text file");
			System.out.println("batch_size: number of edges in a batch");
			System.out.println("size_threshold : minimum sizes of the partitions in a biclique");
			System.out.println("out_file: the file to store the output data");
			System.exit(0);
		}

		//SizeOf.skipStaticField(true);
		//SizeOf.setMinSizeToLog(0);
		
		
		String graphFile = args[0];
		String bicliqueSetFile = args[1];
		String edgeSetFile = args[2];
		int batch_size = Integer.parseInt(args[3]);
		int biflag = Integer.parseInt(args[4]);	// 1: bipartite graph; 0: non-bipartite graph
		int size_th = Integer.parseInt(args[5]);
		String out_file = args[6];
		
		
		G = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
		BC = new HashSet<>();
		
		graph_sc = 0;
		bclique_sc = 0;

		readGraph(graphFile, biflag);

		readBiCliques(bicliqueSetFile, size_th);

		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(out_file, true)));

			out.println();

			out.println("SymDiffBC");
			
			out.println("iteration\tnumber(new)\tnumber(subsumed)\tsizes(new)\tsizes(sub)\tnew-time\tsub-time\ttotal-time(ms)\tspace-cost-graph(KB)\tspace-cost-bcliques(mb)\tspace-cost-total(mb)");
			
			out.close();

			BufferedReader br = new BufferedReader(new FileReader(edgeSetFile));

			int index = 0;
			int count = 0;
			boolean eof_flag = false;

			String line;

			while (true) {
				index = 0;
				count++;
				
				out = new PrintWriter(new BufferedWriter(new FileWriter(out_file, true)));

				Set<String[]> batch = new HashSet<>();

				while (index < batch_size) {
					if ((line = br.readLine()) != null) {
						batch.add(new String[] { line.split("\\s+")[0],
								line.split("\\s+")[1] });
						index++;
					} else {
						eof_flag = true;
						break;
					}
				}
				
				long start = System.currentTimeMillis();
				MaintainBC algo = new MaintainBC();

				algo.run(batch, size_th);
				
				long time = System.currentTimeMillis() - start;
				
				long newtime = time - subtime;
				
				//get java runtime
				//Runtime runtime = Runtime.getRuntime();
				
				//long graph_memory = SizeOf.deepSizeOf(G)/(1024*1024);
				//long bclique_memory = SizeOf.deepSizeOf(BC)/(1024*1024);
				//long total_memory = (SizeOf.deepSizeOf(G) + SizeOf.deepSizeOf(BC) + SizeOf.deepSizeOf(H) + SizeOf.deepSizeOf(Ei) + SizeOf.deepSizeOf(batch))/(1024*1024);
				
				//calculate the used memory
				//long memory = (runtime.totalMemory() - runtime.freeMemory())/(1024L * 1024L);
				
				long total_memory = (graph_sc + bclique_sc + other_sc)/(1024 * 1024);
				
				
				
				System.out.println(count + "," + newBiCliqueCount + "," + subBicliqueCount + "," + newtime + "," + subtime + "," + timegconstruct + "," + time + "," + graph_sc/(1024*1024) + "," + bclique_sc/(1024*1024) + "," + total_memory);
				out.println(count + "," + newBiCliqueCount + "," + subBicliqueCount + "," + newBiCliqueSizes + "," + subBicliqueSizes + "," + newtime + "," + subtime + "," + time + "," + graph_sc/(1024*1024) + "," + bclique_sc/(1024*1024) + "," + total_memory);
				out.close();
				
				if(eof_flag)
					break;
			}
			
			br.close();
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void readBiCliques(String bicliqueSetFile, int size_th) {

		try {
			
			
			BufferedReader br = new BufferedReader(new FileReader(bicliqueSetFile));

			String line;

			while ((line = br.readLine()) != null) {
				
				String X = line.split("\\*")[0];
				String Y = line.split("\\*")[1];
				String[] splitX = X.split("[,\\[\\]\\s+]");
				String[] splitY = Y.split("[,\\[\\]\\s+]");
				Set<String> A = new TreeSet<>();
				Set<String> B = new TreeSet<>();
				
				int sizeL = 0;
				int sizeR = 0;
				for(String x : splitX){
					if(x.endsWith("L")){
						A.add(x);
						sizeL++;
					}
				}
				
				for(String y : splitY){
					if(y.endsWith("R")){
						B.add(y);
						sizeR++;
					}
				}
				if((sizeL >= size_th) && (sizeR >= size_th)){
					Biclique b = new Biclique(A,B);
					BC.add(b);
					bclique_sc += (b.toString().length() - 4 - (b.getX().size()-1)*2 - (b.getY().size()-1)*2)*2;
				}
			}
			br.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void readGraph(String graphFile, int biflag) {
		
		//We assume that the input graph is in adjacency list format. The choice of graph format is so
		//because we start the incremental computation from a graph reduced from the original graph 
		//by removing edges from the original graph with cretain probability. Hence, there might exists 
		//isolated vertices. So, edge list format is not good choice here.
		if(biflag == 1){	//for reading bipartite graph
			G = GraphUtils.readBiGraph(graphFile, 1);
			
			graph_sc = GraphUtils.getSizeOfGraph();
		}
		else{	//for reading non-bipartite graph
			G = GraphUtils.readGraph(graphFile, 1);
		}
		

	}

}
