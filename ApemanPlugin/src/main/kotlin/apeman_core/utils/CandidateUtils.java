package apeman_core.utils;

import apeman_core.pipes.CandidateWithFeatures;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiStatement;
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class CandidateUtils {

    public static ArrayList<CandidateWithFeatures> getCandidatesOfMethod(
            PsiMethod method,
            ArrayList<CandidateWithFeatures> allCandidates)
    {
        return allCandidates.stream()
                .filter(elem -> elem.getCandidate().getSourceMethod().equals(method))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static void checkStartOfCandidates(
            PsiStatement statement,
            ArrayList<CandidateWithFeatures> candidates
    ) {
        for (CandidateWithFeatures candidate: candidates) {
            if (candidate.getCandidate().getStart().equals(statement))
                candidate.getCandidate().setInCandidate(true);
        }
    }

    public static void checkEndOfCandidates(
            PsiStatement statement,
            ArrayList<CandidateWithFeatures> candidates
    ) {
        for (CandidateWithFeatures candidate: candidates) {
            if (candidate.getCandidate().getEnd().equals(statement))
                candidate.getCandidate().setInCandidate(false);
        }
    }
}
