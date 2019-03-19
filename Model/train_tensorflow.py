import pathlib

import numpy as np
import pandas as pd
import tensorflow as tf

tf.random.set_random_seed(123)

# training stage

DATASET_REAL_POSITIVE = pathlib.Path('pos_real.csv')
DATASET_REAL_NEGATIVE = pathlib.Path('neg_real.csv')
DATASET_AUGMENTED_POSITIVE = pathlib.Path('pos_aug.csv')
DATASET_AUGMENTED_NEGATIVE = pathlib.Path('neg_aug.csv')


def _read_file(filename, _class: int = 0):
    dataset = pd.read_csv(filename)
    drop_columns = [
        'NAME_CANDIDATE',
        'NUM_LITERAL',
        'CON_LITERAL',
        'TYPED_ELEMENTS_COUPLING',
        'TYPED_ELEMENTS_COHESION',
        'CON_LOC',
        'CON_ASSERT'
    ]

    for col in drop_columns:
        if col in dataset.columns:
            dataset = dataset.drop(columns=col)

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
    # if "CON_" in feature_name or "NUM_" in feature_name:
    #     feature_columns.append(
    #         tf.feature_column.numeric_column(feature_name, dtype=tf.int64)
    #     )
    # else:
    feature_columns.append(
        tf.feature_column.numeric_column(feature_name, dtype=tf.float64)
    )

NUM_EXAMPLES = len(train_classes)

train_input_fn = tf.estimator.inputs.pandas_input_fn(
    train_dataset, train_classes['CLASSES'], shuffle=True, batch_size=1
)

est = tf.estimator.BoostedTreesRegressor(feature_columns, n_batches_per_layer=1)
est.train(train_input_fn, max_steps=100)

# saving stage

# feature_spec = {
#     col: tf.FixedLenFeature([1], tf.int64, 0)
#     for col in column_names
#     if col.startswith('CON_') or col.startswith('NUM_')
# }
# feature_spec = {}
# feature_spec.update({
#     col: tf.FixedLenFeature([1], tf.float32, 0.0)
#     for col in column_names
#     if col not in feature_spec
# })

feature_spec = {
    col: tf.convert_to_tensor(train_dataset[col].values, dtype=tf.float64, name=col)
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
