from package.modules.generation import generate_random_instance
from package.modules.utils import save_to_file, draw_hotspots
import time


def solve_by_backtrack(instance):
    """Try to find a Locally Envy Free allocation for the given instance with a 
    backtracking algorithm.

    Args:
        instance(list list int): ordered lists of preferences for each agent.

    Returns:
        Resulting allocation if it exists ({actor: object allocated})
        False if no solution can be found.
    """

    # Current actor index. Starts at 0.
    ca_idx = 0
    # Dict containing current allocation.
    alloc = {a: None for a in range(len(instance))}
    # Keeps track of what object was allocated to who and could  not satisfy 
    # the problem.
    impossible_alloc = {a: list() for a in range(len(instance))}  
    # List of currently allocated resources for the current allocation.
    allocated_resources = list() 
    
    hotspots = [[0 for _ in range(len(instance))] for _ in range(len(instance))]
    # hotspots = np.zeros((len(instance),len(instance)),dtype=int)
    
    niter = 0
    # As long as the last agent has not been allocated a resources, the problem
    # has not been solved.
    while not alloc[len(instance)-1] != None:
        # get available resources in order of preference for current agent
        agent_prefs = instance[ca_idx]
        imp_alloc = impossible_alloc[ca_idx]
        available = [r for r in agent_prefs \
            if r not in allocated_resources and r not in imp_alloc]
               
        # try to allocate one of the available resources
        allocation_happened = False
        for r in available:
            niter += 1
            # no constraints for first actor, pick first available resource
            if ca_idx == 0:
                alloc[ca_idx] = r
                allocated_resources.append(r)
                ca_idx += 1
                allocation_happened = True
                break

            # else, check constraints with resource `r`
            # index of `r` in the list of prefs of the current agent
            r_idx_current_agent = instance[ca_idx].index(r)
            # index of the resource given to the previous agent in the list of
            # prefs of the current agent
            idx_in_previous_agent = instance[ca_idx].index(alloc[ca_idx-1])
            # index of `r` in the list of prefs of the previous agent
            r_idx_in_previous_agent = instance[ca_idx-1].index(r)
            # index of the resource given to the previous agent in the list of
            # prefs of the previous agent
            idx_in_previous_agent2 = instance[ca_idx-1].index(alloc[ca_idx-1])
            
            if idx_in_previous_agent > r_idx_current_agent and r_idx_in_previous_agent > idx_in_previous_agent2:
                alloc[ca_idx] = r
                allocated_resources.append(r)
                ca_idx += 1
                allocation_happened = True
                break
            
        if not allocation_happened:
            impossible_alloc[ca_idx] = list()
            ca_idx -= 1
            impossible_alloc[ca_idx].append(alloc[ca_idx])
            allocated_resources.remove(alloc[ca_idx])
            hotspots[instance[ca_idx].index(alloc[ca_idx])][ca_idx] += 1
            alloc[ca_idx] = None
            
        if ca_idx == 0 and len(impossible_alloc[ca_idx]) == len(instance):
            # unsolvable instance
            return False, niter, hotspots
        
    return alloc, niter, hotspots


if __name__ == "__main__":
    number_of_tries = 20
    number_of_agents = 3

    res = list()

    print("Solving {} instances of {} agents by backtrack.".format(number_of_tries, number_of_agents))
    for i in range(number_of_tries):
        instance = generate_random_instance(number_of_agents)

        # track time
        start = time.time()
        alloc, niter, hotspots = solve_by_backtrack(instance)
        end = time.time()
        print(".", end="", flush=True)

        elapsed = (end-start) * 1000 # convert to microseconds
        res.append((instance, alloc, elapsed, niter))

        # print hotspots
        # draw_hotspots(hotspots, instance, alloc, "{}-{}_hotspots".format(number_of_agents, i+1))

    print()
    
    save_to_file(number_of_agents, res)
