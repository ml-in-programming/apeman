import pathlib
from pprint import pprint

import numpy as np
import pandas as pd
import tensorflow as tf
import matplotlib.pyplot as plt

bad_features = []


def _drop_columns(columns, dataset):
    for col in columns:
        if col in dataset.columns:
            dataset = dataset.drop(columns=col)
    return dataset


def _read_train_file(filename, _class: int = 0, offset=1.0, limit=0.1):
    dataset = pd.read_csv(filename)
    dataset = _drop_columns([
        'NAME_CANDIDATE',
        'NUM_LITERAL',
        'CON_LITERAL',
        'CON_ASSERT',
        'VAR_ACCESS_COUPLING',
        'VAR_ACCESS_COHESION_2',
        'TYPE_ACCESS_COUPLING_2',
        'TYPE_ACCESS_COUPLING',
        'PACKAGE_COHESION_2',
        'NUM_SWITCH',
        'NUM_CONDITIONAL',
        'INVOCATION_COUPLING',
        'FIELD_ACCESS_COUPLING_2',
        'FIELD_ACCESS_COUPLING',
        "FIELD_ACCESS_COHESION",
        "VAR_ACCESS_COUPLING_2",
        "VAR_ACCESS_COHESION",
        "TYPED_ELEMENTS_COUPLING",
        "PACKAGE_COUPLING_2",
        "NUM_LOCAL",
        "NUM_INVOCATION",
        "NUM_IF",
        "NUM_FIELD_ACCESS",
        "NUM_ASSIGN",
        "INVOCATION_COHESION",
        "FIELD_ACCESS_COHESION_2",
        "CON_SWITCH",
        "CON_CONDITIONAL",
        "TYPE_ACCESS_COHESION_2",
        "TYPE_ACCESS_COHESION",
        "TYPED_ELEMENTS_COHESION",
        "PACKAGE_COUPLING",
        "NUM_LOC",
        "NUM_COMMENTS",
        "LOC_RATIO",
        "CON_IF",


        # 'NAME_CANDIDATE',
        # 'NUM_LITERAL',
        # 'CON_LITERAL',
        # 'CON_ASSERT',
        # # 'CON_SWITCH',
        # # 'CON_INVOCATION',
        # # 'CON_CONDITIONAL',
        # 'CON_TYPE_ACCESS',
        # 'CON_VAR_ACCESS',
        # 'CON_PACKAGE',
        # 'CON_TYPED_ELEMENTS',
        # 'CON_FIELD_ACCESS',
        # 'CON_ASSIGN',
        # # 'NUM_SWITCH',
        # # 'NUM_CONDITIONAL',
        # 'NUM_PACKAGE',
        # 'NUM_COMMENTS',
        # 'NUM_IF',
        # 'NUM_LOCAL',
        # 'NUM_LOC',
        # 'NUM_TYPED_ELEMENTS',
        # # 'NUM_TYPE_ACCESS',
        # 'NUM_INVOCATION',
        # 'NUM_ASSIGN',
        # 'LOC_RATIO',
        # 'CON_IF',
        # # 'FIELD_ACCESS_COUPLING',
        # # 'FIELD_ACCESS_COHESION',
        # # 'FIELD_ACCESS_COUPLING_2',
        # 'FIELD_ACCESS_COHESION_2',
        # 'VAR_ACCESS_COUPLING',
        # 'VAR_ACCESS_COUPLING_2',
        # 'VAR_ACCESS_COHESION',
        # 'VAR_ACCESS_COHESION_2',
        # # 'TYPE_ACCESS_COUPLING',
        # 'TYPE_ACCESS_COUPLING_2',
        # 'TYPE_ACCESS_COHESION',
        # 'TYPE_ACCESS_COHESION_2',
        # 'TYPED_ELEMENTS_COUPLING',
        # 'TYPED_ELEMENTS_COHESION',
        # # 'PACKAGE_COUPLING',
        # 'PACKAGE_COUPLING_2',
        # 'PACKAGE_COHESION',
        # 'PACKAGE_COHESION_2',
        # # 'INVOCATION_COUPLING',
        # 'INVOCATION_COHESION',
        # 'MEAN_NESTING_DEPTH',
        # 'METHOD_MEAN_NESTING_DEPTH',
    ], dataset)
    classes = np.repeat(a=_class, repeats=dataset.shape[0])
    classes = pd.DataFrame(data=classes, columns=['CLASSES'])

    limit = int(dataset.shape[0] * limit)
    offset = int(dataset.shape[0] * offset)

    train_ds = pd.concat(
        (dataset.iloc[:offset],
         dataset.iloc[offset+limit:]))
    train_classes = pd.concat(
        (classes.iloc[:offset],
         classes.iloc[offset+limit:]))

    eval_ds = dataset.iloc[:offset+limit]
    eval_classes = classes.iloc[:offset+limit]

    return train_ds, train_classes, eval_ds, eval_classes, dataset.columns


