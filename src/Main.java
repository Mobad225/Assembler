import java.util.HashMap;

public class Main {

	public static void main(String[] args) throws Exception {
		ReadFromFile reader = new ReadFromFile();
		reader.read("optable",1);
		System.out.println();
		reader.read("code",0);
		WriteToFile wrt = WriteToFile.getInstance();
		wrt.print();
	}
}
