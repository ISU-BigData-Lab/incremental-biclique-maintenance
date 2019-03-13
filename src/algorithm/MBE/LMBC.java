package algorithm.MBE;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import DS.Biclique;
import DS.Bipartition;
import utils.GraphUtils;
import utils.SetOperations;

public class LMBC {

	//private static Graph graph = null; // Input graph in adjacency list
	
	private static UndirectedGraph<String,DefaultEdge> graph;
	
	private static Bipartition bp;

	private static int ms = 1; // Minimum size of maximal biclique

	private static boolean DEBUG = false; // Debug mode

	private static BufferedWriter output_file = null; // Writer to write into
														// output file

	private static int no_of_max_bicliques = 0; // No of bicliques emitted
	
	private static Set<Biclique> BC;
	
	/**
	 * This constructor is for generating bicliques from simple bipartite graph with given bipartition.
	 * 
	 * @param graph : Input graph (simple bipartite)
	 * @param bp : bipartition
	 * @throws Exception
	 */
	public LMBC(UndirectedGraph<String, DefaultEdge> G, Bipartition bp, int size_threshold) throws Exception{
		
		Set<String> X = new HashSet<>(); // Vertex Set

		// Set <Integer> tail_X = graph.vertex_set(); // Tail(X)

		// Set <Integer> gamma_X = graph.vertex_set(); // Gamma(X) i.e.
		// neighborhood
		
		LMBC.graph = G;
		
		BC = new HashSet<>();

		//Set<Integer> tail_X = new HashSet<>(graph.V());

		//Set<Integer> gamma_X = new HashSet<>(graph.V());
		Set<String> tail_X;
		Set<String> gamma_X;
		if(bp.getL().size() < bp.getR().size()){
			tail_X = bp.getL();
			gamma_X = bp.getR();
		}
		else{
			tail_X = bp.getR();
			gamma_X = bp.getL();
			//System.out.println(tail_X);
			//System.out.println(gamma_X);
		}
		
		ms = size_threshold;

		MineLMBC(X, gamma_X, tail_X, ms);
		
	}
	
	/**
	 * This constructor is for generating bicliques from simple undirected graph.
	 * @param graph : Input graph (simple undirected)
	 * @throws Exception
	 */
	public LMBC(UndirectedGraph<String, DefaultEdge> G) throws Exception{
		
		Set<String> X = new HashSet<>(); // Vertex Set

		// Set <Integer> tail_X = graph.vertex_set(); // Tail(X)

		// Set <Integer> gamma_X = graph.vertex_set(); // Gamma(X) i.e.
		// neighborhood
		
		LMBC.graph = G;
		
		BC = new HashSet<>();

		Set<String> tail_X = new HashSet<>(graph.vertexSet());

		Set<String> gamma_X = new HashSet<>(graph.vertexSet());

		MineLMBC(X, gamma_X, tail_X, ms);
		
	}
	/**
	 * 
	 * @return The set of maximal bicliques
	 */
	public Set<Biclique> collect(){
		return BC;
	}
	

