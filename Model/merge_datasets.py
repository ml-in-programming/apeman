import os

import pandas as pd


def merge_inside_basepath(basepath: str):
    pos_df = None
    neg_df = None
    for i in range(0, 4000, 500):
        if os.path.exists(basepath + f"/dataset_neg_{i}.csv"):
            neg = pd.DataFrame.from_csv(basepath + f"/dataset_neg_{i}.csv")
            if neg_df is None:
                neg_df = neg
            else:
                neg_df = pd.concat((neg_df, neg))

        if os.path.exists(basepath + f"/dataset_pos_{i}.csv"):
            pos = pd.DataFrame.from_csv(basepath + f"/dataset_pos_{i}.csv")
            if pos_df is None:
                pos_df = pos
            else:
                pos_df = pd.concat((pos_df, pos))

    overall_pos = basepath + "/dataset_pos_overall.csv"
    overall_neg = basepath + "/dataset_neg_overall.csv"
    pos_df.to_csv(overall_pos)
    neg_df.to_csv(overall_neg)


def merge_overall(base, projects):
    pos_df = None
    neg_df = None

    for proj_dir in projects:
        neg_path = base + proj_dir + "/dataset_neg_overall.csv"
        pos_path = base + proj_dir + "/dataset_pos_overall.csv"

        neg = pd.DataFrame.from_csv(neg_path)
        if neg_df is None:
            neg_df = neg
        else:
            neg_df = pd.concat((neg_df, neg))

        pos = pd.DataFrame.from_csv(pos_path)
        if pos_df is None:
            pos_df = pos
        else:
            pos_df = pd.concat((pos_df, pos))

    overall_pos = base + "/dataset_pos_overall.csv"
    overall_neg = base + "/dataset_neg_overall.csv"
    pos_df.to_csv(overall_pos)
    neg_df.to_csv(overall_neg)


if __name__ == "__main__":
    base = '../../../train_dataset4/'
    # base = "../GemsDataset/subjects/"
    projects = ['antlr4', 'buck', "intellij-community", 'guava', 'presto', 'mockito', 'RxJava']  #, 'guava', 'intellij-community']
    # projects = ["junit3.8", "JHotDraw5.2", "MyWebMarket", "wikidev-filters", "myplanner-data-src"]
    for proj_dir in projects:
        merge_inside_basepath(base + proj_dir)

    merge_overall(base, projects)
