import pickle

import pandas as pd
import numpy as np
from sklearn.ensemble import GradientBoostingClassifier


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

    def append_candidates(self, filename: str):
        new_dataset = self._read(filename)
        self.features = new_dataset

    def append_proba(self, proba: 'array-like'):
        self.classes_of_classifier = proba

    def _read(self, filename: str) -> np.ndarray:
        dataset = pd.read_csv(filename)
        if 'NAME_CANDIDATE' in dataset.columns:
            dataset = dataset.drop(columns=['NAME_CANDIDATE'])
        return np.nan_to_num(dataset.values, copy=False)

    def store_proba(self, filename: str, proba: 'array-like'):
        is_cand_proba = proba[:, 1]
        pd.DataFrame(data=is_cand_proba).to_csv(filename, index=False)
