from pylab import figure, imshow, savefig, grid
import seaborn as sns
import numpy as np


def pprint_metadata(metadata):
    buffer = str()
    buffer += "Number of solutions: {}\n".format(metadata['nsols'])
    buffer += "Number of PO solutions: {}\n".format(metadata['npo'])
    buffer += "Average number of aff: {:.2f}\n".format(metadata['avg_naff'])
    buffer += "Number of frozen variables: {}\n".format(metadata['nfrozen'])
    buffer += "Estimated size of basin: {}\n".format(metadata['bs'])
    buffer += "Average regret in wpos: {:.2f}\n".format(metadata['avgr'])
    buffer += "Min regret ext left: {}\n".format(metadata['minr_extl'])
    buffer += "Min regret ext right: {}\n".format(metadata['minr_extr'])
    buffer += "Average number of possible obj position: {:.2f}\n".format(metadata['npstn'])
    buffer += "Min regret: {:.2f}\n".format(metadata['minr'])
    buffer += "Number of local optimums: {}\n".format(metadata['nlo'])
    buffer += "Autocorrelation: {:.2f}\n".format(metadata['ac'])

    return buffer


def pprint_instance(instance, allocation=None, indices=None):
    if not allocation: allocation = dict()
    n = len(instance)
    
    buffer = ""
    for i in range(n):
        for a in range(n):
            obj = instance[a][i]
            if indices:
                buffer += "{}\t".format(strobj_fromid(instance, a, i, indices[a]))
            else:
                buffer += "{}{}\t".format("*" if obj == allocation.get(a) else " ", obj)
        buffer += "\n"

    return buffer[:-2]


def save_to_file(n_agents, values):
    with open('../output/alloc_{}.txt'.format(n_agents), 'w') as f:

        for v in values:
            f.write("Resolution time: {}microseconds.\nNumber of iterations: {}.\n".format(v[2], v[3]))
            f.write("Solution {}found.\n".format("" if v[1] else "not "))
            f.write(pprint_instance(v[0], v[1]))
            f.write("\n")


def strobj_fromid(instance, a, x, ind):
    return "{}{}".format("*" if ind == x else "", instance[a][x])
