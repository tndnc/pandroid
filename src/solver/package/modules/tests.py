from package.modules.generation import generate_random_instance, generate_solvable
from package.modules.export import export_toLATEX
from package.modules.backtrack_solve import (
    backtrack_from_agent, compute_optimal_solutions
)
from package.modules.utils import pprint_instance
from package.modules.analysis import *
from package.modules.asp_solve import solve as asp_solve
import networkx as nx


def test_(number_of_tries=20, number_of_agents=3):
    import time

    res = list()

    print("Solving {} instances of {} agents by backtrack."\
        .format(number_of_tries, number_of_agents))
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
        # draw_hotspots(hotspots, instance, alloc, "{}-{}_hotspots"\
        # .format(number_of_agents, i+1))

    print()
    
    save_to_file(number_of_agents, res)


def test_ReverseSolution(instance):
    sol1, niter1, _ = backtrack_from_agent(instance, 0)
    # reversed_instance = instance[::-1]
    sol2, niter2, _ = backtrack_from_agent(instance, 3)
    return sol1, sol2, niter1, niter2


def test_export():
    instance = generate_random_instance(4)
    alloc, niter, _ = solve_by_backtrack(instance)
    h = h1(instance, alloc, niter)
    latexstr = export_toLATEX(instance=instance, 
        allocation=alloc, niter=niter, h=h, env='subfigure')
    print(latexstr)


def test_backtrack():
    instance = [
        [5, 2, 3, 1, 4],
        [5, 1, 3, 4, 2],
        [5, 3, 4, 1, 2],
        [1, 5, 2, 3, 4],
        [3, 4, 5, 2, 1]
    ]
    from package.modules.utils import pprint_instance
    print(pprint_instance(instance))
    sol, niter, _ = backtrack_from_agent(instance, 1)
    print(sol)


def test_generation(number_of_tries=100):

    print("Generating {} instances".format(number_of_tries))

    fails = 0
    f_instances = list()
    for _ in range(number_of_tries):
        instance = generate_solvable(4)
        solutions, metadata = compute_optimal_solutions(instance)
        if len(solutions) > 0:
            print(".", end="", flush=True)
        else:
            print("x", end="", flush=True)
            fails += 1
            f_instances.append(instance)

    print()
    print("Number of fails: {}".format(fails))


def test_graph():
    instance = generate_solvable(4)
    sols = asp_solve(instance=instance)
    print("Number of solutions: {}".format(len(sols)))
    G = get_optima_graph(instance, sols)
    print("Size of attraction basins: {}".format(len(G)-len(sols)))
    # print("Diameter: {}".format(nx.diameter(G)))
    show_optima_graph(G)


