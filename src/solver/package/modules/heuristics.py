def h1(instance, solutions, metadata):
	N = len(instance)

	regrets = [sum(instance[a].index(s.get(a)) for a in range(N)) for s in solutions]
	R_mean = sum(regrets) / len(regrets)

	niters = [m['niter'] for m in metadata]
	Niter_mean = sum(niters) / len(niters)
	
	return (Niter_mean, R_mean, len(solutions))