	public static void main(String[] args) throws Exception {

		// Record the start time
		long start = System.currentTimeMillis();

		// Check if the user provided the path of the input files
		if (args.length != 6) {

			System.err.println("Incorrect initialization of program.");
			System.out.println("java Pruning <edge file path> <size> <bipartite/non-bipartite 1/0> <adjacency/edgelist 1/0> <DEBUG Mode 0/1>");

			System.exit(1);
		}

		String Path = args[0]; // Read in the input path

		//String Edge_Path = Path + "/edges.txt"; // Path for the edge file
		
		String Edge_Path = Path + "/adjlist";
		
		String Output_Path = Path + "/maximal_bicliques"; // Path to output all maximal
													// bicliques in the input
													// graph



		final int ms = Integer.parseInt(args[1]); // Load the pruning size

		int bigraph = Integer.parseInt(args[2]);	// 1 : bipartite graph, 0 : non-bipartite graph 
		
		int input_type = Integer.parseInt(args[3]);	// 1 : adjacency list, 0: edge list
		
		int size_threshold = Integer.parseInt(args[4]);
		
		int debug = Integer.parseInt(args[5]);	// Check for debugging options
		
		// Load the graph in memory in adjacency list format
		System.out.println("\nLoading graph in memory.\n");
		
		graph = new SimpleGraph<>(DefaultEdge.class);
		
		if(debug == 1)
			DEBUG = true;
		else
			DEBUG = false;
		
		if(bigraph == 1){
			graph = GraphUtils.readBiGraph(Edge_Path, input_type);
			bp = GraphUtils.getBipartition();
			
			output_file = new BufferedWriter(new FileWriter(Output_Path));
			
			new LMBC(graph, bp, size_threshold);
		}
		else{
			graph = GraphUtils.readGraph(Edge_Path, input_type);
			
			new LMBC(graph);
		}
		
		
		
		
		/*if(bigraph == 1){
			
			graph = new SimpleGraph<>(DefaultEdge.class);
			
			//graph = new Graph(Edge_Path, 1);
			
			bp = new Bipartition(bipartition_path);



			System.out.println("debug is " + debug);

			if (debug == 0)
				DEBUG = false;

			output_file = new BufferedWriter(new FileWriter(Output_Path)); // Initialize
																			// the
																			// output

			System.out.println();

			// Perform DFS
			System.out.println("\nPerforming DFS using recursion.");
			
			new LMBC(graph, bp);
			
		}
		else {
			
			graph = new Graph(Edge_Path, 0);
			
			System.out.println("debug is " + debug);

			if (debug == 0)
				DEBUG = false;

			output_file = new BufferedWriter(new FileWriter(Output_Path)); // Initialize
																			// the
																			// output

			System.out.println();

			// Perform DFS
			System.out.println("\nPerforming DFS using recursion.");
			
			new LMBC(graph);
		}*/
		
		int no_of_vertices = graph.vertexSet().size();
		int no_of_edges = graph.edgeSet().size();
		
		System.out.println("No of vertices in the graph is " + no_of_vertices);
		System.out.println("No of edges in the graph is " + no_of_edges);
		
		
		

		// int no_of_vertices = graph.no_of_vertices();
		// int no_of_edges = graph.no_of_edges();



		

		// Record the end time
		long end = System.currentTimeMillis(); // Record algorithm runtime

		output_file.close();

		if(no_of_max_bicliques % 1000 == 0)
			System.out.println("\nTotal number of maximal bicliques in input graph with " + no_of_vertices + " vertices and " + no_of_edges + " edges is " + no_of_max_bicliques + ".");
		System.out.println("\nThe program terminated in " + ((end - start) / 1000) + "secs.\n");

	}
	/**
	 * 
	 * @param X
	 * @param gamma_X
	 * @param tail_X
	 * @param ms
	 * @throws Exception
	 */
	private static void MineLMBC(Set<String> X, Set<String> gamma_X, Set<String> tail_X, int ms) throws Exception {

		if (DEBUG == true) {

			System.out.println("\nThe set X: " + Arrays.toString(X.toArray()));
			System.out.println("The set tail_X: " + Arrays.toString(tail_X.toArray()));
			System.out.println("The set gamma_X: " + Arrays.toString(gamma_X.toArray()));
			System.out.println("\n");
		}

		// Iterate over tail_X to check for neighborhood sizes (Algo lines 1 -
		// 3)

		Iterator<String> it_tail_X = tail_X.iterator(); // Create an iterator for tail_X
		
		HashMap<String, Integer> neighborhood_X_union_v_size = new HashMap<>();
		

		while (it_tail_X.hasNext()) {

			String v = it_tail_X.next();

			X.add(v); // Add v to X

			//int neighborhood_X_union_v_size = graph.getNeighbors(X).size();
			
			int size = SetOperations.intersect(gamma_X, GraphUtils.getNeighbors(graph, v)).size();
			
			//neighborhood_X_union_v_size.put(v, GraphUtils.getNeighbors(graph, X).size());
			neighborhood_X_union_v_size.put(v, size);

			if (DEBUG == true) {

				System.out.println("Size of neighborhood_X_union_v is " + neighborhood_X_union_v_size.get(v));
			}

			if (neighborhood_X_union_v_size.get(v) < ms) {

				if (DEBUG == true) {

					System.out.println("Removing element " + v + " from tail_X.");
				}

				it_tail_X.remove();
			}

			X.remove(v); // Remove v from X

		} // End while

		// Exit condition (Algo lines 4-5)

		int size_X = X.size();
		int size_tail_X = tail_X.size();

		if ((size_X + size_tail_X) < ms)
			return;

		// Sort vertices (Algo line 6) - we will use an array for the same

		// Map of size vs vertices. Each size can have multiple vertices
		HashMap<Integer, Set<String>> map = new HashMap<Integer, Set<String>>();

		// Final sorted array to be filled in
		String[] sorted_tail_X = new String[size_tail_X];

		int count = 0;

		Iterator<String> it_tail_X_new = tail_X.iterator(); // Create an iterator for
													// tail_X

		while (it_tail_X_new.hasNext()) {

			String v = it_tail_X_new.next();

			Set<String> vertex_set = map.get(neighborhood_X_union_v_size.get(v));

			if (vertex_set == null) { // First neighbor of main vertex

				vertex_set = new HashSet<>();
			}

			vertex_set.add(v);

			map.put(neighborhood_X_union_v_size.get(v), vertex_set);

		} // End while

		// Now we need to find out the vertices in sorted order according to
		// |Gamma(XUv)|
		Set<Integer> keys = map.keySet();
		int[] int_keys = new int[map.keySet().size()];
		int j=0;
		for (int k : keys){
			int_keys[j] = k;
			j++;
		}
		Arrays.sort(int_keys);

		for (int i = 0; i < int_keys.length; i++) {

			Set<String> vertex_set = map.get(int_keys[i]);

			if (vertex_set == null) { // vertex_set shouldn't be null

				System.err.println("ERROR! Map error!");
			}

			Iterator<String> it_vertex_set = vertex_set.iterator(); // Create an
															// iterator for
															// vertex_set

			while (it_vertex_set.hasNext()) {

				String v = it_vertex_set.next();

				sorted_tail_X[count] = v;
				
				count += 1;

			}
		}

		// Just to check if things are working fine
		if (count != size_tail_X) {

			System.err.println("ERROR!! Count doesn't match.");
		}

		// Main Loop (Algo lines 7 - 14
		
		//graph.print();

		for (int i = 0; i < sorted_tail_X.length; i++) {

			String v = sorted_tail_X[i];
			
			//System.out.println("v is " + v);

			tail_X.remove(v);

			// Line 9
			if ((size_X + 1 + tail_X.size()) >= ms) {

				X.add(v); // Add v to X

				if (DEBUG == true) {

					System.out.println("\nThe set X: " + Arrays.toString(X.toArray()));

				}
				
				//graph.print();
				
				//Set<String> Gamma_X_union_v = graph.getNeighbors(X);
				
				//Set<String> Gamma_X_union_v = GraphUtils.getNeighbors(graph, X);
				//System.out.println("X: " + X);
				//System.out.println("v: " + v);
				//System.out.println("gamma_X: " + gamma_X);
				//System.out.println("gamma_v: " + GraphUtils.getNeighbors(graph, v));
				Set<String> Gamma_X_union_v = SetOperations.intersect(gamma_X, GraphUtils.getNeighbors(graph, v)); 
				
				//System.out.println(Gamma_X_union_v);
				
				//Set<String> Y = graph.getNeighbors(Gamma_X_union_v);
				
				
				Set<String> Y = GraphUtils.getNeighbors(graph, Gamma_X_union_v);

				// Copy Y for test in line 11
				Set<String> copy_Y = new HashSet<>(Y);

				copy_Y.removeAll(X);

				// Line 11
				if (tail_X.containsAll(copy_Y)) {

					// Line 12
					if (Y.size() >= ms) {
						
						//System.out.println(ms);

						// Output maximal biclique as line 13
						//BiClique b = new BiClique(Y_array, gamma_X_union_v_array);

						//output_file.write(b.StringValue() + "\n");
						if(Y.iterator().next().endsWith("L")){
							//output_file.write(Y + "*" + Gamma_X_union_v + "\n");
							BC.add(new Biclique(Y,Gamma_X_union_v));
						}
						else{
							//output_file.write(Gamma_X_union_v + "*" + Y + "\n");
							BC.add(new Biclique(Gamma_X_union_v, Y));
						}

						if (DEBUG == true) {

							System.out.println(Y + "*" + Gamma_X_union_v);
						}

						no_of_max_bicliques = no_of_max_bicliques + 1;
					}

					// Need to generate the new sets before calling recursion
					//Set<String> new_Gamma_X = new HashSet<>();
					
					//new_Gamma_X.addAll(X);
					
					Set<String> new_Gamma_X = SetOperations.intersect(gamma_X, GraphUtils.getNeighbors(graph, v));
					//for (int j = 0; j < gamma_X_union_v_array.length; j++) {

					//	new_gamma_X.add(gamma_X_union_v_array[j]);
					//}

					Set<String> new_tail_X = new HashSet<>(tail_X);
					new_tail_X.removeAll(Y);

					// Recursion - Line 14
					MineLMBC(Y, new_Gamma_X, new_tail_X, ms);
				}

				X.remove(v); // Remove v from X

			} // End if

		} // End for

	} // End MineLMBC

}
