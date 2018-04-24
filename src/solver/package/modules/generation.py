import random
import math
import numpy
from itertools import product


def generate_random_instance(nbactors):
    pref = [0]*nbactors
    for actor in range(nbactors):
        pref[actor] = random.sample(range(1,nbactors+1),nbactors)
    return pref


def generate_solvable(n):
    """Generate random solvable instance of `n` actors.

    Args:
        n: number of actors wanted.

    Returns: 
        (pref list): lists of preferences for each actor.
    """
    # `objects` contains the mapping object_idx -> object_value
    objects = list(range(n))
    random.shuffle(objects)
    # declare alloc model
    # for each actor, choose the index of the alloc in the associated pref list.
    # chosen object for the index will be objects[alloc_indices[a]]
    alloc_indices = {a: None for a in range(n)}
    # choose one possible allocation from these ranges
    possible_idx_ext = range(n-1)
    possible_idx_int = range(n-2)

    for a in range(n):
        pool = possible_idx_ext if a == 0 or a == n-1 else possible_idx_int
        alloc_indices[a] = random.choice(pool)

    def get_object_pool(a):
        if a == 0:
            neighbors = [a+1]
        elif a == n-1:
            neighbors = [a-1]
        else:
            neighbors = [a-1, a+1]

        object_pool = list(range(n))
        for neighbor in neighbors: 
            if neighbor in object_pool:
                object_pool.remove(neighbor)
        object_pool.remove(a)
        return object_pool

    pref_lists = list()

    # populate prefs based on allocation
    for a in range(n):
        object_pool_top = get_object_pool(a)
        random.shuffle(object_pool_top)
        object_pool_bottom = [i for i in range(n) \
            if i != a and i not in object_pool_top]
        random.shuffle(object_pool_bottom)

        prefs = list()
        for pref_idx in range(alloc_indices[a]):
            prefs.append(objects[object_pool_top.pop()])

        prefs.append(objects[a])

        object_pool_bottom += object_pool_top
        for pref_idx in range(alloc_indices[a]+1, n):
            prefs.append(objects[object_pool_bottom.pop()])


        pref_lists.append(prefs)

    return pref_lists