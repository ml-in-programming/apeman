import pathlib

from model import Classifier, Dataset, GRADIENT_BOOSTING


DATASET_REAL_POSITIVE = pathlib.Path('pos_real.csv')
DATASET_REAL_NEGATIVE = pathlib.Path('neg_real.csv')
DATASET_AUGMENTED_POSITIVE = pathlib.Path('pos_aug.csv')
DATASET_AUGMENTED_NEGATIVE = pathlib.Path('neg_aug.csv')


if __name__ == "__main__":
    classifier = Classifier(GRADIENT_BOOSTING)
    fit_dataset = Dataset()

    fit_dataset.append_positive(str(DATASET_REAL_POSITIVE))
    fit_dataset.append_negative(str(DATASET_REAL_NEGATIVE))
    fit_dataset.append_positive(str(DATASET_AUGMENTED_POSITIVE))
    fit_dataset.append_negative(str(DATASET_AUGMENTED_NEGATIVE))

    classifier.fit(fit_dataset.features,
                   fit_dataset.classes_of_classifier)
    with open(DATASET_REAL_POSITIVE) as f:
        headers = f.readline().split(',')
        print(len(headers))
        print(len(classifier.model.feature_importances_))
        import pprint
        pprint.pprint(sorted(zip(headers, classifier.model.feature_importances_), key=lambda n: -n[1]))
    classifier.serialize_model()