def make_train_and_eval_ds(offset: float, limit: float):
    real_pos, y_real_pos, eval_real_pos, y_eval_real_pos, column_names = _read_train_file(
        DATASET_REAL_POSITIVE,
        _class=1,
        offset=offset,
        limit=limit)

    real_neg, y_real_neg, eval_real_neg, y_eval_real_neg, _ = _read_train_file(
        DATASET_REAL_NEGATIVE,
        _class=0,
        offset=offset,
        limit=limit)

    train_dataset = pd.concat((real_pos, real_neg))
    train_classes = pd.concat((y_real_pos, y_real_neg))
    train_df = pd.concat((train_dataset, train_classes), axis=1)

    eval_dataset = pd.concat((eval_real_pos, eval_real_neg))
    eval_classes = pd.concat((y_eval_real_pos, y_eval_real_neg))
    eval_df = pd.concat((eval_dataset, eval_classes), axis=1)

    return train_dataset, train_classes, eval_dataset, eval_classes, column_names


def permutation_importances(est, X_eval, y_eval, metric, features):
    """source: http://explained.ai/rf-importance/index.html
    A similar approach can be done during training. See "Drop-column importance"
    in the above article."""

    baseline = metric(est, X_eval, y_eval)
    imp = []
    for col in features:
        print(col)
        save = X_eval[col].copy()
        X_eval[col] = np.random.permutation(X_eval[col])
        m = metric(est, X_eval, y_eval)
        X_eval[col] = save
        imp.append(baseline - m)
    return np.array(imp)


def accuracy_metric(est, X, y):
    """TensorFlow estimator accuracy."""
    eval_input_fn = tf.estimator.inputs.pandas_input_fn(X, y, shuffle=False, num_epochs=1)
    metrics = est.evaluate(input_fn=eval_input_fn)
    pprint(metrics)
    return metrics['accuracy']#['average_loss']


def importances_main(features, X_eval, y_eval, est):
    importances = permutation_importances(est, X_eval, y_eval['CLASSES'], accuracy_metric, features)
    df_imp = pd.Series(importances, index=features)

    global bad_features

    sorted_ix = df_imp.sort_values().index
    bad_index = [i for i, s in enumerate(sorted_ix) if s == 'RANDOM'][0]
    bad_features += sorted_ix[:bad_index].to_list()

    ax = df_imp[sorted_ix].plot(kind='barh', color='b', figsize=(10, 6))
    ax.grid(False, axis='y')
    ax.set_title('Permutation feature importance')
    plt.show()
    print('showed')


def make_input_fn(X, y, n_epochs=None, shuffle=True):
    NUM_EXAMPLES = 128

    def input_fn():
        dataset = tf.data.Dataset.from_tensor_slices((X.to_dict(orient='list'), y))
        if shuffle:
          dataset = dataset.shuffle(X.shape[0])
        dataset = (dataset.repeat(n_epochs).batch(NUM_EXAMPLES))
        return dataset
    return input_fn


