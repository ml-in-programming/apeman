package dataset;

public class Main {
    public static void main(String[] args) throws Exception {
        dataset.DatasetExtracter extracter = new dataset.DatasetExtracter();
        extracter.detectAllRefactorings();
    }
}
