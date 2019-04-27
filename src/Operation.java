import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Operation {
	String operationName;
	int operationLength,operationType,operationOperands;
	Parser parser = Parser.getInstance();
	public Operation(String opName,int length,int type,int operands) {
		operationName = opName;
		operationLength = length;
		operationType = type;
		operationOperands = operands;
		fill();
	}
	
	protected void fill() {
		if(this.operationType==1) 
			parser.directives.add(operationName);
		else {
			ArrayList<Integer> temp = new ArrayList<>();
			temp.add(operationLength);
			temp.add(operationOperands);
			parser.opCodeTable.put(operationName, temp);
		}
	}
}
