import pickle
from datetime import datetime
from pprint import pprint

import pandas as pd
import numpy as np
import tensorflow as tf

from imblearn.under_sampling import NearMiss, RandomUnderSampler, \
    ClusterCentroids, InstanceHardnessThreshold
from sklearn.base import OutlierMixin
from sklearn.ensemble import IsolationForest
from sklearn.neighbors import LocalOutlierFactor

import logging
logging.getLogger("tensorflow").setLevel(logging.ERROR)

results = []
sampler_str = ''
out_str = ''
perm_str = ''


def main():
    samplers = [
        None,
        InstanceHardnessThreshold(sampling_strategy='majority', random_state=123, n_jobs=-1),
        NearMiss(version=1, sampling_strategy='majority', random_state=123, n_jobs=-1),
        NearMiss(version=3, sampling_strategy='majority', random_state=123, n_jobs=-1),
        RandomUnderSampler(sampling_strategy='majority', random_state=123)
    ]

    outliers = [
        None,
        IsolationForest(random_state=123, behaviour='new', contamination=0.1),
        LocalOutlierFactor(n_neighbors=27, contamination=0.1)
    ]

    for sampler in samplers:
        for out in outliers:

            global sampler_str, out_str, perm_str
            sampler_str = sampler.__class__.__name__
            out_str = out.__class__.__name__

            print(f"\nsampler={sampler_str}, outlier={out_str}")

            X, y, X_valid, y_valid = Dataset.read_all()
            X, y, X_valid, y_valid = Modification.apply_standartization(X, y, X_valid, y_valid)

            print(X.shape)

            if out is not None:
                X, y = Modification.apply_outliers(X, y, out)
                print(X.shape)

            if sampler is None:
                weights, weight_valid = Modification.make_weights_column(X, y, X_valid, y_valid)
            else:
                weights, weight_valid = None, None
                X, y = Modification.apply_samplers(X, y, sampler)
                if "Instance" in sampler_str:
                    X, y = Modification.apply_samplers(
                        X, y, RandomUnderSampler(sampling_strategy='majority', random_state=123)
                    )

            print("0st perm:")
            perm_str = "0st"
            est = Model.train(X, y, X_valid, y_valid, weights, weight_valid)

            print("1st perm:")
            perm_str = "1st"

            X, y, X_valid, y_valid = Modification.apply_permutation(
                X, y, X_valid, y_valid, est, sampler.__class__.__name__,
                weight_valid
            )
            est = Model.train(X, y, X_valid, y_valid, weights, weight_valid)

            print("2nd perm:")
            perm_str = "2nd"
            X, y, X_valid, y_valid = Modification.apply_permutation(
                X, y, X_valid, y_valid, est, sampler.__class__.__name__,
                weight_valid
            )
            Model.train(X, y, X_valid, y_valid, weights, weight_valid)

    print(results)
    analyze_results()


def analyze_results():
    results_ = [(out, samp, perm, r['accuracy'], r['precision'], r['recall'],
                (2 * r['precision'] * r['recall'] / (r['precision'] + r['recall'])))
                for out, samp, perm, r in results]
    pprint(results_)
    with open(f"results_{datetime.utcnow()}.pickle", 'wb') as f:
        pickle.dump(results, f)


class Dataset:
    @classmethod
    def read_all(cls):
        BASE = "../../../train_dataset4/"

        pos = cls.read_dataframe(BASE + "pos_train.csv", 1)
        neg = cls.read_dataframe(BASE + "neg_train.csv", 0)
        train: pd.DataFrame = pd.concat((pos, neg), axis=0).reset_index(drop=True)
        y = train.pop("CLASSES")
        X = train

        pos_valid = cls.read_dataframe(BASE + "pos_validate.csv", 1)
        neg_valid = cls.read_dataframe(BASE + "neg_validate.csv", 0)
        valid = pd.concat((pos_valid, neg_valid), axis=0).reset_index(drop=True)
        y_valid = valid.pop("CLASSES")
        X_valid = valid

        return X, y, X_valid, y_valid

    @classmethod
    def read_dataframe(cls, path, klass: int) -> pd.DataFrame:
        X = pd.read_csv(path)
        X = cls.drop(X, [
            "NAME_CANDIDATE",
            "CON_LITERAL",
            "NUM_LITERAL"
        ])
        y = pd.Series(np.repeat(klass, repeats=X.shape[0]), name='CLASSES')
        return pd.concat((X, y), axis=1)

    @classmethod
    def drop(cls, X: pd.DataFrame, columns: list) -> pd.DataFrame:
        return X.drop(columns=list(set(columns) & set(X.columns)))


