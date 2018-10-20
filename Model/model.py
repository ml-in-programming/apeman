import pickle
import pathlib

import pandas as pd
import numpy as np
from sklearn.ensemble import GradientBoostingClassifier


DATASET_REAL_POSITIVE = pathlib.Path('..', 'GemsDataset', 'real_set', 'con_pos404.csv')
DATASET_REAL_NEGATIVE = pathlib.Path('..', 'GemsDataset', 'real_set', 'con_neg404.csv')
DATASET_AUGMENTED_POSITIVE = pathlib.Path('..', 'GemsDataset', 'augmented_set', 'con_pos404.csv')
DATASET_AUGMENTED_NEGATIVE = pathlib.Path('..', 'GemsDataset', 'augmented_set', 'con_neg404.csv')

MODEL_STORE_FILE = "model.out"

GRADIENT_BOOSTING = 1


class Classifier:

    def __init__(self, classifier_type):

        self.type = classifier_type
        self.model = None
        self._choose_model()

    def fit(self, X: 'array-like', y: 'array-like'):
        self.model.fit(X, y)

    def predict_proba(self, X: 'array-like') -> 'array-like':
        return self.model.predict_proba(X)

    def serialize_model(self):
        with open(MODEL_STORE_FILE, 'wb') as f:
            pickle.dump(self.model, f)

    def deserialize_model(self, filename: str):
        with open(filename, 'rb') as f:
            self.model = pickle.load(f)

    def _choose_model(self):
        if self.type == GRADIENT_BOOSTING:
            self._choose_gradient_boosting()

    def _choose_gradient_boosting(self):
        self.model = GradientBoostingClassifier()


class Dataset:
    def __init__(self):
        self.features: np.ndarray = None
        self.classes_of_classifier: np.ndarray = None

    def append_positive(self, filename: str):
        self.append_to_class(filename, 1)

    def append_negative(self, filename: str):
        self.append_to_class(filename, 0)

    def append_to_class(self, filename: str, _class: int = 0):
        new_dataset = self._read(filename)
        classes = np.repeat(a=_class, repeats=new_dataset.shape[0])

        if self.features is None:
            self.features = new_dataset
            self.classes_of_classifier = classes
        else:
            self.features = np.concatenate((self.features, new_dataset))
            self.classes_of_classifier = np.append(self.classes_of_classifier,
                                                   classes)

    def _read(self, filename: str) -> np.ndarray:
        dataset = pd.read_csv(filename)
        dataset = dataset.drop(columns=['Name_Ext_Mtd'])
        return np.nan_to_num(dataset.values, copy=False)

    def store_proba(self, filename: str):
        pd.DataFrame(data=self.classes_of_classifier).to_csv(filename)


if __name__ == "__main__":
    classifier = Classifier(GRADIENT_BOOSTING)
    fit_dataset = Dataset()

    fit_dataset.append_positive(str(DATASET_REAL_POSITIVE))
    fit_dataset.append_negative(str(DATASET_REAL_NEGATIVE))
    fit_dataset.append_positive(str(DATASET_AUGMENTED_POSITIVE))
    fit_dataset.append_negative(str(DATASET_AUGMENTED_NEGATIVE))

    classifier.fit(fit_dataset.features,
                   fit_dataset.classes_of_classifier)

    classifier.serialize_model()
    exit(0)
