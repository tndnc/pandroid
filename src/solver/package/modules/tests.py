from package.modules.generation import generate_random_instance, generate_solvable
from package.modules.export import export_toLATEX
from package.modules.heuristics import h1
from package.modules.backtrack_solve import (
    backtrack_from_agent, compute_optimal_solutions
)
from package.modules.utils import pprint_instance


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

def test_h1Distribution(number_of_tries=10000):

    print("Number of tries: {}".format(number_of_tries))
    number_of_agents = range(3,8)
    for n in number_of_agents:
        niter_means = list()
        r_means = list()
        nsols_mean = list()

        infeasibles = 0

        print("Solving for {} agents".format(n))
        for _ in range(number_of_tries):
            instance = generate_solvable(n)
            solutions, metadata = compute_optimal_solutions(instance)
            if len(solutions) > 0:
                h = h1(instance, solutions, metadata)
                niter_means.append(h[0])
                r_means.append(h[1])
                nsols_mean.append(h[2])
                # print(".", end="", flush=True)
            else:
                infeasibles += 1
                # print("x", end="", flush=True)

        print("Failed attempts: {}".format(infeasibles))
        print("Average number of iterations = {} (min: {}, max: {})"\
            .format(sum(niter_means)/len(niter_means), min(niter_means), max(niter_means)))
        print("Average regret = {} (min: {}, max: {})"\
            .format(sum(r_means)/len(r_means), min(r_means), max(r_means)))
        print("Average number of solutions = {} (min: {}, max: {})"\
            .format(sum(nsols_mean)/len(nsols_mean), min(nsols_mean), max(nsols_mean)))
        print()


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

