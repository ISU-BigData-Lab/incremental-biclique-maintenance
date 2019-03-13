package utils;

import java.util.Collection;
import java.util.HashSet;

public class SetOperations<T>
{
    
    public static<T> HashSet<T> intersect(Collection<T> A, Collection<T>B){
        
        HashSet<T> R = new HashSet<T>();
        
        if(A.size() >= B.size()){
            for(T b : B){
                if(A.contains(b)){
                    R.add(b);
                }
            }
        }
        else{
            for(T a : A){
                if(B.contains(a)){
                    R.add(a);
                }
            }
        }
        return R;
        
    }

    public static void main(String[] args)
    {
        // TODO Auto-generated method stub

    }

}
