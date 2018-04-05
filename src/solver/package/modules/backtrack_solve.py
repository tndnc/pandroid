# def solve_by_backtrack(instance):
#     """Try to find a Locally Envy Free allocation for the given instance with a 
#     backtracking algorithm.

#     Args:
#         instance(list list int): ordered lists of preferences for each agent.

#     Returns:
#         Resulting allocation if it exists ({actor: object allocated})
#         False if no solution can be found.
#     """

#     # Current actor index. Starts at 0.
#     ca_idx = 0
#     # Dict containing current allocation.
#     alloc = {a: None for a in range(len(instance))}
#     # Keeps track of what object was allocated to who and could  not satisfy 
#     # the problem.
#     impossible_alloc = {a: list() for a in range(len(instance))}  
#     # List of currently allocated resources for the current allocation.
#     allocated_resources = list() 
    
#     hotspots = [[0 for _ in range(len(instance))] for _ in range(len(instance))]
#     # hotspots = np.zeros((len(instance),len(instance)),dtype=int)
    
#     niter = 0
#     # As long as the last agent has not been allocated a resources, the problem
#     # has not been solved.
#     while not alloc[len(instance)-1] != None:
#         # get available resources in order of preference for current agent
#         agent_prefs = instance[ca_idx]
#         imp_alloc = impossible_alloc[ca_idx]
#         available = [r for r in agent_prefs \
#             if r not in allocated_resources and r not in imp_alloc]
               
#         # try to allocate one of the available resources
#         allocation_happened = False
#         for r in available:
#             niter += 1
#             # no constraints for first actor, pick first available resource
#             if ca_idx == 0:
#                 alloc[ca_idx] = r
#                 allocated_resources.append(r)
#                 ca_idx += 1
#                 allocation_happened = True
#                 break

#             # else, check constraints with resource `r`
#             # index of `r` in the list of prefs of the current agent
#             r_idx_current_agent = instance[ca_idx].index(r)
#             # index of the resource given to the previous agent in the list of
#             # prefs of the current agent
#             idx_in_previous_agent = instance[ca_idx].index(alloc[ca_idx-1])
#             # index of `r` in the list of prefs of the previous agent
#             r_idx_in_previous_agent = instance[ca_idx-1].index(r)
#             # index of the resource given to the previous agent in the list of
#             # prefs of the previous agent
#             idx_in_previous_agent2 = instance[ca_idx-1].index(alloc[ca_idx-1])
            
#             if idx_in_previous_agent > r_idx_current_agent \
#             and r_idx_in_previous_agent > idx_in_previous_agent2:
#                 alloc[ca_idx] = r
#                 allocated_resources.append(r)
#                 ca_idx += 1
#                 allocation_happened = True
#                 break
            
#         if not allocation_happened:
#             impossible_alloc[ca_idx] = list()
#             ca_idx -= 1
#             impossible_alloc[ca_idx].append(alloc[ca_idx])
#             allocated_resources.remove(alloc[ca_idx])
#             hotspots[instance[ca_idx].index(alloc[ca_idx])][ca_idx] += 1
#             alloc[ca_idx] = None
            
#         if ca_idx == 0 and len(impossible_alloc[ca_idx]) == len(instance):
#             # unsolvable instance
#             return False, niter, hotspots
        
#     return alloc, niter, hotspots


def _assertNoEnvyC1(instance, alloc, first_index, second_index, r):
    r_first = instance[first_index].index(r)
    idx_in_prev = instance[first_index].index(alloc[second_index])
    r_scnd = instance[second_index].index(r)
    idx_in_prev2 = instance[second_index].index(alloc[second_index])

    return idx_in_prev > r_first and r_scnd > idx_in_prev2

def _assertNoEnvyC2(instance, alloc, first_index, second_index, r):
    r_first = instance[first_index].index(r)
    idx_in_prev = instance[first_index].index(alloc[first_index])
    r_scnd = instance[second_index].index(r)
    idx_in_prev2 = instance[second_index].index(alloc[first_index])

    return idx_in_prev < r_first and r_scnd < idx_in_prev2


def backtrack_from_agent(instance, agent_idx):
    ca_idx = agent_idx
    alloc = {a: None for a in range(len(instance))}
    impossible_alloc = {a: list() for a in range(len(instance))}
    allocated_resources = list()
    niter = 0
    last_agent_idx = agent_idx-1 if agent_idx != 0 else len(instance)-1
    hotspots = [[0 for _ in range(len(instance))] for _ in range(len(instance))]

    while not alloc[last_agent_idx] != None:

        agent_prefs = instance[ca_idx]
        imp_alloc = impossible_alloc[ca_idx]
        available = [r for r in agent_prefs \
            if r not in allocated_resources and r not in imp_alloc]

        allocation_happened = False
        for r in available:
            niter += 1

            if ca_idx == agent_idx:
                alloc[ca_idx] = r
                allocated_resources.append(r)
                ca_idx = (ca_idx + 1) % len(instance)
                allocation_happened = True
                break

            next_index = (ca_idx + 1) % len(instance)
            if ca_idx == 0:
                if next_index == agent_idx:
                    if _assertNoEnvyC2(instance, alloc, next_index, next_index-1, r):
                        alloc[ca_idx] = r
                        allocated_resources.append(r)
                        allocation_happened = True
                else:
                    alloc[ca_idx] = r
                    allocated_resources.append(r)
                    ca_idx = (ca_idx + 1) % len(instance)
                    allocation_happened = True
                break

            first_index = ca_idx 
            second_index = ca_idx-1 

            c1 = _assertNoEnvyC1(instance, alloc, first_index, second_index, r)
            c2 = _assertNoEnvyC2(instance, alloc, next_index, next_index-1, r) \
                if next_index == agent_idx and next_index != 0 else True

            if c1 and c2:
                alloc[ca_idx] = r
                allocated_resources.append(r)
                ca_idx = (ca_idx + 1) % len(instance)
                allocation_happened = True
                break

        if not allocation_happened:
            impossible_alloc[ca_idx] = list()
            ca_idx = ca_idx - 1 if ca_idx != 0 else len(instance) - 1
            impossible_alloc[ca_idx].append(alloc[ca_idx])
            allocated_resources.remove(alloc[ca_idx])
            hotspots[instance[ca_idx].index(alloc[ca_idx])][ca_idx] += 1
            alloc[ca_idx] = None

        if ca_idx == agent_idx and len(impossible_alloc[ca_idx]) == len(instance):
            return False, niter, hotspots

    return alloc, niter, hotspots


def compute_optimal_solutions(instance):
    solutions = list()
    metadata = list()
    revrsd_instance = instance[::-1]
    instance_length = len(instance)
    
    for actor_idx in range(instance_length):
        sol, niter, hotspots = backtrack_from_agent(instance, actor_idx)
        if sol and sol not in solutions: 
            # print("solutions: {}".format(solutions))
            # print("Sol: {}".format(sol))
            solutions.append(sol)
            metadata.append({
                'niter': niter,
                'hotspots': hotspots
            })
            
        # compute solution in the reversed order
        sol, niter, hotspots = backtrack_from_agent(revrsd_instance, actor_idx)
        if not sol: continue
        # reverse solution
        reversed_solution = dict()
        l = instance_length - 1
        for a in range(instance_length):
            reversed_solution[a] = sol.get(l - a)

        if reversed_solution not in solutions: 
            solutions.append(reversed_solution)
            metadata.append({
                'niter': niter,
                'hotspots': hotspots
            })

    return solutions, metadata

