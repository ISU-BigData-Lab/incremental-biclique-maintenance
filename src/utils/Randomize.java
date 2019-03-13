package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;

public class Randomize {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String edges = args[0];
		if(args.length < 1){
			System.out.println("usage: java <prog> <input_file>");
			return;
		}
		try{
			File f = new File(edges);
			LineNumberReader lr = new LineNumberReader(new FileReader(f));
			lr.skip(Long.MAX_VALUE);
			int n = lr.getLineNumber();
			System.out.println(n);
			int[] A = new int[n];
			for(int i = 0; i < n; i++)
				A[i] = i;
			//random permutation
			for(int i=0; i < n; i++){
				int r = i + (int)(Math.random()*(n-i));
				int temp = A[r];
				A[r] = A[i];
				A[i] = temp;
			}

			String[] Array = new String[n];

			BufferedReader br = new BufferedReader(new FileReader(f));

			String line;
			int idx = 0;
			while((line = br.readLine()) != null){
				//System.out.println(A[idx]);
				Array[A[idx]] = line;
				idx++;
			}
			FileWriter fw = new FileWriter(edges + "_new", true);
			for(int i = 0; i < Array.length; i++){
				fw.write(Array[i]);
				fw.write("\n");
			}
			fw.close();
			br.close();
			lr.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}

	}

}
