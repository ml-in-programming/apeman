import pandas as pd

if __name__ == "__main__":

    intellij_path = '../../../train_dataset3/intellij-community/'

    pos_df = None
    neg_df = None
    for i in range(0, 4800, 400):
        neg = pd.DataFrame.from_csv(intellij_path + f"dataset_neg_{i}.csv")
        pos = pd.DataFrame.from_csv(intellij_path + f"dataset_pos_{i}.csv")

        if pos_df is None:
            pos_df = pos
        else:
            pos_df = pd.concat((pos_df, pos))

        if neg_df is None:
            neg_df = neg
        else:
            neg_df = pd.concat((neg_df, neg))

    overall_pos = intellij_path + "dataset_pos_overall.csv"
    overall_neg = intellij_path + "dataset_neg_overall.csv"
    pos_df.to_csv(overall_pos)
    neg_df.to_csv(overall_neg)
