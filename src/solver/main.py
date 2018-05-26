if __name__ == '__main__':
	import sys

	if len(sys.argv) > 1 and sys.argv[1] == '-g':
		from package import app
		sys.exit(app.run(sys.argv))
	else:
		from package.modules.tests import *
		from instances.inst import *

		features = ('id', 'npo', 'nsols', 'avg_naff', 'nfrozen', 'minr', 'avgr', 'minr_extr', 'minr_extl', 'npstn', 'nlo', 'bs', 'ac')
		f = open('stats2.csv', 'w')
		f.write(",".join(features) + "\n")
		f.flush()

		for instance_id in test_instances.keys():
			instance = instances[instance_id]
			solutions = asp_solve(instance=instance)
			wpos, stats = compute_optimal_solutions(instance)

			metadata = compute_metadata(instance, solutions, wpos, stats)
			metadata['id'] = instance_id
			print(metadata)
			f.write(",".join(map(str, [metadata[f] for f in features])) + "\n")
			f.flush()
		# instance = instances["7"]
		# #print(get_nlo(instance))
		# solutions = asp_solve(instance=instance)
		# H = nx.Graph()
		# for solution in solutions:
		# 	print("Estimating basin for {}".format(solution))
		# 	G, ac = estimate_basin(instance, Solution(solution, instance))
		# 	print("Autocorrelation in basin: {}".format(ac))
		# 	H = nx.compose(H, G)
		# print(len(H))