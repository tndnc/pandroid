import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Instance {
	private List<List<Integer>> agentspref;
	Random r = new Random();
	
	
	public Instance(int nbactors){
		agentspref = new ArrayList<List<Integer>>(nbactors);
		for(int i = 0; i < nbactors; i++){
			List<Integer> l = new ArrayList<Integer>(nbactors);
			for(int j = 0; j < nbactors; j++){
				l.add(r.nextInt(nbactors+1));
			}
			agentspref.add(l);
		}
	}
	
	public Instance(int nbactors,int suka){
		agentspref = new ArrayList<List<Integer>>(7);
		List<Integer> l = new ArrayList<Integer>(Arrays.asList(3,5,1,4,2,7,6));
		agentspref.add(l);
		List<Integer> l2 = new ArrayList<Integer>(Arrays.asList(1,5,3,2,7,6,4));
		agentspref.add(l2);
		List<Integer> l3 = new ArrayList<Integer>(Arrays.asList(1,7,4,6,2,3,5));
		agentspref.add(l3);
		List<Integer> l4 = new ArrayList<Integer>(Arrays.asList(2,3,1,4,6,7,5));
		agentspref.add(l4);
		List<Integer> l5 = new ArrayList<Integer>(Arrays.asList(6,1,4,5,2,3,7));
		agentspref.add(l5);
		List<Integer> l6 = new ArrayList<Integer>(Arrays.asList(3,6,1,4,5,2,7));
		agentspref.add(l6);
		List<Integer> l7 = new ArrayList<Integer>(Arrays.asList(2,4,6,7,1,3,5));
		agentspref.add(l7);
	}
	
	public int getSize(){
		return agentspref.size();
	}
	
	public List<Integer> getPrefs(int n){
		return agentspref.get(n);
	}
	
	public void displayInstance(){
		for(int i = 0; i < agentspref.size() ; i++){
			List<Integer> l = agentspref.get(i);
			for(Integer j:l){
				System.out.print(j + " ");
			}
			System.out.println();
			
		}
		System.out.println();
	}
	
}
