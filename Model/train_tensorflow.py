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
        'NAME_CANDIDATE',
        'NUM_LITERAL',
        'CON_LITERAL'
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
    aug_pos, y_aug_pos, eval_aug_pos, y_eval_aug_pos, _ = _read_train_file(
        DATASET_AUGMENTED_POSITIVE, _class=1, coef=coef)
    aug_neg, y_aug_neg, eval_aug_neg, y_eval_aug_neg, _ = _read_train_file(
        DATASET_AUGMENTED_NEGATIVE, _class=0, coef=coef)

    train_dataset = pd.concat((real_pos, real_neg, aug_pos, aug_neg))
    train_classes = pd.concat((y_real_pos, y_real_neg, y_aug_pos, y_aug_neg))
    train_df = pd.concat((train_dataset, train_classes), axis=1)

    eval_dataset = pd.concat((eval_real_pos, eval_real_neg, eval_aug_pos, eval_aug_neg))
    eval_classes = pd.concat((y_eval_real_pos, y_eval_real_neg, y_eval_aug_pos, y_eval_aug_neg))
    eval_df = pd.concat((eval_dataset, eval_classes), axis=1)

    return train_dataset, train_classes, eval_dataset, eval_classes, column_names


def train_model(coef=1.0):

    train_dataset, train_classes, eval_dataset, eval_classes, column_names = \
        make_train_and_eval_ds(coef)

    feature_columns = []
    for feature_name in column_names:
        feature_columns.append(
            tf.feature_column.numeric_column(feature_name, dtype=tf.float64)
        )

    train_input_fn = tf.estimator.inputs.pandas_input_fn(
        train_dataset,
        train_classes['CLASSES'],
        shuffle=True,
        batch_size=128,
        num_epochs=2
    )

    est = tf.estimator.BoostedTreesClassifier(feature_columns, n_batches_per_layer=1)
    est.train(train_input_fn, max_steps=90)

    eval_dataset = eval_dataset.fillna(value=0)
    eval_input_fn = tf.estimator.inputs.pandas_input_fn(
        eval_dataset, eval_classes['CLASSES'], shuffle=False, batch_size=1
    )
    proba = None#est.evaluate(eval_input_fn)
    print(f'Coef : {coef}')
    pprint(list(proba.items()))

    return est, proba


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

    DATASET_REAL_POSITIVE = pathlib.Path('pos_real.csv')
    DATASET_REAL_NEGATIVE = pathlib.Path('neg_real.csv')
    DATASET_AUGMENTED_POSITIVE = pathlib.Path('pos_aug.csv')
    DATASET_AUGMENTED_NEGATIVE = pathlib.Path('neg_aug.csv')
    EVALUATION = pathlib.Path('candidates.csv')

    DATASET_REAL_POSITIVE = pathlib.Path(
        '/home/snyss/Prog/mm/diploma/main/apeman/GemsDataset/real_set/con_pos404.csv')
    DATASET_REAL_NEGATIVE = pathlib.Path(
        '/home/snyss/Prog/mm/diploma/main/apeman/GemsDataset/real_set/con_neg404.csv')
    DATASET_AUGMENTED_POSITIVE = pathlib.Path(
        '/home/snyss/Prog/mm/diploma/main/apeman/GemsDataset/augmented_set/con_pos404.csv')
    DATASET_AUGMENTED_NEGATIVE = pathlib.Path(
        '/home/snyss/Prog/mm/diploma/main/apeman/GemsDataset/augmented_set/con_neg404.csv')

    train_ds, train_classes, eval_ds, eval_classes, column_names = make_train_and_eval_ds(coef=1.0)
    est, _ = train_model(coef=1.0)
    predict_test_candidates(est)
    save_model(est, train_ds, column_names)

    # test_results = []
    # for i in np.arange(0.1, 1, 0.1):
        # train_ds, train_classes, eval_ds, eval_classes, column_names = \
        #     make_train_and_eval_ds(coef=i)
        # _, test_proba = train_model(coef=i)
        # test_results.append(test_proba)
        # predict_test_candidates(est)
