import pathlib

from model import Classifier, Dataset, GRADIENT_BOOSTING


DATASET_REAL_POSITIVE = pathlib.Path('..', 'GemsDataset', 'real_set', 'con_pos404.csv')
DATASET_REAL_NEGATIVE = pathlib.Path('..', 'GemsDataset', 'real_set', 'con_neg404.csv')
DATASET_AUGMENTED_POSITIVE = pathlib.Path('..', 'GemsDataset', 'augmented_set', 'con_pos404.csv')
DATASET_AUGMENTED_NEGATIVE = pathlib.Path('..', 'GemsDataset', 'augmented_set', 'con_neg404.csv')


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
