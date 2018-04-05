
def export_toXML(**kwargs):
	try:
		instance = kwargs['instance']
	except:
		# TODO
		raise

	s = "<level name=\"grid_{}\" size=\"{}\">".format(1, len(instance))
	for i in range(len(instance)):
		s += "<preferences line=\"{}\" order=\"{}\"/>".format(i+1, "".join(map(str, instance[i])))
	s += "</level>"
	return s


def export_toLATEX(**kwargs):
	"""Export the instance to a latex table.

	Args:
		instance: the instance to print.
		allocation: one solution for the instance.
		niter: number of iterations for the given allocation.
		h: value of difficulty heuristic.

	Optional: 
		env (string): latex environment, "figure" or "subfigure"
		heatmap: hotspots from the backtrack algorithm 
			(for the given solution)
		color (string): color for the allocation.

	Returns:
		(string)
	"""
	try:
		instance = kwargs['instance']
		allocation = kwargs['allocation']
		niter = kwargs['niter']
		h = kwargs['h']
	except KeyError:
		# TODO
		raise

	env = kwargs.get('env', "figure")
	color = kwargs.get('color', "yellow")
	n = len(instance)
	cbstr_start = "\\bb{"
	cbstr_end = "}"

	s = "\\begin{{{}}}[h]{}\n".format(env, "" if env == "figure" else "{0.45\\textwidth}")
	s += "\t\\centering\n"
	s += "\t\\begin{{tabular}}{{|{}|}}\n".format(" ".join(['c']*n))
	s += "\t\t\\hline\n"
	for j in range(n):
		objs = ["{}{}{}".format(
			cbstr_start if allocation.get(i) == instance[i][j] else "", 
			instance[i][j],
			cbstr_end if allocation.get(i) == instance[i][j] else "")
		for i in range(n)]

		objs_list = " & ".join(objs)
		s += "\t\t{} \\\\\n".format(objs_list)
	s += "\t\t\\hline\n"
	s += "\t\\end{tabular}\n"
	s += "\t\\caption{{Nombre d'étapes: {}, $h = {}$}}\n".format(niter, h)
	s += "\\end{{{}}}".format(env)

	return s