import pathlib
from pprint import pprint

import numpy as np
import pandas as pd
import tensorflow as tf

tf.random.set_random_seed(123)

# training stage

DATASET_REAL_POSITIVE = pathlib.Path('pos_real.csv')
DATASET_REAL_NEGATIVE = pathlib.Path('neg_real.csv')
DATASET_AUGMENTED_POSITIVE = pathlib.Path('pos_aug.csv')
DATASET_AUGMENTED_NEGATIVE = pathlib.Path('neg_aug.csv')

EVALUATION = pathlib.Path('candidates.csv')


def _drop_columns(columns, dataset):
    for col in columns:
        if col in dataset.columns:
            dataset = dataset.drop(columns=col)


def _read_file(filename, _class: int = 0):
    dataset = pd.read_csv(filename)
    _drop_columns([
        'NAME_CANDIDATE',
        'NUM_LITERAL',
        'CON_LITERAL',
        'TYPED_ELEMENTS_COUPLING',
        'TYPED_ELEMENTS_COHESION',
        'CON_LOC',
        'CON_ASSERT'
    ], dataset)
    classes = np.repeat(a=_class, repeats=dataset.shape[0])
    classes = pd.DataFrame(data=classes, columns=['CLASSES'])
    return dataset, classes, dataset.columns


real_pos, y_real_pos, column_names = _read_file(DATASET_REAL_POSITIVE, _class=1)
real_neg, y_real_neg, _ = _read_file(DATASET_REAL_NEGATIVE, _class=0)
aug_pos, y_aug_pos, _ = _read_file(DATASET_AUGMENTED_POSITIVE, _class=1)
aug_neg, y_aug_neg, _ = _read_file(DATASET_AUGMENTED_NEGATIVE, _class=0)

train_dataset = pd.concat((real_pos, real_neg, aug_pos, aug_neg))
train_classes = pd.concat((y_real_pos, y_real_neg, y_aug_pos, y_aug_neg))
train_df = pd.concat((train_dataset, train_classes), axis=1)

print(train_df)
features = train_dataset.columns

feature_columns = []
for feature_name in column_names:
    feature_columns.append(
        tf.feature_column.numeric_column(feature_name, dtype=tf.float64)
    )

NUM_SAMPLES = len(list(train_dataset.iterrows()))

train_input_fn = tf.estimator.inputs.pandas_input_fn(
    train_dataset,
    train_classes['CLASSES'],
    shuffle=True,
    batch_size=128,
    num_epochs=2
)

est = tf.estimator.BoostedTreesRegressor(feature_columns, n_batches_per_layer=1)
est.train(train_input_fn, max_steps=100)

# evaluate on test data

eval_dataset = pd.read_csv(EVALUATION)
predict_keys = eval_dataset['NAME_CANDIDATE'].values
print(eval_dataset.head())

_drop_columns([
    'NAME_CANDIDATE',
    'NUM_LITERAL',
    'CON_LITERAL',
    'TYPED_ELEMENTS_COUPLING',
    'TYPED_ELEMENTS_COHESION',
    'CON_LOC',
    'CON_ASSERT'
], eval_dataset)

eval_input_fn = tf.estimator.inputs.pandas_input_fn(
    eval_dataset, shuffle=False, batch_size=1
)
proba = list(est.predict(eval_input_fn))#, predict_keys=predict_keys))
pprint(list(zip(proba, predict_keys)))

# saving stage

feature_spec = {
    col: tf.convert_to_tensor(
        train_dataset[col].values, dtype=tf.float64, name=col
    )
    for col in column_names
}

serving_fn = tf.estimator.export.build_raw_serving_input_receiver_fn(
    feature_spec
)

est.export_savedmodel(
    '/home/snyss/Prog/mm/diploma/main/apeman/Model/model_tf_base',
    serving_fn,
    strip_default_attrs=True
)
