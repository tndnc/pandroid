import networkx as nx
import matplotlib.pyplot as plt
from copy import copy
from random import shuffle, sample
from itertools import combinations
from package.modules.utils import *

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

	# G = get_optima_graph(instance, solutions)

	mean_regret = 0
	mean_regret_wpos = 0
	ext_regret = 0
	min_regret = float('inf')
	min_ext_regret = float('inf')
	for solution in solutions:
		r, ext1, ext2 = regret(instance, solution)
		mean_regret += r
		if r < min_regret: min_regret = r
		if ext1 + ext2 < min_ext_regret: min_ext_regret = ext1 + ext2
		if solution in wpos: 
			ext_regret += ext1 + ext2
			mean_regret_wpos += r
	mean_regret /= len(solutions)
	mean_regret_wpos /= len(wpos)
	ext_regret /= len(wpos)

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

	return {
		'number_of_wpos': len(wpos),
		'number_of_solutions': len(solutions),
		# 'proportions': proportions,
		'average_niter': sum(s['niter'] for s in stats) / len(stats),
		'number_of_frozen_variables': number_of_frozen_variables,
		'attraction_basin_size': 0,
		# 'optima_graph': G,
		'mean_regret': mean_regret,
		'ext_regret': ext_regret,
		'mean_regret_wpos': mean_regret_wpos,
		'average_number_of_possible_position': average_nb_of_possible_position,
		'min_regret': min_regret,
		'min_ext_regret': min_ext_regret
	}


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
	nx.draw(G, pos=pos, node_size=50, node_color=colors, edge_color="#BDBDBD")
	plt.show()


class Solution:

	def __init__(self, sol_dict, instance):
		self.sol = sol_dict
		self.instance = instance
		self.cost_ = cost(self.instance, self.sol)
		self.valid_ = True

	def __repr__(self):
		# return "c={} {}".format(self.cost_, tuple(self.sol.values()))
		return str(self.cost)

	def __hash__(self):
		return hash(tuple(self.sol.values()))

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

	@property
	def cost(self):
		if not self.valid_: 
			self.cost_ = cost(self.instance, self.sol)
		return self.cost_
		