def train_model():

    feature_columns = []
    for feature_name in column_names:
        feature_columns.append(
            tf.feature_column.numeric_column(feature_name, dtype=tf.float64))

    # train_input_fn = tf.estimator.inputs.pandas_input_fn(
    #     train_ds,
    #     train_classes['CLASSES'],
    #     shuffle=True,
    #     batch_size=128,
    #     num_epochs=20
    # )
    # eval_input_fn = tf.estimator.inputs.pandas_input_fn(
    #     eval_ds, eval_classes['CLASSES'], shuffle=False, batch_size=1)
    #
    train_input_fn = make_input_fn(train_ds, train_classes['CLASSES'])
    eval_input_fn = make_input_fn(eval_ds, eval_classes['CLASSES'], shuffle=False, n_epochs=1)

    est = tf.estimator.BoostedTreesClassifier(#BoostedTreesRegressor(
        feature_columns, n_batches_per_layer=1, max_depth=2, n_trees=90)
    est.train(, train_input_fn,

    pprint(est.evaluate(input_fn=eval_input_fn))
    # lol = list(est.predict(tf.estimator.inputs.pandas_input_fn(eval_ds, shuffle=False, batch_size=1)))
    # pprint(est.experimental_feature_importances())
    # pprint(est.evaluate(input_fn=train_input_fn))
    return est


def predict_test_candidates(est):
    eval_dataset = pd.read_csv(EVALUATION)
    predict_keys = eval_dataset['NAME_CANDIDATE'].values
    print(eval_dataset.head())

    _drop_columns([
        'NAME_CANDIDATE',
        'NUM_LITERAL',
        'CON_LITERAL'
    ], eval_dataset)

    eval_input_fn = tf.estimator.inputs.pandas_input_fn(
        eval_dataset, shuffle=False, batch_size=1
    )
    proba = list(est.predict(eval_input_fn))
    pprint(list(zip(proba, predict_keys)))


def save_model(est, train_dataset, column_names):
    feature_spec = {
        column: tf.convert_to_tensor(
            train_dataset[column].values, dtype=tf.float64, name=column
        ) for column in column_names
    }

    serving_fn = tf.estimator.export.build_raw_serving_input_receiver_fn(
        feature_spec
    )

    est.export_savedmodel(
        '/home/snyss/Prog/mm/diploma/main/apeman/Model/model_tf_base',
        serving_fn,
        strip_default_attrs=True
    )


def add_random_feature():
    train_ds['RANDOM'] = np.random.random(train_ds.shape[0])
    eval_ds['RANDOM'] = np.random.random(eval_ds.shape[0])
    return train_ds, eval_ds


if __name__ == "__main__":

    tf.random.set_random_seed(124)

    EVALUATION = pathlib.Path('candidates.csv')

    base_path = "./"  #'../../../train_dataset4/'

    DATASET_REAL_POSITIVE = pathlib.Path(base_path + "pos_filtered.csv")
    DATASET_REAL_NEGATIVE = pathlib.Path(base_path + "neg_filtered.csv")

    lol = ['CON_ASSIGN', 'CON_CONDITIONAL', 'CON_FIELD_ACCESS', 'CON_IF',
       'CON_INVOCATION', 'CON_LOC', 'CON_LOCAL', 'CON_PACKAGE',
       'CON_SWITCH', 'CON_TYPED_ELEMENTS', 'CON_TYPE_ACCESS',
       'FIELD_ACCESS_COHESION', 'FIELD_ACCESS_COHESION_2',
       'FIELD_ACCESS_COUPLING', 'FIELD_ACCESS_COUPLING_2',
       'INVOCATION_COHESION', 'INVOCATION_COUPLING', 'LOC_RATIO',
       'MEAN_NESTING_DEPTH', 'METHOD_MEAN_NESTING_DEPTH', 'NUM_ASSIGN',
       'NUM_COMMENTS', 'NUM_CONDITIONAL', 'NUM_FIELD_ACCESS', 'NUM_IF',
       'NUM_INVOCATION', 'NUM_LOC', 'NUM_LOCAL', 'NUM_PACKAGE',
       'NUM_SWITCH', 'NUM_TYPED_ELEMENTS', 'NUM_TYPE_ACCESS',
       'NUM_VAR_ACCESS', 'PACKAGE_COHESION', 'PACKAGE_COHESION_2',
       'PACKAGE_COUPLING', 'PACKAGE_COUPLING_2', 'RANDOM',
       'TYPED_ELEMENTS_COHESION', 'TYPED_ELEMENTS_COUPLING',
       'TYPE_ACCESS_COHESION', 'TYPE_ACCESS_COHESION_2',
       'TYPE_ACCESS_COUPLING', 'TYPE_ACCESS_COUPLING_2',
       'VAR_ACCESS_COHESION', 'VAR_ACCESS_COHESION_2',
       'VAR_ACCESS_COUPLING', 'VAR_ACCESS_COUPLING_2']
    counts = ['1', '2', '2', '4',
       '2', '2', '2', '3', '2', '4', '3', '2', '3', '1', '1', '3', '2',
       '4', '2', '3', '4', '3', '2', '2', '3', '4', '2', '5', '2', '2',
       '1', '1', '2', '3', '4', '2', '1', '5', '3', '2', '2', '2', '1',
       '1', '3', '1', '2', '3']

    heh = [['CON_ASSIGN', '3'],
       ['CON_CONDITIONAL', '2'],
       ['CON_FIELD_ACCESS', '3'],
       ['CON_LOC', '2'],
       ['CON_LOCAL', '2'],
       ['CON_SWITCH', '1'],
       ['CON_TYPED_ELEMENTS', '3'],
       ['CON_VAR_ACCESS', '3'],
       ['FIELD_ACCESS_COHESION', '2'],
       ['FIELD_ACCESS_COUPLING', '1'],
       ['FIELD_ACCESS_COUPLING_2', '1'],
       ['INVOCATION_COUPLING', '2'],
       ['MEAN_NESTING_DEPTH', '4'],
       ['NUM_CONDITIONAL', '1'],
       ['NUM_FIELD_ACCESS', '2'],
       ['NUM_LOC', '4'],
       ['NUM_PACKAGE', '3'],
       ['NUM_SWITCH', '1'],
       ['NUM_TYPED_ELEMENTS', '4'],
       ['NUM_TYPE_ACCESS', '1'],
       ['NUM_VAR_ACCESS', '2'],
       ['PACKAGE_COUPLING', '2'],
       ['PACKAGE_COUPLING_2', '4'],
       ['RANDOM', '5'],
       ['TYPED_ELEMENTS_COUPLING', '1'],
       ['TYPE_ACCESS_COHESION', '3'],
       ['TYPE_ACCESS_COHESION_2', '3'],
       ['TYPE_ACCESS_COUPLING', '2'],
       ['TYPE_ACCESS_COUPLING_2', '1'],
       ['VAR_ACCESS_COHESION', '1'],
       ['VAR_ACCESS_COHESION_2', '2'],
       ['VAR_ACCESS_COUPLING', '1'],
       ['VAR_ACCESS_COUPLING_2', '2']]

    # lol = np.reshape(lol, (len(lol), 1))
    # counts = np.reshape(counts, (len(counts), 1))
    # heh = np.concatenate((lol, counts), axis=1)

    # lol, counts = np.unique(bad_features, return_counts=True)
    pprint(sorted(heh, key=lambda x: x[1]))
    # plt.plot(lol, counts)
    # plt.show()

    value = False
    for offset in np.arange(0, 1, 0.2):
        train_ds, train_classes, eval_ds, eval_classes, column_names = make_train_and_eval_ds(
            offset=offset, limit=0.2)
        train_ds, eval_ds = add_random_feature()
        column_names = train_ds.columns
        est = train_model()
        importances_main(column_names, eval_ds, eval_classes, est)
        if value:
            break

    lol, counts = np.unique(bad_features, return_counts=True)
    lol = np.reshape(lol, (len(lol), 1))
    counts = np.reshape(counts, (len(counts), 1))
    heh = np.concatenate((lol, counts), axis=1)
    # pprint(heh)
    pprint(sorted(heh, key=lambda x: x[1]))
    plt.plot(lol, counts)
    plt.show()
    # predict_test_candidates(est)
    # save_model(est, train_ds, column_names)

    # test_results = []
    # for i in np.arange(0.1, 1, 0.1):
    #     train_ds, train_classes, eval_ds, eval_classes, column_names = \
    #         make_train_and_eval_ds(coef=i)
    #     _, test_proba = train_model(coef=i)
    #     test_results.append(test_proba)
    #     predict_test_candidates(est)
