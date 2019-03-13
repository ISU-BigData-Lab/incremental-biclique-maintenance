package DS;

/**

 * This program creates an undirected graph G from an input file 
 * where the edges are represented as pair of integers 
 * and integers represent vertices of the graph. 
 * It is assumed that there is no duplicate edge and self loop in the graph.
 * */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import utils.SetOperations;

public class Graph {

	private Scanner scanner;

	public Map<String, Set<String>> AdjList;
	
	private boolean bigraph;	// 1 : bipartite graph; 0: non-bipartite graph 

	public Graph() {

		this.AdjList = new HashMap<String, Set<String>>();
	}

	public Graph(Graph G) {

		AdjList = new HashMap<String, Set<String>>(G.AdjList);

	}

	// Read input graph when the input graph is in the form of adjacency list
	// instead of edge list in a file
	// args is referred to that file
	public Graph(String args, int val) {

		AdjList = new HashMap<String, Set<String>>();

		if (val == 1) {
			/* read bipartite graph */
			try{
				BufferedReader br = new BufferedReader(new FileReader(args));
				String line;
				while((line = br.readLine()) != null){
					String[] splits = line.split("\\s+");
					if(splits.length == 1){
						String u = splits[0] + "L";
						AdjList.put(u, new TreeSet<String>());
					}
					else{
						String u = splits[0] + "L";
						Set<String> S = new HashSet<>();
						for(int i=1; i < splits.length; i++){
							S.add(splits[i] + "R");
						}
						AdjList.put(u,S);
					}
				}
				br.close();
			}catch (IOException e){
				e.printStackTrace();
			}
		} else {
				/*Read non-bipartite graph*/
			try {
				BufferedReader br = new BufferedReader(new FileReader(args));
				String line;
				while ((line = br.readLine()) != null) {
					String[] splits = line.split("\\s+");
					if (splits.length == 1) {
						AdjList.put(splits[0], new TreeSet<String>());
					} else {
						String u = splits[0];
						Set<String> S = new HashSet<String>();
						for (int i = 1; i < splits.length; i++) {
							S.add(splits[i]);
						}
						AdjList.put(u, S);
					}
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public Graph(String args) {

		AdjList = new HashMap<String, Set<String>>();

		try {
			File file = new File(args);
			scanner = new Scanner(file);

			while (scanner.hasNextInt()) {
				String u = scanner.next();
				String v = scanner.next();

				if (u != v)
					addEdge(u, v);
				// System.out.println(i + " " + j);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addVertex(String u) {
		if (AdjList.get(u) == null) {
			Set<String> S = new TreeSet<String>();
			AdjList.put(u, S);
		}
	}

	public void addEdge(String u, String v) {

		if (AdjList.get(u) == null) {

			Set<String> S = new TreeSet<String>();
			S.add(v);
			AdjList.put(u, S);
		} else {

			Set<String> S = AdjList.get(u);
			S.add(v);
			AdjList.put(u, S);
		}

		if (AdjList.get(v) == null) {

			Set<String> S = new TreeSet<String>();
			S.add(u);
			AdjList.put(v, S);
		} else {

			Set<String> S = AdjList.get(v);
			S.add(u);
			AdjList.put(v, S);
		}

		// AdjList.get(u).add(v);
		// AdjList.get(v).add(u);

	}

	public boolean containsEdge(String u, String v) {
		if (AdjList.get(u) != null) {
			if (AdjList.get(u).contains(v))
				return true;
			else
				return false;
		}
		return false;
	}

	public void removeEdge(String u, String v) {

		if (AdjList.get(u) != null) {
			if (AdjList.get(u).contains(v)) {
				AdjList.get(u).remove(v);
				AdjList.get(v).remove(u);
			}
		}

	}

	public void print() {
		for (String v : AdjList.keySet()) {
			System.out.print(v + ":");
			for (String u : AdjList.get(v)) {
				System.out.print(u + ";");
			}
			System.out.println();
		}
	}

	public void print(FileWriter fw) {
		try {
			for (String v : AdjList.keySet()) {
				fw.write(v + " ");
				for (String u : AdjList.get(v)) {
					fw.write(u + " ");
				}
				fw.write("\n");
			}
		} catch (IOException e) {
		}
	}

	public int MaxDegree() {
		int max = -1;
		for (String u : AdjList.keySet()) {
			int size = AdjList.get(u).size();
			if (max < size) {
				max = size;
			}
		}
		return max;
	}

	public int MinDegree() {
		int min = 99999;
		for (String u : AdjList.keySet()) {
			int size = AdjList.get(u).size();
			if (min > size) {
				min = size;
			}
		}
		return min;
	}

	public double AvgDegree() {
		int sum = 0;
		for (String u : AdjList.keySet()) {
			sum += AdjList.get(u).size();
		}

		return ((double) sum / (double) (2 * AdjList.size()));
	}

	public int numEdges() {
		int size = 0;
		for (String u : AdjList.keySet()) {
			size += AdjList.get(u).size();
		}
		return size / 2;
	}

	public int numVertices() {
		return AdjList.size();
	}

	public Set<String> V() {
		return AdjList.keySet();
	}

	public int getSize() {
		int size = 0;
		for (String u : AdjList.keySet()) {
			size++;
			size += AdjList.get(u).size();
		}
		return size;
	}

	/**
	 * 
	 * @param X
	 *            : a set of vertices
	 * @return Set<Integer> : Set of vertices adjacent to all vertices in X
	 *         (common neighbors)
	 */
	public Set<String> getNeighbors(Set<String> X) {

		Set<String> result = new TreeSet<String>();

		Iterator<String> it = X.iterator();

		String u = it.next();

		result.addAll(AdjList.get(u));
		while (it.hasNext()) {
			result = SetOperations.intersect(result, AdjList.get(it.next()));
		}
		return result;

	}

	/**
	 * 
	 * @param u
	 * @return Set of vertices adjacent to u
	 */
	public Set<String> getNeighbors(String u) {
		return AdjList.get(u);
	}

	public Graph CreateInducedBipartiteGraph(Set<String> A, Set<String> B) {

		Graph g = new Graph();

		for (String u : A) {
			for (String v : B) {
				if (containsEdge(u, v)) {
					g.addEdge(u, v);
				} else {
					g.addVertex(u);
					g.addVertex(v);
				}
			}
		}

		return g;
	}

	public void clear() {
		AdjList.clear();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Graph g = new Graph(args[0]);

		System.out.println("Before removal of edges");
		g.print();

		// g.removeEdge(1,2);
		// g.removeEdge(2,3);
		System.out.println("After removal of (1,2) and (2,3)");
		g.print();

		// new Tomita(g);

	}

}
