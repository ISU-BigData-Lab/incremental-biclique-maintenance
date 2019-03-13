package utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jgrapht.Graphs;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

/**
 * This program generates edge stream from real world time stamped bipartite graph. For generating the initial graph 
 * to start computing on the stream, we consider the edges in the earliest timestamp to be in the initial graph. 
 * @author Apurba (January 05, 2017)
 *
 */

public class reduceTBiGraph {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		String gFile = args[0];
		
		//Read the graph along with storing edges with timestamps using a map
		UndirectedGraph<String, DefaultEdge> g = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
		
		FileWriter writer1 = new FileWriter(gFile + "_initial", true);
		FileWriter writer2 = new FileWriter(gFile + "_edge_stream", true);
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(gFile));
			
			String line;
			Map<Long, Set<String>> TE = new TreeMap<>();
			br.readLine();	//for skipping the header
			while((line = br.readLine()) != null){
				String[] splits = line.split("\\s+");
				String u = splits[0] + "L";
				String v = splits[1] + "R";
				long ts = Long.parseLong(splits[splits.length-1]);
				
				String edge = u + " " + v;
				
				Set<String> edges = TE.get(ts);
				
				if(edges == null){
					edges = new HashSet<>();
				}
				
				edges.add(edge);
				TE.put(ts, edges);
				
				g.addVertex(u);
				g.addVertex(v);
			}
			
			TreeSet<Long> timestamps = new TreeSet<>(TE.keySet());
			
			Iterator<Long> it = timestamps.iterator();
			
			//Set<String> edges = TE.get(it.next());
			
			//for(String e : edges){
			//	String u = e.split("\\s+")[0];
			//	String v = e.split("\\s+")[1];
			//	g.addEdge(u, v);
			//}
			
			//for adding 10% edges (with earliest time stamps) for movielens-10m graph
			HashMap<String, Long> eTots = new HashMap<>();
			int count_edges = 0;
			while(it.hasNext()){
				long timestamp = it.next();
				Set<String> edges = TE.get(timestamp);
				for(String e : edges){
//					if(count_edges < 1366832){
//						String u = e.split("\\s+")[0];
//						String v = e.split("\\s+")[1];
//						g.addEdge(u, v);
//						count_edges++;
//					}
//					else
					if(eTots.get(e) == null){	//consider the earliest timestamp is there are multiple edges with different timestamps
						eTots.put(e, timestamp);
						writer2.write(e + "\n");
					}
				}
			}
			
			System.out.println("number of edges in the graph : " + count_edges);
			
			//write the edge stream to a file
			//while(it.hasNext()){
			//	edges = TE.get(it.next());
			//	for(String e : edges){
			//		writer2.write(e + "\n");
			//	}
			//}
			writer2.close();
			
			//write the graph in adjacency list format
			for(String v : g.vertexSet()){
				StringBuilder sb = new StringBuilder();
				sb.append(v);
				List<String> S = Graphs.neighborListOf(g, v);
				if(S.isEmpty()){
					writer1.write(sb.toString());
					writer1.write("\n");
				}
				else{
					for(String u : S){
						sb.append(" " + u);
					}
					writer1.write(sb.toString());
					writer1.write("\n");
				}
			}
			writer1.close();
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