class Modification:
    @classmethod
    def apply_samplers(cls, X, y, sampler):
        features = X.columns
        X, y = sampler.fit_resample(X, y)
        X = pd.DataFrame(X, columns=features)
        y = pd.Series(y)
        print(X.shape)
        return X, y

    @classmethod
    def apply_permutation(cls, X, y, X_valid, y_valid, est,
                          samplers_names=None, weights_valid=None):
        def add_random_feature():
            X['RANDOM'] = np.random.random(X.shape[0])
            X_valid['RANDOM'] = np.random.random(X_valid.shape[0])
            return X, X_valid

        def metric():
            """TensorFlow estimator accuracy."""
            eval_input_fn = Model.make_input_fn(
                X_valid, y_valid, shuffle=False, n_epochs=1, weights=weights_valid
            )
            metrics = est.evaluate(input_fn=eval_input_fn)
            return metrics['accuracy']

        def permutation_importances():
            """source: http://explained.ai/rf-importance/index.html"""
            baseline = metric()
            imp = []
            for col in X.columns:
                save = X_valid[col].copy()
                X_valid[col] = np.random.permutation(X_valid[col])
                m = metric()
                X_valid[col] = save
                imp.append(baseline - m)

            return np.array(imp)

        X, X_valid = add_random_feature()
        importances = permutation_importances()
        df_imp = pd.Series(importances, index=X.columns)

        sorted_ix = df_imp.sort_values().index
        bad_index = [i for i, s in enumerate(sorted_ix) if s == 'RANDOM'][0]
        bad_features = sorted_ix[:bad_index].to_list()

        # ax = df_imp[sorted_ix].plot(kind='barh', color='b', figsize=(10, 6))
        # ax.grid(False, axis='y')
        # ax.set_title(f'Perm. imp., samplers: {samplers_names}')
        # plt.savefig(f"./permutation_importance/plot_{samplers_names}.png")
        # plt.show()
        # print('showed')

        exclude_columns = bad_features + ['RANDOM']
        print('columns to exclude:')
        print(exclude_columns)
        X = Dataset.drop(X, exclude_columns)
        X_valid = Dataset.drop(X_valid, exclude_columns)
        return X, y, X_valid, y_valid

    @classmethod
    def apply_standartization(cls, X, y, X_valid, y_valid):
        for col in X:
            mean = X[col].mean()
            std = X[col].std()
            X[col] = (X[col] - mean) / std
            X_valid[col] = (X_valid[col] - mean) / std
        return X, y, X_valid, y_valid

    @classmethod
    def apply_outliers(cls, X: pd.DataFrame, y, outlier: OutlierMixin):
        positives = y.where(y == 1).dropna(how='all').index
        x_pos = X.iloc[positives]
        y_pos = y.iloc[positives]

        negs = y.where(y == 0).dropna(how='all').index
        x_neg = X.iloc[negs]
        y_neg = y.iloc[negs]

        outliers = pd.Series(outlier.fit_predict(x_pos.values))
        indices = outliers.where(outliers==-1).dropna(how='all').index
        x_pos = x_pos.drop(index=indices)
        y_pos = y_pos.drop(labels=indices)

        X = pd.concat((x_pos, x_neg)).reset_index(drop=True)
        y = pd.concat((y_pos, y_neg)).reset_index(drop=True)
        return X, y

    @classmethod
    def make_weights_column(cls, X: pd.DataFrame, y: pd.Series, X_valid, y_valid):
        positives = y.where(y == 1).dropna(how='all').shape[0]
        negatives = y.where(y == 0).dropna(how='all').shape[0]
        weight = negatives / positives

        weight_data = pd.Series(np.ones(X.shape[0]))
        pos_indices = y.where(y == 1).dropna(how='all').index
        weight_data[pos_indices] = weight

        weight_valid = pd.Series(np.ones(X_valid.shape[0]))
        # indices_valid = y_valid.where(y_valid==1).dropna(how='all').index
        # weight_valid[indices_valid] = weight
        return weight_data, weight_valid


class Model:
    @classmethod
    def make_normalizer_fn(cls, X, feature):
        mean = X[feature].mean()
        std = X[feature].std()

        def normalize_fn(col):
            return (col - mean) / std
        return normalize_fn

    @classmethod
    def train(cls, X: pd.DataFrame, y: pd.Series, X_valid: pd.DataFrame, y_valid: pd.Series,
              weights=None, weigths_valid=None):

        feature_columns = []
        for feature_name in X.columns:
            feature_columns.append(tf.feature_column.numeric_column(
                feature_name,
                dtype=tf.float64,
                normalizer_fn=cls.make_normalizer_fn(X, feature_name)
            ))

        if weights is not None:
            w_col = tf.feature_column.numeric_column('weight', (X.shape[0],))
        else:
            w_col = None

        train_input_fn = cls.make_input_fn(X, y, weights=weights)

        est = tf.estimator.BoostedTreesClassifier(
            feature_columns, n_batches_per_layer=1, max_depth=2, n_trees=90, weight_column=w_col)
        est.train(train_input_fn)

        eval_input_fn = cls.make_input_fn(X_valid, y_valid, shuffle=False, n_epochs=1,
                                          weights=weigths_valid)
        global results, perm_str, out_str, sampler_str
        results.append((out_str, sampler_str, perm_str, est.evaluate(input_fn=eval_input_fn)))
        pprint(results[-1][-1])
        return est

    @classmethod
    def make_input_fn(cls, X: pd.DataFrame, y: pd.Series, n_epochs=None, shuffle=True, weights=None):
        NUM_EXAMPLES = 128

        def input_fn():
            x = X.to_dict(orient='list')
            if weights is not None:
                x.update({'weight': weights})

            dataset = tf.data.Dataset.from_tensor_slices((x, y))
            if shuffle:
              dataset = dataset.shuffle(X.shape[0], seed=123)
            dataset = (dataset.repeat(n_epochs).batch(NUM_EXAMPLES))
            return dataset
        return input_fn


if __name__ == "__main__":
    main()
