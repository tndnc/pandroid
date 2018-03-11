
public class Main {
	
	
	public static void main(String [] args){
		BackTracking b;
		Instance i;
		
		i = new Instance(3,4);
		i.displayInstance();
		b = new BackTracking();
		
		System.out.println(b.solveInstance(i));
	}
}
