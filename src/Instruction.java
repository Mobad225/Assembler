import com.sun.prism.impl.shape.OpenPiscesPrismUtils;

public class Instruction {

	private String labelField,opCodeField,operandField;
	private boolean directive = false, setLocation = false ,type4 =false;
	private int lineNumber , error = 0 , lineNumberWithoutComments , length = 0;
	String [] operands;
	Parser parser = Parser.getInstance();
	public Instruction(String label, String opCode, String operand,int lineNumber,int lineWithoutComments,boolean last) {
		labelField = label;
		opCodeField = opCode;
		operandField = operand;
		this.lineNumber=lineNumber;
		lineNumberWithoutComments = lineWithoutComments;
		fixFields();
		operands = operandField.split(",");
		if(parser.directives.contains(opCodeField)) directive = true;
		if(!directive) {
			check();
			if(error==0) calculateLength();
		}
		else {
			Directive directive = new Directive(labelField,opCodeField,operandField,lineNumberWithoutComments,last);
			setLocation = directive.getSetLocation();
			error = directive.getError();
			length = directive.getLength();
		}
	}
	
	protected void fixFields(){
		int cnt = 0;
		for(int i=labelField.length()-1;i>=0;i--,cnt++) {
			if(labelField.charAt(i)!=' ') break;
		}
		labelField = labelField.substring(0, 9-cnt);
		
		cnt = 0;
		for(int i=opCodeField.length()-1;i>=0;i--,cnt++) {
			if(opCodeField.charAt(i)!=' ') break;
		}
		opCodeField = opCodeField.substring(0, 8-cnt);
		
		cnt = 0;
		for(int i=Math.min(17,operandField.length()-1);i>=0;i--,cnt++) {
			if(operandField.charAt(i)!=' ') break;
		}
		operandField = operandField.substring(0, Math.min(operandField.length()-1, 17)+1-cnt);
		operandField=operandField.toUpperCase();
		labelField=labelField.toUpperCase();
		opCodeField=opCodeField.toUpperCase();
		if (opCodeField.length()!=0 && opCodeField.charAt(0)=='+') {
			opCodeField = opCodeField.substring(1,opCodeField.length());
			type4 = true;
		}
		
	}
	
	protected void check() {
		if(opCodeField.length()==0) {
			error = error|(1<<2);
			return;
		}
		if(type4 && parser.opCodeTable.get(opCodeField)==null){
			error = error|(1<<8);
			return;
		}
		if(!type4 && parser.opCodeTable.get(opCodeField)==null) {
			error = error|(1<<8);
			return;
		}
		if(labelField.length()>8 || opCodeField.length()>6) error = error|(1<<1);
		if(operandField.length()==0) error = error|(1<<3);
		if(operands.length!=parser.opCodeTable.get(opCodeField).get(1)) {
			if(parser.opCodeTable.get(opCodeField).get(0)==3 && operands.length==2) {
				if(operands[1].equalsIgnoreCase("x") && !invalidLabel(operands[0])) {
					//ok
				}
				else {
					error = error|(1<<15);
				}
			}
			else {
				error = error|(1<<15);
			}
		}
		else {
			if(parser.opCodeTable.get(opCodeField).get(0)==2 && unknownRegisters()) error = error|(1<<12);
		}
		if(parser.opCodeTable.get(opCodeField).get(0)==3 && containsRegister()) {
			if(!(operands[1].equalsIgnoreCase("x") && !invalidLabel(operands[0]))) {
				error = error|(1<<9);
			}
		}
		if(labelField.length()>0 && invalidLabel(labelField)) error = error|(1<<20);
		
		if (parser.symbolTable.get(labelField)!=null) {
			error = error|(1<<4);
		}
		
	}
	
	protected void calculateLength() {
		//CHECK IF WRONG OPCODE FIRST, AS HASHMAP RETURNS NULL IF NOT FOUND.
			if(type4) {
				if(parser.opCodeTable.get(opCodeField).get(0)==3) {
					this.length = 4;
				}
				else {
					error = error|(1<<7);
				}
			}
			else {
				this.length = parser.opCodeTable.get(opCodeField).get(0);
			}
	}
	
	protected int getLength() {
		return this.length;
	}
	
	protected boolean unknownRegisters() {
		for(int i=0;i<operands.length;i++) 
			if(!parser.registers.contains(operands[i])) return true;
		return false;
	}
	
	protected boolean containsRegister() {
		for(int i=0;i<operands.length;i++) 
			if(parser.registers.contains(operands[i]))return true;
		return false;
	}
	
	private boolean invalidLabel(String s) {
		if(s.length()==0) return true;
		if(s.charAt(0)>='0' && s.charAt(0)<='9') return true;
		if(s.matches("[a-zA-Z0-9]*")) return false;
		return true;
	}
	
	protected boolean getSetter() {
		return setLocation;
	}
	
	protected boolean containsSpace(String s) {
		for(int i=0;i<s.length();i++)
			if(s.charAt(i)==' ') return true;
		return false;
	}
	
	protected String getLabel() {
		return labelField;
	}
	
	protected String getOperand() {
		return operandField;
	}
	
	protected int getError() {
		return error;
	}
	
}
