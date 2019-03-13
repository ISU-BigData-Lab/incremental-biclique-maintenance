package algorithm.DynamicMBE;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import DS.Biclique;
import DS.Bipartition;
import algorithm.MBE.LMBC;
import utils.GraphUtils;
import utils.SetOperations;

/**
 * In this implementation, we implement baseline algorithm. In this algorithm, we enumerate all maximal bicliques
 * of the bipartite graph before adding a set of new edges, enumerate maximal bicliques after updating the graph
 * with a set of new edges, then compute the symmetric difference to compute the changes in the set of maximal bicliques
 * due to addition of a set of new edges (batch).
 *  
 * @author Apurba Das
 *
 */

public class MaintainBC_Baseline {

	private static HashSet<Biclique> BCliqueSet[];
	
	private static UndirectedGraph<String, DefaultEdge> G;
	private static Bipartition bp;

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		String graph = args[0];
		String bicliqueset = args[1];
		String edge_set = args[2];
		int batch_size = Integer.parseInt(args[3]);
		int biflag = Integer.parseInt(args[4]);
		int size_th = Integer.parseInt(args[5]);
		String out_file = args[6];

		//Graph G = new Graph(graph, 1);
		
		G = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
		bp = new Bipartition();
		
		readGraph(graph, biflag);

		LineNumberReader lnr = new LineNumberReader(new FileReader(new File(edge_set)));

		lnr.skip(Long.MAX_VALUE);

		int lines = lnr.getLineNumber() + 1;
		lnr.close();

		System.out.println(lines);
		BCliqueSet = new HashSet[lines / batch_size + 10];

		BufferedReader bbr = new BufferedReader(new FileReader(bicliqueset));
		String line;

		BCliqueSet[0] = new HashSet<>();

		while ((line = bbr.readLine()) != null) {
			
			//System.out.println(line);
			String X = line.split("\\*")[0];
			String Y = line.split("\\*")[1];
			String[] splitX = X.split("[,\\[\\]\\s+]");
			String[] splitY = Y.split("[,\\[\\]\\s+]");
			Set<String> A = new TreeSet<>();
			Set<String> B = new TreeSet<>();
			
			//System.out.println(splitX.length);
			
			for(String x : splitX){
				if(x.endsWith("L"))
					A.add(x);
			}
			
			for(String y : splitY){
				if(y.endsWith("R"))
					B.add(y);
			}
			
			System.out.println(A.size());
			Biclique b = new Biclique(A,B);

			System.out.println(b);
			BCliqueSet[0].add(b);
		}

		System.out.println("Initial BicliqueSet Reading Complete");
		bbr.close();

		int id = 0;
		int index = 0;
		boolean eof_flag = false;

		BufferedReader ebr = new BufferedReader(new FileReader(edge_set));

		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(out_file, true)));

		out.println();
		out.println("Algorithm- \"MaintainBC-Baseline\"");
		out.println("Iteration\tNumber(new)\tNumber(subsumed)\t Computation Time(ms)\t Space Cost(MB)");
		out.close();

		while (true) {
			index = 0;
			id++;
			
			out = new PrintWriter(new BufferedWriter(new FileWriter(out_file, true)));

			while (index < batch_size) {
				if ((line = ebr.readLine()) != null) {
					//System.out.println("line: " + line);
					//System.out.println("id: " + id);
					String u = line.split("\\s+")[0];
					String v = line.split("\\s+")[1];
					//System.out.println("adding edge: " + u + " " + v);
					G.addEdge(u, v);
					index++;
				}else{
					//System.out.println("eof_flag: " + eof_flag);
					eof_flag = true;
					break;
				}
			}
			
			//System.out.println("Updating graph complete!!");
			
			//System.out.println("Maximal Clique Computation start!!");
            long t1 = System.currentTimeMillis();
            System.out.println(bp.getL().size());
            System.out.println(bp.getR().size());
            LMBC lmbc = new LMBC(G, bp, size_th);
            long compute_time = System.currentTimeMillis() - t1;
            //System.out.println("Maximal Clique Computation end!!");
            
            //System.out.println("Symmetric Difference Computation start!!");
            long t2 = System.currentTimeMillis();
            /* computing the symmetric difference */
            BCliqueSet[id] = new HashSet<>();
            BCliqueSet[id].addAll(lmbc.collect());
            
            Set<Biclique> bcliques_updated = new HashSet<>(BCliqueSet[id]);
            Set<Biclique> bcliques_old = new HashSet<>(BCliqueSet[id-1]);
            
            //System.out.println("maximal bicliques of the updated bipartite graph");
            //for(Biclique b : bcliques_updated){
            //	System.out.println(b);
            //}
            //System.out.println("maximal bicliques of the graph before update");
            //for(Biclique b : bcliques_old){
            //	System.out.println(b);
            //}
            
            Set<Biclique> intrsect = SetOperations.intersect(bcliques_updated, bcliques_old);
            
            bcliques_updated.removeAll(intrsect);
            bcliques_old.removeAll(intrsect);
            //Set<Biclique> symmetricDiff = new HashSet<Biclique>(BCliqueSet[id-1]);
            //symmetricDiff.addAll(BCliqueSet[id]);
            //Set<Biclique> tmp = new HashSet<>(BCliqueSet[id - 1]);
            //tmp.retainAll(BCliqueSet[id]);
            //symmetricDiff.removeAll(tmp);
            //System.out.println("Symmetric Difference Computation end!!");

            long symdiff_time = System.currentTimeMillis() - t2;
            
            long comp_time = (compute_time + symdiff_time);
            
          //get java runtime
			Runtime runtime = Runtime.getRuntime();
			
			//calculate the used memory
			long memory = (runtime.totalMemory() - runtime.freeMemory())/(1024L * 1024L);
            
            System.out.println(id + "," + bcliques_updated.size() + "," + bcliques_old.size() + "," + comp_time + "," + memory);
            
            out.println(id + "," + bcliques_updated.size() + "," + bcliques_old.size() + "," + comp_time + "," + memory);
            
            out.close();
            
            //if(id == 3615)
            //	break;
            
            if(eof_flag)
            	break;
            
            
		}
		ebr.close();

	}
	
	private static void readGraph(String graphFile, int biflag) {
		
		//We assume that the input graph is in adjacency list format. The choice of graph format is so
		//because we start the incremental computation from a graph reduced from the original graph 
		//by removing edges from the original graph with cretain probability. Hence, there might exists 
		//isolated vertices. So, edge list format is not good choice here.
		if(biflag == 1){	//for reading bipartite graph
			G = GraphUtils.readBiGraph(graphFile, 1);
			bp = GraphUtils.getBipartition(G);
		}
		else{	//for reading non-bipartite graph
			G = GraphUtils.readGraph(graphFile, 1);
		}
		

	}

}
