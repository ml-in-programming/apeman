import pandas as pd
import numpy as np
from imblearn.under_sampling import TomekLinks, ClusterCentroids, NearMiss, \
    InstanceHardnessThreshold
from imblearn.pipeline import


def filter_zero_con_loc(df: pd.DataFrame):
    return df.where(df["CON_LOC"] != 0).dropna(how='all')


def filter_negative_similar_examples(pos: pd.DataFrame, neg: pd.DataFrame):
    for pos_row in pos.iterrows():
        pos_row = pos_row[1].drop("NAME_CANDIDATE")
        without_names = neg.drop('NAME_CANDIDATE', axis=1)
        differences = without_names.apply(lambda row: row - pos_row, axis=1).abs()
        most_similar_row = differences.argsort()[:1]
        similar_index = most_similar_row.index()
        neg = neg.drop(index=similar_index)

    return pos, neg


if __name__ == "__main__":
    BASE = "../../../train_dataset4/"
    OUR_POS = BASE + "dataset_pos_overall.csv"
    OUR_NEG = BASE + "dataset_neg_overall.csv"
    pos = pd.read_csv(OUR_POS)
    neg = pd.read_csv(OUR_NEG)

    # pos = filter_zero_con_loc(pos)
    # neg = filter_zero_con_loc(neg)
    # pos, neg = filter_negative_similar_examples(pos, neg)

    # pd.concat((pos, neg), axis=)
    #
    # for sample in (
    #         TomekLinks(sampling_strategy='majority'),
    #         TomekLinks(sampling_strategy='majority'),
    #         TomekLinks(sampling_strategy='majority'),
    #         AllKNN(sampling_strategy='majority'),
    #         CondensedNearestNeighbour(sampling_strategy='majority'),
            # InstanceHardnessThreshold(sampling_strategy='majority'),
            # NearMiss(version=1, sampling_strategy='majority')):
        #
        #
        # sample.fit_resample(pos, np.repeat(1, pos.shape[0]))

    pos.to_csv(BASE + "pos_filtered.csv", index=False)
    neg.to_csv(BASE + "neg_filtered.csv", index=False)
