package algorithm.DynamicMBE;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import DS.Biclique;

public class EnumSB {
	
	public static Set<Biclique> subsumedB;
	public static int subSize;
	
	public static long bclique_sc;
	
	public EnumSB(){
		
	}
	/**
	 * 
	 * @param B : Set of bicliques of the original graph
	 * @param NB : A set new bicliques from which subsumed bicliques to be generated
	 * @param batch : Set of new edges that updated the graph
	 * @return Set of bicliques subsumed by BC
	 */
	public static  Set<String> run(Set<String> B, Set<Biclique> BC, Set<int[]> batch){
		
		return null;
	}
	/**
	 * 
	 * @param B : Set of bicliques of the original graph
	 * @param NB : A new biclique from which subsumed bicliques to be generated
	 * @param batch : Set of new edges that updated the graph
	 * @return Set of bicliques subsumed by BC
	 */
	public static Set<String> run(Set<Biclique> B, Biclique BC, Set<String> H, int size_th){
		
		subsumedB = new HashSet<>();
		
		subSize = 0;
		
		bclique_sc = 0;
		
		int size1 = BC.getX().size() * BC.getY().size();
		
		int size2 = H.size();
		
		//System.out.println("size1: " + size1);
		HashSet<Biclique>[] S = new HashSet[size1 + 1];
		
		S[0] = new HashSet<>();
		
		S[0].add(BC);
		
		int idx = 0;
		
		if(size1 > size2){
			for(String e : H){
				String u = e.split("\\s+")[0];
				String v = e.split("\\s+")[1];
				Iterator<Biclique> itr = S[idx].iterator();
				idx++;
				S[idx] = new HashSet<>();
				while(itr.hasNext()){
					Biclique bc = itr.next();
					Set<String> X = bc.getX();
					Set<String> Y = bc.getY();
					
					if(X.contains(u) && Y.contains(v)){
						
						Y.remove(v);
						if(!Y.isEmpty()){
							if(X.size() >= size_th && Y.size() >= size_th){
								Biclique bc1 = new Biclique(X,Y);
								S[idx].add(bc1);
							}
						}
						Y.add(v);
						
						X.remove(u);
						if(!X.isEmpty()){
							if(X.size() >= size_th && Y.size() >= size_th){
								Biclique bc2 = new Biclique(X,Y);
								S[idx].add(bc2);
							}
						}
						X.add(u);
						
					}
					else{
						
						S[idx].add(bc);
						
					}	
				}
			}
		}
		else{
			
			Set<String> X = new HashSet<>(BC.getX());
			Set<String> Y = new HashSet<>(BC.getY());
			
			for(String u : X){
				for(String v : Y){
					String edge = u + " " + v;
					if(H.contains(edge)){
						
						Iterator<Biclique> itr = S[idx].iterator();
						idx++;
						S[idx] = new HashSet<>();
						
						while(itr.hasNext()){
							Biclique bc = itr.next();
							
							Set<String> setX = bc.getX();
							Set<String> setY = bc.getY();
							
							if(setX.contains(u) && setY.contains(v)){
								
								setY.remove(v);
								if(!setY.isEmpty()){
									if(setX.size() >= size_th && setY.size() >= size_th){
										Biclique bc1 = new Biclique(setX, setY);
										S[idx].add(bc1);
									}
								}
								setY.add(v);
								
								setX.remove(u);
								if(!setX.isEmpty()){
									if(setX.size() >= size_th && setY.size() >= size_th){
										Biclique bc2 = new Biclique(setX, setY);
										S[idx].add(bc2);
									}
								}
								setX.add(u);
							}
							else{
								S[idx].add(bc);
							}
						}
						
					}
				}
			}
			
		}
		
		for(Biclique b : S[idx]){
			
			if(B.contains(b)){
				if(!subsumedB.contains(b)){
						subsumedB.add(b);
						subSize += b.getX().size() * b.getY().size();
						B.remove(b);	//no need to compute space cost here, as addition of b to subsumedB and removal from B keeps the space cost unchanged
						//System.out.println("bclique_sc_before_sub: " + bclique_sc);
						bclique_sc += (b.toString().length() - 4 - (b.getX().size()-1)*2 - (b.getY().size()-1)*2)*2;
						//System.out.println("bclique_sc_after_sub: " + bclique_sc);
				}
			}
		}
		
		return null;
		
	}
	
	public static long getdeductedspacecost(){
		return bclique_sc;
	}
	/**
	 * 
	 * @return number of maximal bicliques in the original graph subsumed by the new maximal biclique(s)
	 */
	public static long getSubCount() {
		// TODO Auto-generated method stub
		return subsumedB.size();
	}
	/**
	 * 
	 * @return total size of maximal bicliques in the original graph sbsumed by the new maximal biclique(s)
	 */
	public static int getSubSizes() {
		// TODO Auto-generated method stub
		return subSize;
	}
}
