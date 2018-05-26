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
    # X_train, Y_train = X_[10:], Y[10:]
    # X_test, Y_test = X_[:10], Y[:10]
    # X_train, Y_train = X_, Y
    # X_test, Y_test = X_, Y
    clf = tree.DecisionTreeClassifier(min_samples_leaf=0.1, criterion='entropy', min_samples_split=2)
    clf = clf.fit(X_train, Y_train)
    print(np.mean((clf.predict(X_test) - Y_test) ** 2))
    print(clf.score(X_test, Y_test))
    dot_data = tree.export_graphviz(clf, out_file=None)
    graph = graphviz.Source(dot_data)
    graph.render('tree')


def regression():
    # X_train, Y_train = X_[10:], Y[10:]
    # X_test, Y_test = X_[:10], Y[:10]
    # X_train, X_test = X_, X_
    # Y_train, Y_test = Y, Y
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
    # X_ = (X - X.min()) / (X.max() - X.min())
    # polynomial transform
    # degree = 1
    # for deg in range(2, degree+1):
    #     for feature in features:
    #         X_["{}^{}".format(feature, deg)] = X_[feature].apply(lambda a: pow(a, deg))
    #
    # X_['Ones'] = np.ones(len(df))
    #
    # print(X_.shape)
    # w = np.linalg.solve(np.dot(X_.T, X_), np.dot(X_.T, df[predict]))
    # Yhat = np.dot(X_, w)
    #
    # d1 = df[predict] - Yhat
    # d2 = df[predict] - df[predict].mean()
    # r2 = 1 - d1.dot(d1) / d2.dot(d2)
    # print(r2)

    X_train, X_test, Y_train, Y_test = train_test_split(X_, Y, test_size = 0.2, random_state = 0)
    print(X_train.shape)
    # decision_tree()
    # regression()
    # polyregr()
    # logitreg()

#
# def plot_features():
#     for feature in features:
#         plt.scatter(df[feature], df[predict])
#         plt.xlabel(feature)
#         plt.ylabel(predict)
#         plt.legend()
#         plt.show()

#plot_features()
# sns.pairplot(df, x_vars=['avg_naff', 'avgr', 'npstn', 'nlo', 'npo', 'minr', 'difficulty'], y_vars=['ac', 'nfrozen', 'bs', 'size', 'nsols'])
# sns.set(style='ticks', color_codes=True)
# sns.pairplot(df, x_vars=['size'], y_vars=['difficulty'], markers="+", plot_kws=dict(s=50, edgecolor="g", linewidth=1))
# pal = sns.color_palette("GnBu_d")
# sns.violinplot(x='size', y='difficulty', data=df[['size', 'difficulty']], palette=pal, inner='points')
# plt.show()