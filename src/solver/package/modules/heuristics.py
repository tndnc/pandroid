def h1(instance, allocation, niter):
	N = len(instance)
	S = N * sum([instance[a].index(allocation.get(a))+1 for a in range(N)])
	return N + S