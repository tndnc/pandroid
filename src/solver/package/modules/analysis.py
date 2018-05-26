import networkx as nx
import numpy as np
import matplotlib.pyplot as plt
from copy import copy
from random import shuffle, choice
from itertools import combinations, permutations
from package.modules.utils import *
import math

def compute_metadata(instance, solutions, wpos, stats, 
	MIN_FREEZE_PROPORTION=1):
	
	number_of_frozen_variables = 0
	proportions = dict()
	for var_idx in range(len(solutions[0])):
		
		proportions[var_idx] = dict()
		for solution in solutions:
			proportions[var_idx][solution[var_idx]] = proportions[var_idx].get(
				solution[var_idx], 0) + 1

		for k in proportions[var_idx].keys():
			proportions[var_idx][k] /= len(solutions)
			if proportions[var_idx][k] >= MIN_FREEZE_PROPORTION:
				number_of_frozen_variables += 1

	mean_regret_wpos = 0
	min_regret = float('inf')
	min_ext_r = float('inf')
	min_ext_l = float('inf')
	for solution in solutions:
		r, ext1, ext2 = regret(instance, solution)
		if r < min_regret: 
			min_regret = r
		if ext1 < min_ext_l:
			min_ext_l = ext1
		if ext2 < min_ext_r:
			min_ext_r = ext2
		if solution in wpos: 
			mean_regret_wpos += r
	mean_regret_wpos /= len(wpos)

	# Compute average number of possible agents for variables
	average_nb_of_possible_position = 0
	for obj in range(len(instance)):
		n = 0
		for a in range(len(instance)):
			if a == 0: neighbors = [a+1]
			elif a == len(instance)-1: neighbors = [a-1]
			else: neighbors = [a-1, a+1]

			if all(instance[neighbor][0] != obj for neighbor in neighbors):
				n += 1
		average_nb_of_possible_position += n
	average_nb_of_possible_position /= len(instance)

	H = nx.Graph()
	acs = list()
	for solution in solutions:
		# print("Estimating basin for {}".format(solution))
		G, ac = estimate_basin(instance, Solution(solution, instance))
		H = nx.compose(H, G)
		acs.append(ac)

	avg_ac = np.array(acs).mean()

	return {
		'avg_naff': sum(s['niter'] for s in stats) / len(stats),
		'npo': len(wpos),
		'minr': min_regret,
		'avgr': mean_regret_wpos,
		'minr_extr': min_ext_r,
		'minr_extl': min_ext_l,
		'npstn': average_nb_of_possible_position,
		'nsols': len(solutions),
		'nlo': get_nlo(instance),
		'nfrozen': number_of_frozen_variables,
		'bs': len(H),
		'ac': avg_ac
	}


def adaptative_walk(instance, start_solution, n=50):
	pairs = list(combinations(range(len(instance)), 2))
	current_solution = start_solution
	current_cost = start_solution.cost
	walk_sols = list()
	walk_costs = list()

	for _ in range(n):
		walk_sols.append(current_solution)
		walk_costs.append(current_cost)
		neighbor_solutions = [swap(current_solution, pair) for pair in pairs]
		neighbor_solutions = list(filter(lambda s: s.cost >= current_cost and s not in walk_sols, neighbor_solutions))

		if len(neighbor_solutions) == 0:
			break

		current_solution = choice(neighbor_solutions)
		current_cost = current_solution.cost

		if current_cost == len(instance):
			walk_sols.append(current_solution)
			walk_costs.append(current_cost)
			break

	return walk_sols, walk_costs


def estimate_basin(instance, optimum, n=50):
	basin = nx.Graph()
	all_costs = list()

	for _ in range(n):
		walk_sols, walk_costs = adaptative_walk(instance, optimum)
		# basin.update(walk_sols)
		for i in range(len(walk_sols)-1):
			basin.add_edge(walk_sols[i], walk_sols[i+1])
		all_costs.append(walk_costs)

	acs = list()
	for walkc in all_costs:
		costs = np.array(walkc)
		f_mean = costs.mean()
		f_std = costs.std()

		cs = np.array([((walkc[i] - f_mean)*(walkc[i+1]-f_mean)) for i in range(len(walkc)-1)])
		esp_corr = cs.mean()
		ac = esp_corr / pow(f_std, 2)

		acs.append(ac)

	return basin, np.array(acs).mean()



def estimate_nlo_rndm(instance):
	lo = set()
	pairs = list(combinations(range(len(instance)), 2))
	n = int(math.factorial(len(instance))*1)
	print("Sampling {} solutions".format(n))

	for _ in range(n):
		solution = list(range(len(instance)))
		shuffle(solution)
		sol_cost = cost(instance, solution)
		m = 0
		for pair in pairs:
			neighbor_solution = swap(solution, pair)
			neighbor_cost = cost(instance, neighbor_solution)
			if neighbor_cost < sol_cost: m += 1
		if m == 0: lo.add(tuple(solution))

	return len(lo)


