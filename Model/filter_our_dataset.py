import pandas as pd


def filter_zero_con_loc(df: pd.DataFrame):
    return df.where(df["CON_LOC"] != 0).dropna(how='all')


if __name__ == "__main__":
    BASE = "../../../train_dataset4/"
    OUR_POS = BASE + "dataset_pos_overall.csv"
    OUR_NEG = BASE + "dataset_neg_overall.csv"
    pos = pd.read_csv(OUR_POS)
    neg = pd.read_csv(OUR_NEG)

    pos = filter_zero_con_loc(pos)
    neg = filter_zero_con_loc(neg)

    # for df in (pos, neg):
    #     df = filter_zero_con_loc(df)

    pos.to_csv(BASE + "pos_filtered.csv", index=False)
    neg.to_csv(BASE + "neg_filtered.csv", index=False)
