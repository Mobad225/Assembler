import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

public class Parser {
	
	private static Parser parser = null;
	WriteToFile writeToFile = WriteToFile.getInstance();
	private Parser() {
		
	}
	
	public static Parser getInstance() {
		if(parser==null) 
			parser = new Parser();
		return parser;
	}
	private String line;
	int locationCounter = 0;
	protected HashMap<String,ArrayList<Integer> > opCodeTable = new HashMap<>();
	protected HashSet<String> directives = new HashSet<>(); 
	protected HashSet<String> registers = new HashSet<>(Arrays.asList("X", "A", "S","T","B"));
	protected HashMap<String,Integer> symbolTable = new HashMap<>();
	
	protected void parseInstruction(String line,int lineNumber,int lineNumberWithoutComments,boolean last) {
		if(line.length()>0 && line.charAt(0)=='.') {
			writeToFile.add(line,0,lineNumber,locationCounter);
			return;
		}
		this.line=line;
		String labelField = line.substring(0, 9);
		String opCodeField = line.substring(9,17);
		String operandField = line.substring(17,Math.min(35, line.length()));
		Instruction instruction = new Instruction(labelField, opCodeField, operandField,lineNumber,lineNumberWithoutComments,last);
		int error = instruction.getError();
		if(error==0) {
			if(instruction.getSetter()) {
				if (symbolTable.get(instruction.getOperand())!=null) {
					locationCounter = symbolTable.get(instruction.getOperand());
				}
				else {
					locationCounter = Integer.parseInt(instruction.getOperand(),16);
				}
			
				if (instruction.getLabel().length()!=0) {
					symbolTable.put(instruction.getLabel(),locationCounter);
				}
				writeToFile.add(line, error, lineNumber, locationCounter);
		}
		else {
			if (instruction.getLabel().length()!=0) 
				symbolTable.put(instruction.getLabel(),locationCounter);
			locationCounter +=instruction.getLength();
			writeToFile.add(line, error, lineNumber, locationCounter);
		}
		}
			else{
				//error
				writeToFile.add(line, error, lineNumber, locationCounter);
			}
		
	}
	
	protected void parseOperation(String line,int lineNumber) {
		String [] arr = line.split(" ");
		String operationName = arr[0];
		int operationLength= Integer.parseInt(arr[1]);
		int operationType = Integer.parseInt(arr[2]);
		int operationOperands = Integer.parseInt(arr[3]);
		//System.out.println(operationName + operationLength + operationType);
		Operation operation = new Operation(operationName,operationLength,operationType,operationOperands);
	}
}
