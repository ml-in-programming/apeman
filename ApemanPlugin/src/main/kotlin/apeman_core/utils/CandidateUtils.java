package apeman_core.utils;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiStatement;
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class CandidateUtils {

    public static ArrayList<ExtractionCandidate> getCandidatesOfMethod(
            PsiMethod method,
            ArrayList<ExtractionCandidate> allCandidates)
    {
        return allCandidates.stream()
                .filter(elem -> elem.getSourceMethod().equals(method))
                .collect(Collectors.toCollection(ArrayList::new));

    }

    public static void checkStartOfCandidates(
            PsiStatement statement,
            ArrayList<ExtractionCandidate> candidates
    ) {
        for (ExtractionCandidate candidate: candidates) {
            if (candidate.getStart().equals(statement))
                candidate.setInCandidate(true);
        }
    }

    public static void checkEndOfCandidates(
            PsiStatement statement,
            ArrayList<ExtractionCandidate> candidates
    ) {
        for (ExtractionCandidate candidate: candidates) {
            if (candidate.getEnd().equals(statement))
                candidate.setInCandidate(false);
        }
    }
}
