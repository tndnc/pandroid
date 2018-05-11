import pandas as pd
import seaborn as sns
import numpy as np
import matplotlib.pyplot as plt

df = pd.read_csv('stats.csv')
sizes = {s: 0 for s in (3, 4, 5, 6, 7)}
for x in df.iloc[:,1]:
    size = int(x[0])
    sizes[size] += 1

df['Size'] = df['Instance id'].apply(lambda a: int(a[0]))

plot_by = 'Frozen Variables'
predict = 'Difficulty'
features = ['Size', 'wpos', 'allsols', 'frozen',
            'attr_bas_size', 'avg_iter', 'avg_regret', 'ext_regret', 'avg_regret_wpos', 'avg_nb_pos']

# df.sort_values(by=plot_by, inplace=True)
X = df[features].copy()
# normalize
X_ = (X - X.mean()) / X.std()
# X_ = (X - X.min()) / (X.max() - X.min())
# polynomial transform
degree = 5
for deg in range(2, degree+1):
    for feature in features:
        X_["{}^{}".format(feature, deg)] = X_[feature].apply(lambda a: pow(a, deg))

X_['Ones'] = np.ones(len(df))

print(X_.shape)
w = np.linalg.solve(np.dot(X_.T, X_), np.dot(X_.T, df[predict]))
Yhat = np.dot(X_, w)

d1 = df[predict] - Yhat
d2 = df[predict] - df[predict].mean()
r2 = 1 - d1.dot(d1) / d2.dot(d2)
print(r2)


def plot_features():
    for feature in features:
        plt.scatter(df[feature], df[predict])
        plt.xlabel(feature)
        plt.ylabel(predict)
        plt.legend()
        plt.show()

#plot_features()
sns.pairplot(X, x_vars=['avg_iter', 'avg_regret', 'ext_regret', 'Size', 'wpos'], y_vars=['avg_regret_wpos', 'avg_nb_pos', 'attr_bas_size'])
plt.show()