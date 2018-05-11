if __name__ == '__main__':
	import sys

	if len(sys.argv) > 1 and sys.argv[1] == '-g':
		from package import app
		sys.exit(app.run(sys.argv))
	else:
		from package.modules.tests import *
		instance = test_instances["3-1"]
		solutions = asp_solve(instance=instance)
		wpos, stats = compute_optimal_solutions(instance)
		
		metadata = compute_metadata(instance, solutions, wpos, stats)
		print(metadata)