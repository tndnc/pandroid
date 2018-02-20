import random
import math
import numpy
from itertools import product


def generate_random_instance(nbactors):
    pref = [0]*nbactors
    for actor in range(nbactors):
        pref[actor] = random.sample(range(1,nbactors+1),nbactors)
    return pref


def generate_all_actors(obj,listactor):
    if len(listactor) == 0:
        return obj
    L = []
    for i in listactor:
        sublist = listactor.copy()
        sublist.remove(i)
        q = generate_all_actors(obj+[i],sublist)
        L += q
    return L


def splitList(nb,list):
    cpt = 0
    L = []
    for i in range(len(list)):
        if cpt % nb == 0:
            L.append(list[i:i+3])
        cpt += 1
    return L

def generate_all_pool(List,nbactors):
    L = []
    for roll in product(List, repeat=nbactors):
        L.append(roll)
    return L
    
    
def generate(nbactors):
    L = list(range(nbactors))
    L1 = generate_all_actors([],L)
    L2 = splitList(nbactors,L1)
    return generate_all_pool(L2,nbactors)



if __name__ == "__main__":
    print(len( generate(4)))






