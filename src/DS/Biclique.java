package DS;

import java.util.Set;
import java.util.TreeSet;

public class Biclique {
	
	private Set<String> X;
	private Set<String> Y;
	
	public Biclique(Set<String> A, Set<String> B){
		X = new TreeSet<>();
		Y = new TreeSet<>();
		
		X.addAll(A);
		Y.addAll(B);
	}
	
	public Set<String> getX(){
		return X;
	}
	
	public Set<String> getY(){
		return Y;
	}
	
	@Override
	public String toString(){
	
		return X.toString() + Y.toString();
	}
	
	@Override
	public boolean equals(Object o){
		
		Biclique bc = (Biclique)o;
		return this.X.equals(bc.X) && this.Y.equals(bc.Y);
	}
	
	@Override
	public int hashCode(){
		//String bc = X + "*" + Y;
		String bc = this.toString();
		return bc.hashCode();
	}

}
