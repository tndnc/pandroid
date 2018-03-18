from pylab import figure, imshow, savefig, grid
import seaborn as sns
import numpy as np


def pprint_instance(instance, allocation=None):
    if not allocation: allocation = dict()
    n = len(instance)
    
    # print("\n")
    buffer = ""
    for i in range(n):
        for a in range(n):
            obj = instance[a][i]
            buffer += "{}{}\t".format("*" if obj == allocation.get(a) else " ", obj)
        buffer += "\n"
        # print(buffer)

    return buffer


def save_to_file(n_agents, values):
    with open('../output/alloc_{}.txt'.format(n_agents), 'w') as f:

        for v in values:
            f.write("Resolution time: {}microseconds.\nNumber of iterations: {}.\n".format(v[2], v[3]))
            f.write("Solution {}found.\n".format("" if v[1] else "not "))
            f.write(pprint_instance(v[0], v[1]))
            f.write("\n")


def strobj(instance, a, x, alloc):
    return "{}{}".format("*" if instance[a][x] == alloc.get(a) else "",
        instance[a][x])


def draw_hotspots(hotspots, instance, alloc, figname):
    if not alloc: alloc = dict()
    f = figure()
    # im = imshow(data, interpolation='nearest', cmap='Reds')
    annotations = [[strobj(instance, a, x, alloc) for a in range(len(instance))] for x in range(len(instance))]
    annotations = np.asarray(annotations)
    ax = sns.heatmap(hotspots, annot=annotations, fmt="s")
    # grid(True)
    savefig('../output/{}.png'.format(figname))