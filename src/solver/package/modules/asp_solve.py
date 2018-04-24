from pyasp.asp import Gringo4Clasp, TermSet, Term


def encode_LEF(instance):
	n = len(instance)
	terms = TermSet()

	for agent in range(n):
		terms.update([
			Term('position', [agent, instance[agent][i], i]) for i in range(n)
		])
		terms.add(Term('object', [agent]))
		terms.add(Term('agent', [agent]))

	return terms


def solve(facts=None, instance=None):

	if not facts: facts = encode_LEF(instance)
	program = 'package/modules/LEF.lp'

	solver = Gringo4Clasp(gringo_options='', clasp_options='0')
	results = solver.run([program, facts.to_file()], collapseTerms=True, collapseAtoms=False)

	# to dicts
	allocations = list()
	for res in results:
		alloc = dict()
		for term in res:
			alloc[int(term.arg(0))] = int(term.arg(1))
		allocations.append(alloc)

	return allocations