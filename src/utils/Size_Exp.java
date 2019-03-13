package utils;

class X{
	int a;
	int b;
	
	public X(int a, int b){
		this.a = a;
		this.b = b;
	}
}

public class Size_Exp {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		X x = new X(4,5);
		
		System.out.println(ObjectSizeFetcher.getObjectSize((Object)x));

	}

}
