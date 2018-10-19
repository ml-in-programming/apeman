package dataset;

import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.diff.ExtractOperationRefactoring;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.refactoringminer.api.*;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

import javax.validation.constraints.NotNull;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatasetExtracter {
    GitHistoryRefactoringMiner miner;
    GitService gitService;

    JavaReposDetecter fullNamesOfRepositories;
    ArrayList<String> fullNames;

    public static final String CLONE_REPO_PREFIX = "https://github.com/";
    public static final String CLONE_REPO_SUFFIX = ".git";

    public static final String FILE_REFACTORINGS_NAME = "reafctorings.txt";
    final File refactorings;

    public DatasetExtracter() throws Exception {
        fullNamesOfRepositories = new JavaReposDetecter();
        fullNames = fullNamesOfRepositories.getReposIfNotExist();

        refactorings = new File(FILE_REFACTORINGS_NAME);

        if (refactorings.exists()) {
            refactorings.delete();
        }

        gitService = new GitServiceImpl();
        miner = new GitHistoryRefactoringMinerImpl();
    }

    public void detectAllRefactorings() throws Exception {
        for (int i = 0; i < fullNames.size(); i++) {

            System.out.println("clonning another repo...");
            Repository repo = gitService.cloneIfNotExists(
                    "tmp/" + fullNames.get(i),
                    CLONE_REPO_PREFIX + fullNames.get(i) + CLONE_REPO_SUFFIX
            );
            detectRefactoringsOfRepo(repo);
        }

    }

    private void detectRefactoringsOfRepo(@NotNull Repository repo) throws Exception {
        System.out.println("detecting...");

        try {
            miner.detectAll(repo, "master", new RefactoringHandler() {
                @Override
                public void handle(RevCommit commitData, List<Refactoring> refactorings) {
                    if (!refactorings.isEmpty()) {
                        parseRefactorings(refactorings, commitData);
                    }
                }
            });
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }
    }

    private void parseRefactorings(List<Refactoring> refactorings, RevCommit commitData) {
        System.out.println();
        System.out.println("found refactoring!");

        for (Refactoring ref : refactorings) {
            System.out.println(ref.getRefactoringType());

            if (ref.getRefactoringType() == RefactoringType.EXTRACT_OPERATION) {
                saveExtractMethodRefactoring((ExtractOperationRefactoring) ref, commitData);
            }
        }
    }

    private void saveExtractMethodRefactoring(ExtractOperationRefactoring extractRef, RevCommit commitData) {
        System.out.println("saving to file...");

        try {

            BufferedWriter writer = new BufferedWriter(new FileWriter(refactorings, true));
            writer.write(getInfoExtractMethod(extractRef, commitData));
            writer.newLine();
            writer.newLine();
            writer.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String getInfoExtractMethod(ExtractOperationRefactoring extractRef, RevCommit commitData) {

        UMLOperation sourceMethod = extractRef.getSourceOperationBeforeExtraction();

        String info = "";
        info += "\nClass = " + sourceMethod.getClassName();
        info += "\nSource method = " + sourceMethod.toString();
        info += "\nExtracted method = " + extractRef.getExtractedOperation().toString();
        info += "\nStart and end lines: ";
        info += sourceMethod.getLocationInfo().getStartLine();
        info += ", ";
        info += sourceMethod.getLocationInfo().getEndLine();
        info += "\nCommit: " + commitData.getId().getName();
        info += "\nCommit parent: " + commitData.getParent(0).getId().getName();

        return info;
    }
}
