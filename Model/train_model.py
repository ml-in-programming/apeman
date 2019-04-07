import pathlib
import pandas as pd
import numpy as np
from sklearn.ensemble import RandomForestClassifier
from sklearn.tree import DecisionTreeClassifier

from model import Classifier, Dataset, GRADIENT_BOOSTING


DATASET_REAL_NEGATIVE = pathlib.Path('../GemsDataset/real_set/con_neg404.csv')
DATASET_REAL_POSITIVE = pathlib.Path('../GemsDataset/real_set/con_pos404.csv')
DATASET_AUGMENTED_POSITIVE = pathlib.Path('../GemsDataset/augmented_set/con_pos404.csv')
DATASET_AUGMENTED_NEGATIVE = pathlib.Path('../GemsDataset/augmented_set/con_neg404.csv')


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
    classes = pd.DataFrame(data=classes, columns=['CLASSES'], dtype=np.float64)

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
    train_df: pd.DataFrame = pd.concat((train_dataset, train_classes), axis=1)

    eval_dataset = pd.concat((eval_real_pos, eval_real_neg, eval_aug_pos, eval_aug_neg))
    eval_classes = pd.concat((y_eval_real_pos, y_eval_real_neg, y_eval_aug_pos, y_eval_aug_neg))
    eval_df = pd.concat((eval_dataset, eval_classes), axis=1)

    train_df.reset_index()
    eval_df.reset_index()

    return train_dataset, train_classes, eval_dataset, eval_classes, column_names, train_df, eval_df


def predict_something():
    from sklearn.ensemble import GradientBoostingClassifier
    from sklearn.metrics import accuracy_score, average_precision_score, roc_auc_score
    from sklearn.model_selection import cross_val_score
    from sklearn.linear_model import SGDClassifier

    for coef in np.arange(0.1, 1, 0.1):
        train_ds, train_cls, eval_ds, eval_cls, _, train_df, eval_df = make_train_and_eval_ds(coef)

        eval_ds = eval_ds.fillna(0.0)
        train_ds = train_ds.fillna(0.0)

        is_label = train_df['CLASSES'] == 1
        eval_df1 = eval_df[eval_df['CLASSES'] == 1]
        # print(eval_df1.mean())
        # print(eval_df1.max())
        # print(eval_df1.min())

        eval_df0 = eval_df[eval_df['CLASSES'] == 0]
        # print(pd.concat((eval_df0.mean(), eval_df1.mean()), axis=1))
        # print(pd.concat((eval_df0.median(), eval_df1.median()), axis=1))
        # print(pd.concat((eval_df0.max(), eval_df1.max()), axis=1))
        # print(eval_df0.min())

        # train_df = train_df[is_label]
        # print(train_df.mean())
        # print(train_df.max())
        # print(train_df.min())

        # print(eval_ds.isna().any())
        # print(eval_cls.isna().any())
        # print(all([not lol for lol in eval_ds.isin([np.nan, np.inf, -np.inf]).any(1)]))

        features = range(46)#np.random.choice(range(46), 10, replace=False)
        train_ds = train_ds.values[:, features]
        eval_ds = eval_ds.values[:, features]

        est = DecisionTreeClassifier()#SGDClassifier(loss="modified_huber")#max_depth=4)
        est = est.fit(train_ds, train_cls.values)
        eval_pred = est.predict(eval_ds)
        eval_proba = est.predict_proba(eval_ds)

        print(f"Coef: {coef}")
        print(f"mean acc: {est.score(eval_ds, eval_cls.values)}")
        print(f"cross_val {cross_val_score(est, train_ds, train_cls.values)}")
        print(f'accuracy: {accuracy_score(eval_cls.values, eval_pred)}')
        print(f'aver_precision: {average_precision_score(eval_cls.values, eval_pred)}')
        print(f'roc_auc: {roc_auc_score(eval_cls.values, eval_proba[:, 1])}')
        print()


if __name__ == "__main__":
    classifier = Classifier(GRADIENT_BOOSTING)
    fit_dataset = Dataset()

    fit_dataset.append_positive(str(DATASET_REAL_POSITIVE))
    fit_dataset.append_negative(str(DATASET_REAL_NEGATIVE))
    fit_dataset.append_positive(str(DATASET_AUGMENTED_POSITIVE))
    fit_dataset.append_negative(str(DATASET_AUGMENTED_NEGATIVE))

    classifier.fit(fit_dataset.features, fit_dataset.classes_of_classifier)

    with open(DATASET_REAL_POSITIVE) as f:
        headers = f.readline().split(',')
        print(len(headers))
        print(len(classifier.model.feature_importances_))
        import pprint
        pprint.pprint(sorted(zip(headers, classifier.model.feature_importances_), key=lambda n: -n[1]))
    classifier.serialize_model()
    #
    # predict_something()
