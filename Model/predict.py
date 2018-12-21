from model import Dataset, Classifier, GRADIENT_BOOSTING

if __name__ == "__main__":
    classifier = Classifier(GRADIENT_BOOSTING)
    classifier.deserialize_model("model.out")
    
    predict_dataset = Dataset()
    predict_dataset.append_candidates("candidates.csv")
    proba = classifier.predict_proba(predict_dataset.features)
    predict_dataset.store_proba("probabilities.csv", proba)