def get_nlo(instance):
	lo = list()
	all_sols = permutations(range(len(instance)))
	pairs = list(combinations(range(len(instance)), 2))

	for sol in all_sols:
		solution = list(sol)
		sol_cost = cost(instance, solution)
		m = 0
		for pair in pairs:
			neighbor_solution = swap(solution, pair)
			neighbor_cost = cost(instance, neighbor_solution)
			if neighbor_cost < sol_cost: m += 1
		if m == 0: lo.append(solution)

	return len(lo)


def regret(instance, solution):
	s = 0
	for a in range(len(instance)):
		s += instance[a].index(solution[a])
	ext1 = instance[0].index(solution[0])
	ext2 = instance[-1].index(solution[len(instance)-1])
	return s, ext1, ext2


def get_optima_graph(instance, solutions):
	G = nx.DiGraph()
	solutions = list(map(lambda x: Solution(x, instance), solutions))
	all_pairs = combinations(solutions, 2)

	basin = get_attraction_basin(solutions)
	for b in basin:
		G.add_edge(b[0], b[1], d=1)

	return G


def cost(instance, solution):
	"""Return the cost associated with a solution,
	i.e. the number of agents that exhibit jealousy.
	"""
	def get_neighbors_idx(a):
		if a == 0: return [a+1]
		elif a == len(solution)-1: return [a-1]
		else: return [a-1, a+1]

	ret = 0
	for a1 in range(len(solution)):
		jealous = False
		for a2 in get_neighbors_idx(a1):
			r1a1 = instance[a1].index(solution[a1])
			r2a1 = instance[a1].index(solution[a2])
			r1a2 = instance[a2].index(solution[a1])
			r2a2 = instance[a2].index(solution[a2])
			if r1a1 > r2a1 or r2a2 > r1a2:
				jealous = True
		if jealous: ret += 1

	# print(pprint_instance(instance, allocation=solution))
	# print("Cost: {}".format(ret))

	return ret


def distance(s1, s2):
	"""Distance between solutions with the swap operator"""
	if s1 == s2: return 0
	return sum([1 for k in s1.keys() if s1[k] != s2[k]]) - 1


def swap(solution, pair):
	"""Swaps the value of allocated object in a solution
	between a pair of agents"""
	solution_ = copy(solution)
	value_a1 = solution[pair[0]]
	value_a2 = solution[pair[1]]
	solution_[pair[0]] = value_a2
	solution_[pair[1]] = value_a1
	return solution_


def get_attraction_basin(solutions):
	B = set()
	S = set(solutions)
	S_ = set()
	all_pairs = list(combinations(range(len(solutions[0])), 2))

	# import ipdb; ipdb.set_trace()

	while len(S) != 0:
		# pair = sample(range(len(solution)), 2)
		origin = S.pop()
		S_.add(origin)

		for pair in all_pairs:
			candidate = swap(origin, pair)
			if candidate.cost >= origin.cost and candidate.cost < len(solutions[0]):
				B.add((candidate, origin))
				# B.update(get_attraction_basin(candidate))
				if candidate not in S_: S.add(candidate)

	return B


def show_optima_graph(G):
	colormap = {
		0: '#af0428', 1: '#1A237E', 2: '#3949AB', 3: '#7986CB',
		4: '#C5CAE9', 5: '#E8EAF6', 6: '#7986CB', 7: '#9FA8DA',
		8: '#C5CAE9', 9: '#E8EAF6'
	}
	colors = [colormap[node.cost] for node in G]

	# for pair in all_pairs:
	# 	d = distance(pair[0], pair[1])
	# 	if d <= 2:
	# 		G.add_edge(pair[0], pair[1], d=d)

	pos = nx.spring_layout(G, iterations=150, k=0.5)
	# pos = nx.circular_layout(G)
	# pos = nx.random_layout(G)
	# nx.draw_networkx_edge_labels(G, pos=pos)
	# nx.draw_networkx_labels(G, pos=pos)
	nx.draw(G, pos=pos, node_size=40, node_color=colors, edge_color="#BDBDBD")
	plt.show()


class Solution:

	def __init__(self, sol_dict, instance):
		self.sol = sol_dict
		self.instance = instance
		self.cost_ = cost(self.instance, self.sol)
		self.valid_ = True

	def __repr__(self):
		# return "c={} {}".format(self.cost_, tuple(self.sol.values()))
		return str(self.sol.values())

	def __hash__(self):
		# h = hash(tuple(self.sol.values()))
		# return h
		return hash(frozenset(self.sol.items()))

	def __copy__(self):
		return Solution(copy(self.sol), self.instance)

	def __getitem__(self, k):
		return self.sol[k]

	def __setitem__(self, k, v):
		self.sol[k] = v
		self.valid_ = False

	def keys(self):
		return self.sol.keys()

	def __len__(self):
		return len(self.sol)

	def __eq__(self, other):
		return other.sol == self.sol

	def __ne__(self, other):
		return other.sol != self.sol

	@property
	def cost(self):
		if not self.valid_: 
			self.cost_ = cost(self.instance, self.sol)
		return self.cost_
		