package utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.Graphs;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import DS.Bipartition;

public class GraphUtils {
	
	private static Bipartition bp = null;
	
	private static long size;

	public static Set<String> getNeighbors(UndirectedGraph<String, DefaultEdge> G, Set<String> X) {

		Set<String> result = new TreeSet<>();

		Iterator<String> it = X.iterator();

		String u = it.next();

		// result.addAll(AdjList.get(u));
		result.addAll(Graphs.neighborListOf(G, u));
		while (it.hasNext()) {
			result = SetOperations.intersect(result, Graphs.neighborListOf(G, it.next()));
		}
		return result;

	}

	public static List<String> getNeighbors(UndirectedGraph<String, DefaultEdge> G, String u) {
		return Graphs.neighborListOf(G, u);
	}

	public static UndirectedGraph<String, DefaultEdge> readBiGraph(String edge_path, int intype) {

		UndirectedGraph<String, DefaultEdge> graph = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
		
		bp = new Bipartition();
		
		size = 0;
		

		if (intype == 1) { // adjacency list format

			try {
				BufferedReader br = new BufferedReader(new FileReader(edge_path));
				String line;
				while ((line = br.readLine()) != null) {
					size += line.length();
					String[] splits = line.split("\\s+");
					if (splits.length == 1) {
						String u = splits[0];
						graph.addVertex(u);
						if(u.endsWith("L"))
							bp.addToL(u);
						else
							bp.addToR(u);
						// AdjList.put(u, new TreeSet<String>());
					} else {
						String u = splits[0];
						graph.addVertex(u);
						if(u.endsWith("L"))
							bp.addToL(u);
						else
							bp.addToR(u);
						Set<String> S = new HashSet<>();
						for (int i = 1; i < splits.length; i++) {
							String v = splits[i];
							graph.addVertex(v);
							if(v.endsWith("L"))
								bp.addToL(v);
							else
								bp.addToR(v);
							graph.addEdge(u, v);
							// S.add(splits[i] + "R");
						}
						// AdjList.put(u,S);
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

		} else if (intype == 0) { // edge list format
			try {
				BufferedReader br = new BufferedReader(new FileReader(edge_path));
				String line;
				while((line = br.readLine()) != null){
					String[] splits = line.split("\\s+");
					String u = splits[0] + "L";
					String v = splits[1] + "R";
					graph.addVertex(u);
					graph.addVertex(v);
					bp.addToL(u);
					bp.addToR(v);
					graph.addEdge(u, v);
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
		
		return graph;
	}

	public static UndirectedGraph<String, DefaultEdge> readGraph(String edge_path, int intype) {
		
		UndirectedGraph<String, DefaultEdge> graph = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
		
		if(intype == 1){	//adjacency list format
			try {
				BufferedReader br = new BufferedReader(new FileReader(edge_path));
				String line;
				while ((line = br.readLine()) != null) {
					String[] splits = line.split("\\s+");
					if (splits.length == 1) {
						String u = splits[0];
						graph.addVertex(u);
					} else {
						String u = splits[0];
						graph.addVertex(u);
						for (int i = 1; i < splits.length; i++) {
							String v = splits[i];
							graph.addVertex(v);
							graph.addEdge(u, v);
							// S.add(splits[i] + "R");
						}
						// AdjList.put(u,S);
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
		else if(intype == 0){	//edge list format
			try {
				BufferedReader br = new BufferedReader(new FileReader(edge_path));
				String line;
				while((line = br.readLine()) != null){
					String[] splits = line.split("\\s+");
					String u = splits[0];
					String v = splits[1];
					graph.addVertex(u);
					graph.addVertex(v);
					graph.addEdge(u, v);
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
		
		return graph;

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static UndirectedGraph<String, DefaultEdge> CreateInducedBipartiteGraph(
			UndirectedGraph<String, DefaultEdge> G, Collection<String> A, Collection<String> B) {
		// TODO Auto-generated method stub
		UndirectedGraph<String, DefaultEdge> bigraph = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
		
		bp = new Bipartition();
		
		for(String u : A){
			for(String v : B){
				if(G.containsEdge(u, v)){
					bigraph.addVertex(u);
					bigraph.addVertex(v);
					bigraph.addEdge(u, v);
					if(u.endsWith("L")){
						bp.addToL(u);
						bp.addToR(v);
					}
					else{
						bp.addToL(v);
						bp.addToR(u);
					}
				}
			}
		}
				
 		return bigraph;
	}
	
	public static Bipartition getBipartition() {
		if(bp == null){
			System.out.println("No bipartition found!!");
			return null;
		}
		return bp;
	}

	public static Bipartition getBipartition(UndirectedGraph<String, DefaultEdge> G) {
		if(bp == null){
			System.out.println("No bipartition found!!");
			return null;
		}
		Set<String> V = G.vertexSet();
		
		for(String v : V){
			if(v.endsWith("L"))
				bp.addToL(v);
			else
				bp.addToR(v);
		}
		return bp;
	}
	
	public static long getSizeOfGraph(){
		return size*2;	//return in bytes assuming each character takes 2 bytes
	}

}
