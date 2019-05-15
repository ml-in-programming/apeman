from typing import Tuple

import pandas as pd
import numpy as np


def parted_out_ds(pos: pd.DataFrame, neg: pd.DataFrame, size: int, name: str)\
        -> Tuple[pd.DataFrame, pd.DataFrame]:

    ind_pos = np.random.choice(np.arange(pos.shape[0]), size)
    ind_neg = np.random.choice(np.arange(neg.shape[0]), size)
    parted_pos: pd.DataFrame = pos.iloc[ind_pos]
    parted_neg: pd.DataFrame = neg.iloc[ind_neg]

    parted_pos.to_csv(BASE + f"pos_{name}.csv", index=False)
    parted_neg.to_csv(BASE + f"neg_{name}.csv", index=False)

    pos = pos.drop(index=ind_pos, axis=0).reset_index(drop=True)
    neg = neg.drop(index=ind_neg, axis=0).reset_index(drop=True)
    return pos, neg


if __name__ == "__main__":
    np.random.seed(123)

    BASE = "../../../train_dataset4/"
    pos = pd.read_csv(BASE + "dataset_pos_overall.csv")
    neg = pd.read_csv(BASE + "dataset_neg_overall.csv")

    print(pos.shape, neg.shape)
    pos, neg = parted_out_ds(pos, neg, 100, "validate")
    print(pos.shape, neg.shape)
    pos, neg = parted_out_ds(pos, neg, 50, "test")
    print(pos.shape, neg.shape)
    pos.to_csv(BASE + "pos_train.csv", index=False)
    neg.to_csv(BASE + "neg_train.csv", index=False)
