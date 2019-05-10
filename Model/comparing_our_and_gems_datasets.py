import os

import numpy as np
import pandas as pd
from matplotlib import pyplot as plt


def _drop_columns(columns, dataset):
    for col in columns:
        if col in dataset.columns:
            dataset = dataset.drop(columns=col)
    return dataset


def read_csv(path):
    assert os.path.exists(path)
    df = pd.read_csv(path)

    df = _drop_columns(["NAME_CANDIDATE", "NUM_LOCAL", "CON_LOCAL"], df)
    return df


def one_plot(ax: plt.Axes, our_df, gems_df, column, title):
    result, counts = np.unique(our_df[column].values, return_counts=True)
    ax.plot(result, counts / our_df.shape[0] * 100, linewidth=5, alpha=0.5)

    result, counts = np.unique(gems_df[column].values, return_counts=True)
    ax.plot(result, counts / gems_df.shape[0] * 100, linewidth=5, alpha=0.5)

    ax.set_title(title)
    ax.set_ylim(0, 100)
    ax.legend(["Apeman", "GEMS"])


def compare_datasets(
        our_pos: pd.DataFrame,
        our_neg: pd.DataFrame,
        gems_pos: pd.DataFrame,
        gems_neg: pd.DataFrame
):
    for column in our_pos:
        fig, (ax0, ax1) = plt.subplots(1, 2, figsize=(7, 3))

        one_plot(ax0, our_pos, gems_pos, column, f"positive examples")
        one_plot(ax1, our_neg, gems_neg, column, f"negative examples")
        ax0.set_ylabel("Все примеры, %")
        ax0.set_xlabel(f"{column}")
        ax1.set_xlabel(f"{column}")
        plt.savefig(f"./plots/{column}.png", bbox_inches='tight')
        # plt.show()


if __name__ == "__main__":
    # OUR_BASE = "../../../train_dataset4/"
    OUR_BASE = "../GemsDataset/subjects/"
    # OUR_POS = OUR_BASE + "pos_filtered.csv"
    OUR_POS = OUR_BASE + "dataset_pos_overall.csv"
    # OUR_NEG = OUR_BASE + "neg_filtered.csv"
    OUR_NEG = OUR_BASE + "dataset_neg_overall.csv"
    GEMS_POS = "../GemsDataset/augmented_set/con_pos404.csv"
    GEMS_NEG = "../GemsDataset/augmented_set/con_neg404.csv"

    our_pos = read_csv(OUR_POS)
    our_neg = read_csv(OUR_NEG)
    gems_pos = read_csv(GEMS_POS)
    gems_neg = read_csv(GEMS_NEG)

    compare_datasets(our_pos, our_neg, gems_pos, gems_neg)
