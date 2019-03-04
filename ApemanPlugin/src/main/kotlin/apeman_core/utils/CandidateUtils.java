package apeman_core.utils;

import apeman_core.pipes.CandidateWithFeatures;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiStatement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CandidateUtils {

    public static List<CandidateWithFeatures> getCandidatesOfMethod(
            PsiMethod method,
            List<CandidateWithFeatures> allCandidates)
    {
        return allCandidates.stream()
                .filter(elem -> elem.getCandidate().getSourceMethod().equals(method))
                .collect(Collectors.toList());
    }

    public static void checkStartOfCandidates(
            PsiStatement statement,
            List<CandidateWithFeatures> candidates
    ) {
        for (CandidateWithFeatures candidate: candidates) {
            if (candidate.getCandidate().getStart().equals(statement))
                candidate.getCandidate().setInCandidate(true);
        }
    }

    public static void checkEndOfCandidates(
            PsiStatement statement,
            List<CandidateWithFeatures> candidates
    ) {
        for (CandidateWithFeatures candidate: candidates) {
            if (candidate.getCandidate().getEnd().equals(statement))
                candidate.getCandidate().setInCandidate(false);
        }
    }
}
