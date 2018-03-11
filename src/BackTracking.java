import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BackTracking {
	private HashMap<Integer,Integer> alloc;
	private int ca_idx;
	private HashMap<Integer,List<Integer>> impossible_alloc;
	private List<Integer> allocated_ressources;
	private int niter;
	
	
	public BackTracking(){
		alloc = new HashMap<Integer,Integer>();
		impossible_alloc = new HashMap<Integer,List<Integer>>();
		allocated_ressources = new ArrayList<Integer>();
		ca_idx = 0;
		niter = 0;
	}
	
	
	public boolean solveInstance(Instance inst){
		alloc = new HashMap<Integer,Integer>(inst.getSize());
		impossible_alloc = new HashMap<Integer,List<Integer>>(inst.getSize());
		for(int i = 0; i < inst.getSize() ; i++){
			alloc.put(i, -1);
			impossible_alloc.put(i,new ArrayList<Integer>(inst.getSize()));
		}
		
		
		List<Integer> prefs_CurAgent;
		List<Integer> imp_alloc;
		List<Integer> available;
		
		int r_idx_current_agent;
		int idx_in_previous_agent;
		int r_idx_in_previous_agent;
		int idx_in_previous_agent2;
		boolean allocation_happened = false;
		
		//As long as the last agent has not been allocated a resources, the problem
	    //has not been solved.
		while(alloc.get(inst.getSize()-1) == -1){
			System.out.println(ca_idx);
			//get available resources in order of preference for current agent
			prefs_CurAgent = inst.getPrefs(ca_idx);
			imp_alloc = impossible_alloc.get(ca_idx);
			available = new ArrayList<Integer>(prefs_CurAgent);
			if(imp_alloc != null){
				System.out.print("ressources non disponible:");
				for(int i = 0; i<imp_alloc.size();i++){
					System.out.print(imp_alloc.get(i) + " ");
				}
				System.out.println();
				available.removeAll(imp_alloc);
				System.out.print("ressources available:");
				for(int i = 0; i<available.size();i++){
					System.out.print(available.get(i) + " ");
				}
				System.out.println();
			}
			if(allocated_ressources != null){
				available.removeAll(allocated_ressources);
				System.out.print("ressources available:");
				for(int i = 0; i<available.size();i++){
					System.out.print(available.get(i) + " ");
				}
				System.out.println();
			}
			
			
		    
		    allocation_happened = false;
		    
		    //try to allocate one of the available resources
		    for (Integer r: available){
		    	niter++;
		    	
		    	//no constraints for first actor, pick first available resource
		    	if(ca_idx == 0){
		    		alloc.put(ca_idx, r);
		    		allocated_ressources.add(r);
		    		ca_idx++;
		    		allocation_happened = true;
		    		break;
		    	}
		    	
		    	//else, check constraints with resource `r`
		        //index of `r` in the list of prefs of the current agent
		    	r_idx_current_agent = prefs_CurAgent.indexOf(r);
		    	//index of the resource given to the previous agent in the list of prefs of the current agent
		    	idx_in_previous_agent = prefs_CurAgent.indexOf(alloc.get(ca_idx - 1));
		    	//index of `r` in the list of prefs of the previous agent
		    	r_idx_in_previous_agent = inst.getPrefs(ca_idx-1).indexOf(r);
		    	//index of the resource given to the previous agent in the list of prefs of the previous agent
		    	idx_in_previous_agent2 = inst.getPrefs(ca_idx-1).indexOf(alloc.get(ca_idx-1));
		    	
		    	if(idx_in_previous_agent > r_idx_current_agent && r_idx_in_previous_agent > idx_in_previous_agent2){
		    		alloc.put(ca_idx,r);
		            allocated_ressources.add(r);
		            ca_idx++;
		            allocation_happened = true;
		            break;
		    	}
		    
		    }
		    
		    System.out.print("ressources alloue:");
			for(int i = 0; i<allocated_ressources.size();i++){
				System.out.print(allocated_ressources.get(i) + " ");
			}
			System.out.println();
			System.out.println();
		    	
		    if(allocation_happened == false){
		    	ca_idx = ca_idx - 1;
		    	impossible_alloc.get(ca_idx).add(alloc.get(ca_idx));
	            allocated_ressources.remove(alloc.get(ca_idx));
	            alloc.put(ca_idx, -1);
		    }
		    
		    if( ca_idx == 0 && impossible_alloc.get(ca_idx).size() == inst.getSize()){
		    	//unsolvable instance
		    	System.out.println(niter);
		    	return false;
		    }
		    
		
		}
		System.out.println(niter);
		return true;
		
	}
	
	
	
	
	
	
}
