import java.io.File; 
import java.io.FileNotFoundException; 
import java.util.Scanner; 
  
public class ReadFromFile 
{ 
	public static void read(String filename,int type) throws Exception 
	  { 
	    File file = new File(filename); 
	    Scanner sc = new Scanner(file); 
	    Parser parser = Parser.getInstance();
	    for(int i = 1, j = 1; sc.hasNextLine() ; i++ , ++j) {
	    	String line = sc.nextLine();
	    	boolean next = sc.hasNextLine();
	    	if(line.charAt(0)=='.') {
	    		j--;
	    		//System.out.println(line + " " + i + " " + j);
	    		//print to line file -> i + address + line
	    		//continue;
	    	}
	    	//System.out.println(line + " " + i + " " + j);
	    	if(type == 0) parser.parseInstruction(line, i , j, !next);
	    	else parser.parseOperation(line, i);
	    }
	  }   
} 