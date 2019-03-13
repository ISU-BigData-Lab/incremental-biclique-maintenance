package algorithm.DynamicMBE;

import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import DS.Biclique;
import utils.MurmurHash3;

public class EnumSB_Hashing {
	
	public static Set<BitSet> subsumedB;
	public static int subSize;
	private static long bclique_sc;
	
	private static long sub_count;
	public EnumSB_Hashing(){
		
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
	public static Set<String> run(Set<BitSet> B, Biclique BC, Set<String> H, int size_th){
		
		subsumedB = new HashSet<>();
		
		sub_count = 0L;
		
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
			
			//compute hash of the biclique
			byte[] key = b.toString().getBytes();
			long[] hash = {MurmurHash3.MurmurHash3_x64_64(key, 0), 0 };
			BitSet bs = BitSet.valueOf(hash);
			
			if(B.contains(bs)){	
				//if(!subsumedB.contains(bs)){
					//subsumedB.add(bs);
					sub_count++;
					//System.out.println("subsumed biclique: " + b);
					subSize += (b.getX().size()+b.getY().size());
					B.remove(bs);
					bclique_sc += bs.size()/8;
				//}
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
		//return subsumedB.size();
		
		return sub_count;
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
