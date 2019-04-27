
public class Directive {

	private String operand,directive,label;
	private int error = 0, linesWithoutComment , length = 0;
	private boolean setLocation = false, last = false;
	Parser parser = Parser.getInstance();
	public Directive(String label, String directive,String operand,int linesWithoutComment,boolean last) {
		this.operand=operand;
		this.directive=directive;
		this.label=label;
		this.linesWithoutComment=linesWithoutComment;
		this.last=last;
		start();
	}
	
	void start() {
		if(directive.equalsIgnoreCase("START")||directive.equalsIgnoreCase("ORG")) setLocation = true;
		checkError();
		if(error==0) calculateLength();
	}
	
	private void calculateLength() {
		if(directive.equalsIgnoreCase("BASE")||directive.equalsIgnoreCase("EQU")||directive.equalsIgnoreCase("END"))
			return;
		if(directive.equalsIgnoreCase("START")||directive.equalsIgnoreCase("ORG"))
			return;
		if(directive.equalsIgnoreCase("WORD")) length = solveWord();
		if(directive.equalsIgnoreCase("BYTE")) length = solveByte();
		if(directive.equalsIgnoreCase("RESB")) length = Integer.parseInt(operand);
		if(directive.equalsIgnoreCase("RESW")) length = Integer.parseInt(operand)*3;
		
	}
	
	private void checkError(){
		if(last&&!operand.equalsIgnoreCase("END")) error = error|(1<<21);
		if(label.length()>8) error = error|(1<<1);
		if(label.length()>0 && invalidLabel(label)) {
			error = error|(1<<20);
			return ;
		}
		if(containsSpace(label)) error = error|(1<<14);
		if(label.length()>0) {
			if(directive.equalsIgnoreCase("ORG") || directive.equalsIgnoreCase("BASE") || directive.equalsIgnoreCase("END")) {
				error = error|(1<<5);
			}
		}
		if(operand.length()==0 && !directive.equalsIgnoreCase("END")) {
			error = error|(1<<3);
			return;
		}
		if(directive.equalsIgnoreCase("START")) {
			if(linesWithoutComment!=1) error = error|(1<<16);
			if(wrongHexadecimal(operand)) error = error|(1<<9);
		}
		if(directive.equalsIgnoreCase("END")) {
			if(operand.length()>0) {
				//must be known label
				if (parser.symbolTable.get(operand)==null){
					error = error|(1<<18);
				}
			}
		}
		if(directive.equalsIgnoreCase("BYTE")) {
			if(wrongByteFormat(operand)) error = error|(1<<17); 
			else {
				if((operand.charAt(0)=='X' || operand.charAt(0)== 'x') &&wrongHexadecimal(operand.substring(2,operand.length()-1))) error = error|(1<<10);
			}
		}
		if(directive.equalsIgnoreCase("WORD")) {
			String [] operands = operand.split(",");
			for(int i=0;i<operands.length;i++) {
				String temp = operands[i];
				if(containsChar(temp)) error = error|(1<<18);
			}
		}
		if(directive.equalsIgnoreCase("RESW") || directive.equalsIgnoreCase("RESB")) {
			if(containsChar(operand)) error = error|(1<<18);
		}
		if(directive.equalsIgnoreCase("EQU")) {
			if(label.length()==0) error = error|(1<<19);
			if(wrongHexadecimal(operand)) error = error|(1<<18);
		}
		if(directive.equalsIgnoreCase("BASE")) {
			if(invalidLabel(operand) && wrongHexadecimal(operand)) error = error|(1<<18);
		}
		if(directive.equalsIgnoreCase("ORG")) {
			if (parser.symbolTable.get(operand)==null) {
				error = error|(1<<18);
			}
		}
		if (parser.symbolTable.get(label)!=null) {
			error = error|(1<<4);
		}
	}
		
	protected boolean containsChar(String s) {
		for(int i=0;i<s.length();i++) {
			if(s.charAt(i)<'0' || s.charAt(i)>'9') return true;
		}
		return false;
	}
	
	protected int getError() {
		return error;
	}
	
	protected int getLength() {
		return this.length;
	}
	
	protected boolean containsSpace(String s) {
		for(int i=0;i<s.length();i++)
			if(s.charAt(i)==' ') return true;
		return false;
	}
	
	protected boolean wrongHexadecimal(String s) {
		for(int i=0;i<s.length();i++) {
			if(s.charAt(i)>='0' && s.charAt(i)<='9') continue;
			if((s.charAt(i)>='a' && s.charAt(i)<='f') || (s.charAt(i)>='A' && s.charAt(i)<='F')) continue;
			return true;
		}
		return false;
	}
	
	protected boolean notNumber(String s) {
		for(int i=0;i<s.length();i++)
			if(s.charAt(i)>'9' || s.charAt(i)<'0') return true;
		return false;
	}
	
	
	
	private boolean invalidLabel(String s) {
		if(s.length()==0) return true;
		if(s.charAt(0)>='0' && s.charAt(0)<='9') return true;
		if(s.matches("[a-zA-Z0-9]*")) return false;
		return true;
	}
	
	private int solveWord() {
		String arr[] = operand.split(",");
		return arr.length*3;
	}
	
	private int solveByte() {
		int cnt = operand.length()-3;
		if(operand.charAt(0)=='c') return cnt;
		return cnt/2;
	}
	
	protected boolean getSetLocation() {
		return setLocation;
	}
	
	protected boolean wrongByteFormat(String s) {
		if(s.length()<4) return true;
		char firstChar = s.charAt(0);
		if(firstChar<97) firstChar+=32;
		if(firstChar!='c' && firstChar!='x') return true;
		if(s.charAt(1)!='\'') return true;
		if(s.charAt(s.length()-1)!='\'') return true; 
		int cnt = 0;
		for(int i=0;i<s.length();i++)
			if(s.charAt(i)=='\'') cnt++;
		if(cnt>2) return true;
		cnt = s.length()-3;
		if(firstChar=='x' && cnt%2==1) return true;
		return false;
	}
}