test_instances = {
    "3-1": [
        [0, 1, 2],
        [1, 2, 0],
        [2, 0, 1],
    ],
    "3-2": [
        [1, 0, 2],
        [2, 0, 1],
        [1, 0, 2],
    ],

    "4-1": [
        [0, 3, 2, 1],
        [1, 3, 2, 0],
        [2, 0, 1, 3],
        [2, 1, 3, 0],
    ],
    "4-2": [
        [3, 2, 0, 1],
        [1, 2, 0, 3],
        [0, 3, 2, 1],
        [0, 2, 1, 3],
    ],
    "4-3": [
        [0, 1, 3, 2],
        [0, 2, 1, 3],
        [1, 3, 2, 0],
        [3, 2, 0, 1],
    ],
    "4-4": [
        [0, 2, 3, 1],
        [0, 1, 2, 3],
        [3, 2, 0, 1],
        [3, 1, 0, 2],
    ],

    "5-1": [
        [2, 0, 4, 1, 3],
        [0, 4, 3, 2, 1],
        [2, 0, 1, 3, 4],
        [3, 2, 4, 1, 0],
        [2, 1, 0, 3, 4],
    ],
    "5-2": [
        [4, 3, 1, 0, 2],
        [0, 4, 2, 1, 3],
        [4, 3, 1, 0, 2],
        [0, 2, 1, 4, 3],
        [3, 4, 1, 2, 0],
    ],
    "5-3": [
        [4, 2, 3, 0, 1],
        [4, 1, 3, 2, 0],
        [4, 2, 0, 1, 3],
        [1, 3, 0, 2, 4],
        [4, 1, 2, 0, 3],
    ],
    "5-4": [
        [4, 3, 2, 0, 1],
        [3, 2, 1, 4, 0],
        [2, 4, 0, 3, 1],
        [0, 3, 1, 4, 2],
        [0, 4, 2, 1, 3],
    ],
    "5-5": [
        [0, 1, 3, 2, 4],
        [0, 1, 4, 3, 2],
        [0, 2, 3, 1, 4],
        [4, 1, 3, 2, 0],
        [0, 4, 3, 2, 1],
    ],

    "6-1": [
        [4, 2, 3, 1, 0, 5],
        [1, 3, 5, 4, 2, 0],
        [4, 3, 2, 0, 5, 1],
        [5, 2, 1, 4, 3, 0],
        [5, 3, 2, 0, 1, 4],
        [0, 5, 2, 4, 1, 3],
    ],
    "6-2": [
        [5, 0, 3, 2, 4, 1],
        [5, 0, 4, 1, 3, 2],
        [2, 4, 0, 3, 1, 5],
        [3, 1, 5, 0, 4, 2],
        [4, 1, 2, 3, 5, 0],
        [3, 1, 2, 0, 5, 4],
    ],
    "6-3": [
        [4, 0, 3, 2, 5, 1],
        [4, 3, 1, 2, 0, 5],
        [3, 0, 2, 5, 1, 4],
        [5, 2, 4, 1, 3, 0],
        [1, 3, 5, 0, 2, 4],
        [0, 5, 1, 4, 2, 3],
    ],
    "6-4": [
        [2, 1, 0, 3, 4, 5],
        [4, 2, 3, 5, 1, 0],
        [4, 0, 1, 3, 5, 2],
        [0, 3, 5, 2, 4, 1],
        [4, 5, 1, 0, 3, 2],
        [3, 0, 5, 1, 2, 4],
    ],
    "6-5": [
        [4, 0, 2, 5, 1, 3],
        [4, 3, 2, 1, 5, 0],
        [2, 4, 5, 0, 1, 3],
        [1, 5, 3, 2, 0, 4],
        [5, 3, 4, 0, 2, 1],
        [3, 2, 5, 0, 1, 4],
    ],
    "6-6": [
        [3, 0, 5, 2, 1, 4],
        [5, 2, 1, 4, 3, 0],
        [1, 2, 0, 3, 5, 4],
        [0, 1, 4, 5, 2, 3],
        [3, 0, 2, 4, 1, 5],
        [3, 5, 4, 0, 1, 2],
    ],
    
    "7-1": [
        [6, 4, 5, 3, 0, 1, 2],
        [1, 4, 2, 6, 5, 3, 0],
        [1, 0, 4, 6, 3, 5, 2],
        [2, 5, 1, 3, 6, 0, 4],
        [2, 0, 6, 3, 4, 5, 1],
        [0, 5, 3, 2, 1, 6, 4],
        [6, 4, 0, 5, 3, 2, 1],
    ],
    "7-2": [
        [0, 4, 2, 5, 1, 6, 3],
        [5, 3, 4, 0, 1, 6, 2],
        [2, 4, 6, 5, 1, 3, 0],
        [3, 2, 5, 0, 1, 4, 6],
        [3, 6, 1, 4, 2, 0, 5],
        [0, 3, 2, 5, 6, 4, 1],
        [2, 4, 1, 6, 0, 3, 5],
    ],
    "7-3": [
        [4, 3, 6, 2, 0, 5, 1],
        [0, 6, 1, 5, 2, 3, 4],
        [4, 5, 0, 3, 2, 6, 1],
        [2, 3, 1, 6, 0, 4, 5],
        [4, 3, 1, 5, 2, 6, 0],
        [6, 0, 3, 1, 4, 2, 5],
        [4, 2, 1, 6, 5, 3, 0],
    ],
    "7-4": [
        [0, 1, 3, 5, 4, 6, 2],
        [1, 4, 3, 2, 0, 6, 5],
        [6, 0, 3, 1, 5, 4, 2],
        [1, 2, 3, 4, 5, 6, 0],
        [6, 1, 5, 0, 2, 3, 4],
        [6, 5, 2, 3, 4, 1, 0],
        [1, 5, 0, 2, 6, 4, 3],
    ],
    "7-5": [
        [1, 2, 5, 4, 0, 3, 6],
        [5, 0, 3, 2, 6, 1, 4],
        [5, 4, 3, 1, 0, 6, 2],
        [5, 6, 4, 0, 2, 3, 1],
        [3, 1, 0, 4, 6, 2, 5],
        [6, 1, 5, 4, 2, 3, 0],
        [1, 0, 2, 4, 3, 6, 5],
    ],
    "7-6": [
        [2, 5, 6, 3, 4, 0, 1],
        [3, 4, 1, 0, 5, 6, 2],
        [2, 6, 3, 0, 5, 4, 1],
        [1, 3, 0, 6, 4, 2, 5],
        [6, 2, 5, 1, 0, 3, 4],
        [6, 2, 1, 4, 3, 5, 0],
        [6, 1, 2, 4, 0, 5, 3],
    ],
    "7-7": [
        [0, 5, 1, 3, 6, 4, 2],
        [4, 0, 2, 6, 5, 1, 3],
        [6, 1, 3, 4, 0, 5, 2],
        [6, 2, 4, 5, 3, 0, 1],
        [4, 1, 3, 2, 0, 6, 5],
        [1, 3, 5, 6, 2, 0, 4],
        [1, 0, 4, 3, 5, 2, 6],
    ],
    "7-8": [
        [3, 6, 0, 5, 2, 4, 1],
        [3, 1, 6, 5, 2, 0, 4],
        [0, 4, 2, 6, 5, 3, 1],
        [6, 5, 1, 3, 4, 0, 2],
        [0, 1, 4, 2, 5, 6, 3],
        [4, 1, 3, 6, 0, 5, 2],
        [0, 4, 1, 5, 2, 3, 6],
    ],
}
