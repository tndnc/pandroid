from pylab import figure, imshow, savefig, grid
import seaborn as sns
import numpy as np


def pprint_instance(instance, allocation=None, indices=None):
    if not allocation: allocation = dict()
    n = len(instance)
    
    # print("\n")
    buffer = ""
    for i in range(n):
        for a in range(n):
            obj = instance[a][i]
            if indices:
                buffer += "{}\t".format(strobj_fromid(instance, a, i, indices[a]))
            else:
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


def strobj_fromid(instance, a, x, ind):
    return "{}{}".format("*" if ind == x else "", instance[a][x])


def draw_hotspots(hotspots, instance, alloc, figname):
    if not alloc: alloc = dict()
    f = figure()
    # im = imshow(data, interpolation='nearest', cmap='Reds')
    annotations = [[strobj(instance, a, x, alloc) for a in range(len(instance))] for x in range(len(instance))]
    annotations = np.asarray(annotations)
    ax = sns.heatmap(hotspots, annot=annotations, fmt="s")
    # grid(True)
    savefig('../output/{}.png'.format(figname))


def show_average_heatmap(hotspots_list, instance):
    f = figure()
    annotations = np.asarray([[instance[a][x] for a in range(len(instance))] \
        for x in range(len(instance))])
    average_heatmap = np.mean(hotspots_list, axis=0)
    ax = sns.heatmap(average_heatmap, annot=annotations, fmt="d")
    f.show()