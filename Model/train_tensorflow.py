import pathlib
from pprint import pprint

import numpy as np
import pandas as pd
import tensorflow as tf


def _drop_columns(columns, dataset):
    for col in columns:
        if col in dataset.columns:
            dataset = dataset.drop(columns=col)
    return dataset


def _read_train_file(filename, _class: int = 0, coef = 1.0):
    dataset = pd.read_csv(filename)
    dataset = _drop_columns([
        "NAME_CANDIDATE",
        "NUM_LITERAL",
        "CON_LITERAL",
        "CON_ASSERT",
        "VAR_ACCESS_COUPLING",
        "VAR_ACCESS_COHESION_2",
        "TYPE_ACCESS_COUPLING_2",
        "TYPE_ACCESS_COUPLING",
        "PACKAGE_COHESION_2",
        "NUM_SWITCH",
        "NUM_CONDITIONAL",
        "INVOCATION_COUPLING",
        "FIELD_ACCESS_COUPLING_2",
        "FIELD_ACCESS_COUPLING",
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

    num_head = int(dataset.shape[0] * coef)
    num_tail = dataset.shape[0] - num_head

    train_ds = dataset.head(n=num_head)
    train_classes = classes.head(n=num_head)

    eval_ds = dataset.tail(n=num_tail)
    eval_classes = classes.tail(n=num_tail)

    return train_ds, train_classes, eval_ds, eval_classes, dataset.columns


def make_train_and_eval_ds(coef: float):
    real_pos, y_real_pos, eval_real_pos, y_eval_real_pos, column_names = _read_train_file(
        DATASET_REAL_POSITIVE, _class=1, coef=coef)
    real_neg, y_real_neg, eval_real_neg, y_eval_real_neg, _ = _read_train_file(
        DATASET_REAL_NEGATIVE, _class=0, coef=coef)
    # aug_pos, y_aug_pos, eval_aug_pos, y_eval_aug_pos, _ = _read_train_file(
    #     DATASET_AUGMENTED_POSITIVE, _class=1, coef=coef)
    # aug_neg, y_aug_neg, eval_aug_neg, y_eval_aug_neg, _ = _read_train_file(
    #     DATASET_AUGMENTED_NEGATIVE, _class=0, coef=coef)

    train_dataset = pd.concat((real_pos, real_neg))#, aug_pos, aug_neg))
    train_classes = pd.concat((y_real_pos, y_real_neg))#, y_aug_pos, y_aug_neg))
    train_df = pd.concat((train_dataset, train_classes), axis=1)

    eval_dataset = pd.concat((eval_real_pos, eval_real_neg))#, eval_aug_pos, eval_aug_neg))
    eval_classes = pd.concat((y_eval_real_pos, y_eval_real_neg))#, y_eval_aug_pos, y_eval_aug_neg))
    eval_df = pd.concat((eval_dataset, eval_classes), axis=1)

    return train_dataset, train_classes, eval_dataset, eval_classes, column_names


def make_input_fn(X, y, n_epochs=None, shuffle=True):
    NUM_EXAMPLES = 128

    def input_fn():
        dataset = tf.data.Dataset.from_tensor_slices((X.to_dict(orient='list'), y))
        if shuffle:
          dataset = dataset.shuffle(X.shape[0])
        dataset = (dataset.repeat(n_epochs).batch(NUM_EXAMPLES))
        return dataset
    return input_fn


def train_model(coef=1.0):

    train_dataset, train_classes, eval_dataset, eval_classes, column_names = \
        make_train_and_eval_ds(coef)

    feature_columns = []
    for feature_name in column_names:
        feature_columns.append(
            tf.feature_column.numeric_column(feature_name, dtype=tf.float64)
        )

    # train_input_fn = tf.estimator.inputs.pandas_input_fn(
    #     train_dataset,
    #     train_classes['CLASSES'],
    #     shuffle=True,
    #     batch_size=128,
    #     num_epochs=2
    # )

    train_input_fn = make_input_fn(train_ds, train_classes['CLASSES'])

    est = tf.estimator.BoostedTreesRegressor(feature_columns, n_batches_per_layer=1, max_depth=2,
                                             n_trees=90)
    est.train(, train_input_fn,

    # eval_dataset = eval_dataset.fillna(value=0)
    # eval_input_fn = tf.estimator.inputs.pandas_input_fn(
    #     eval_dataset, eval_classes['CLASSES'], shuffle=False, batch_size=1
    # )
    # proba = est.evaluate(eval_input_fn)
    # print(f'Coef : {coef}')
    # pprint(list(proba.items()))

    return est, None#proba


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


if __name__ == "__main__":
    tf.random.set_random_seed(123)

    EVALUATION = pathlib.Path('candidates.csv')

    base_path = ""  #'../../../train_dataset4/'

    DATASET_REAL_POSITIVE = pathlib.Path(base_path + "pos_filtered.csv")
    DATASET_REAL_NEGATIVE = pathlib.Path(base_path + "neg_filtered.csv")

    train_ds, train_classes, eval_ds, eval_classes, column_names = make_train_and_eval_ds(coef=1.0)
    est, _ = train_model(coef=1.0)
    # predict_test_candidates(est)
    save_model(est, train_ds, column_names)

    # test_results = []
    # for i in np.arange(0.1, 1, 0.1):
    #     train_ds, train_classes, eval_ds, eval_classes, column_names = \
    #         make_train_and_eval_ds(coef=i)
    #     _, test_proba = train_model(coef=i)
    #     test_results.append(test_proba)
    #     predict_test_candidates(est)
