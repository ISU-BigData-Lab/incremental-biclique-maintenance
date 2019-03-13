package DS;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Bipartition {
	
	private Set<String> L;
	private Set<String> R;
	
	public Bipartition(){
		L = new HashSet<>();
		R = new HashSet<>();
	}
	
	public Bipartition(Collection<String> A, Collection<String> B){
		L = new HashSet<>();
		R = new HashSet<>();
		
		L.addAll(A);
		R.addAll(B);
	}
	
	public Bipartition(String file) throws Exception{
		BufferedReader br = new BufferedReader(new FileReader(file)); 
		
		String line1 = br.readLine();
		String line2 = br.readLine();
		
		String[] A = line1.split("\\s+");
		String[] B = line2.split("\\s+");
		
		//System.out.println(A[0]);
		
		L = new HashSet<>();
		R = new HashSet<>();
		
		for(String u : A){
			L.add(u);
		}
		
		for(String v : B){
			R.add(v);
		}
	}
	
	public void addToL(String u){
		L.add(u);
	}
	
	public void addToR(String v){
		R.add(v);
	}
	
	public Set<String> getL(){
		return new HashSet<String>(L);
	}
	
	public Set<String> getR(){
		return new HashSet<String>(R);
	}

}
