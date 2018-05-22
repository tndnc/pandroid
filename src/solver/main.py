if __name__ == '__main__':
	import sys

	if len(sys.argv) > 1 and sys.argv[1] == '-g':
		from package import app
		sys.exit(app.run(sys.argv))
	else:
		from package.modules.tests import *
		from instances.inst import *
		# for instance_id in instances.keys():
		# 	instance = instances[instance_id]
		# 	solutions = asp_solve(instance=instance)
		# 	wpos, stats = compute_optimal_solutions(instance)

		# 	metadata = compute_metadata(instance, solutions, wpos, stats)
		# 	metadata['id'] = instance_id
		# 	print(metadata)
		instance = instances["5-4"]
		#print(get_nlo(instance))
		solutions = asp_solve(instance=instance)
		H = nx.Graph()
		for solution in solutions:
			print("Estimating basin for {}".format(solution))
			G = estimate_basin(instance, Solution(solution, instance))
			H = nx.compose(H, G)
		print(len(H))

		for node in H.nodes:
			if node.cost == 0: print(node)
		# H = estimate_basin(instance, Solution(solutions[0], instance))
		show_optima_graph(H)