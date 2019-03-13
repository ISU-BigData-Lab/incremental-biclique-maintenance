package utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import org.jgrapht.Graphs;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class reduceBiGraph {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String gFile = args[0];	//assumed that original graph is in edge list format
		double rp = Double.parseDouble(args[1]);	//probability of retaining edges
		
		double dp = 1 - rp;	//probability of deleting
		
		UndirectedGraph<String, DefaultEdge> g = GraphUtils.readBiGraph(gFile, 0);	//read the graph from file
		
		try {
			FileWriter writer1 = new FileWriter(gFile + "_" + rp, true);
			FileWriter writer2 = new FileWriter(gFile+"_"+rp + "edge_stream", true);
			
			File f = new File(gFile);
			
			HashSet<String> eS = new HashSet<>();
			
			Scanner sc = new Scanner(f);
			while(sc.hasNextInt()){
				String u = sc.nextInt() + "L";
				String v = sc.nextInt() + "R";
				if(Math.random() <= dp){
					g.removeEdge(u, v);
					eS.add(u + " " + v);
				}
			}
			
			//write the deleted edges to file
			for(String e : eS){
				writer2.write(e);
				writer2.write("\n");
			}
			
			//write the reduced graph in file in adjacency list format
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
			writer2.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
