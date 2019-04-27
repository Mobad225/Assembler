import java.util.ArrayList;

public class WriteToFile {
	private ArrayList<String> lines = new ArrayList<>();
	private static WriteToFile writeToFile = null;
	String [] errors = {"",
			"Misplaced Label","Missing Opcode","Missing Operand","Duplicate label","Cant Have label",
			"Cant Have Operand","Wrong Operation Prefix","Unrecognized Opcode","Unknown Operand","Not a Hexadecimal String",
			"Cant be format 4 Instruction","Unknown Register","","","Wrong Number of Operands",
			"Start not at First line","Wrong Directive Format","Wrong Directive Operand","Missing Directive Label","Wrong Label",
			"Missing End Statement"};
	private WriteToFile() {
		String start = "Line no.";
		while(start.length()<10) start+=" ";
		
		String temp = "Address";
		while(temp.length()<10) temp+=" ";
		start+=temp;
		
		temp="Label";
		while(temp.length()<9) temp+=" ";
		start+=temp;
		
		temp="Opcode";
		while(temp.length()<6) temp+=" ";
		start+=temp;
		
		start+="Operands";
		lines.add(start);
	}
	
	public static WriteToFile getInstance() {
		if(writeToFile == null) writeToFile = new WriteToFile();
		return writeToFile;
	}
	
	protected void add(String line,int error,int lineNumber,int locationCounter) {
		//adding line number
		String newLine = Integer.toString(lineNumber);		
		while(newLine.length()<10) newLine+=" ";
		
		//adding address
		String temp = Integer.toHexString(locationCounter);
		while(temp.length()<10) temp+=" ";
		newLine+=temp;
		
		//adding line
		newLine+=line;
		if(error == 0) {
			lines.add(newLine);
			return;
		}
		
		for(int i=0;i<22;i++) {
			if((error & (1<<i)) != 0) {
				newLine+="\n\t***";
				newLine+=errors[i];
			}
		}
		lines.add(newLine);
	}
	
	protected void print() {
		for(int i=0;i<lines.size();i++)
			System.out.println(lines.get(i));
	}
}
