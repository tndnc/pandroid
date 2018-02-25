from gen import generate, generate_random_instance
from ipdb import set_trace
from random import choice


def solve_backtrack(instance):
    """Try to find a Localy Envy Free allocation for the given instance with a 
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
    
    # set_trace()
    
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
            ca_idx -= 1
            impossible_alloc[ca_idx].append(alloc[ca_idx])
            allocated_resources.remove(alloc[ca_idx])
            alloc[ca_idx] = None

        if ca_idx == 0 and len(impossible_alloc[ca_idx]) == len(instance):
            # unsolvable instance
            return False
            
    return alloc


def pprint_instance(instance, allocation=None):
    if not allocation: allocation = dict()
    n = len(instance)
    
    print("\n")
    for i in range(n):
        buffer = "\t"
        for a in range(n):
            obj = instance[a][i]
            buffer += "{}{}\t".format("*" if obj == allocation.get(a) else " ", obj)
        print(buffer)
    
    
if __name__ == "__main__":
    number_of_tries = 10
    number_of_agents = 4

    for _ in range(number_of_tries):
        instance = generate_random_instance(number_of_agents)
        alloc = solve_backtrack(instance)
        pprint_instance(instance, allocation=alloc)
