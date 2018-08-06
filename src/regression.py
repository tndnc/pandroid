import pandas as pd
import seaborn as sns
import numpy as np
import matplotlib.pyplot as plt
from sklearn import linear_model
from sklearn.model_selection import train_test_split
from sklearn import tree
from sklearn.preprocessing import PolynomialFeatures
from sklearn.pipeline import make_pipeline
import graphviz

def decision_tree():
    clf = tree.DecisionTreeClassifier(min_samples_leaf=0.1, criterion='entropy', min_samples_split=2)
    clf = clf.fit(X_train, Y_train)
    print(np.mean((clf.predict(X_test) - Y_test) ** 2))
    print(clf.score(X_test, Y_test))
    dot_data = tree.export_graphviz(clf, out_file=None)
    graph = graphviz.Source(dot_data)
    graph.render('tree')


def regression():
    # regr = linear_model.LinearRegression()
    regr = linear_model.Ridge(alpha=1)
    regr.fit(X_train, Y_train)
    print(regr.coef_)
    print(np.mean((regr.predict(X_test) - Y_test) ** 2))
    print(regr.score(X_test, Y_test))


def polyregr():
    degree = 5
    model = make_pipeline(PolynomialFeatures(degree), linear_model.Ridge())
    model.fit(X_train, Y_train)
    print(np.mean((model.predict(X_test) - Y_test) ** 2))
    print(model.score(X_test, Y_test))


def logitreg():
    regr = linear_model.LogisticRegression()
    regr.fit(X_train, Y_train)
    print(np.mean((regr.predict(X_test) - Y_test) ** 2))
    print(regr.score(X_test, Y_test))


if __name__ == "__main__":
    df = pd.read_csv('stats3.csv')
    sizes = {s: 0 for s in (3, 4, 5, 6, 7)}
    for x in df.iloc[:,1]:
        size = int(x[0])
        sizes[size] += 1

    df['size'] = df['inst'].apply(lambda a: int(a[0]))

    # plot_by = 'Frozen Variables'
    predict = 'difficulty'
    features = ['size', 'npo', 'nsols', 'avg_naff', 'nfrozen', 'minr', 'avgr',
                'minr_extr', 'minr_extl', 'npstn', 'nlo', 'bs', 'ac']

    # df.sort_values(by=plot_by, inplace=True)
    X = df[features].copy()
    Y = df[predict]
    # normalize
    X_ = (X - X.mean()) / X.std()

    X_train, X_test, Y_train, Y_test = train_test_split(X_, Y, test_size = 0.2, random_state = 0)
    print(X_train.shape)
    # decision_tree()
    # regression()
    # polyregr()
    # logitreg()