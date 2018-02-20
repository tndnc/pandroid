from gen import generate
from ipdb import set_trace


def solve_backtrack(instance):
    current_actor_idx = 0
    allocation = {a: None for a in range(len(instance))}
    impossible_alloc = {a: list() for a in range(len(instance))}  
    taken_resources = list()    
    
    #set_trace()
    
    while not allocation[len(instance)-1]:
        # get available resources in order of 
        # pref for current agent
        agent_prefs = instance[current_actor_idx]
        imp_alloc = impossible_alloc[current_actor_idx]
        available = list()
        for r in agent_prefs:
            if r not in taken_resources and r not in imp_alloc:
                available.append(r)
               
        # try to allocate available resources
        allocation_happened = False
        for r in available:
            # no constraints for first actor
            if current_actor_idx == 0:
                allocation[current_actor_idx] = r
                taken_resources.append(r)
                current_actor_idx += 1
                allocation_happened = True
                break
            # check constraints with resource `r`
            r_idx_current_agent = instance[current_actor_idx].index(r)
            idx_in_previous_agent = instance[current_actor_idx].index(allocation[current_actor_idx-1])
            r_idx_in_previous_agent = instance[current_actor_idx-1].index(r)
            idx_in_previous_agent2 = instance[current_actor_idx-1].index(allocation[current_actor_idx-1])
            
            if idx_in_previous_agent < r_idx_current_agent and r_idx_in_previous_agent < idx_in_previous_agent2:
                allocation[current_actor_idx] = r
                taken_resources.append(r)
                current_actor_idx += 1
                allocation_happened = True
                break
            
        if not allocation_happened:
            current_actor_idx -= 1
            impossible_alloc[current_actor_idx].append(allocation[current_actor_idx])
            taken_resources.remove(allocation[current_actor_idx])
            allocation[current_actor_idx] = None

        if current_actor_idx == 0 and len(impossible_alloc[current_actor_idx]) == len(instance):
            # unsolvable instance
            return False
            
    return allocation
    
    
if __name__ == "__main__":
    #instance = generate(3)[4]
    instance = [[0, 1, 2], [1, 0, 2], [2, 0, 1]]
    print(instance)
    print("Allocation: {}".format(solve_backtrack(instance)))