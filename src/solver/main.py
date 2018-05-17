if __name__ == '__main__':
	import sys

	if len(sys.argv) > 1 and sys.argv[1] == '-g':
		from package import app
		sys.exit(app.run(sys.argv))
	else:
		from package.modules.tests import *
		from instances.inst import *
		for instance_id in instances.keys():
			instance = instances[instance_id]
			solutions = asp_solve(instance=instance)
			wpos, stats = compute_optimal_solutions(instance)

			metadata = compute_metadata(instance, solutions, wpos, stats)
			metadata['id'] = instance_id
			print(metadata)
