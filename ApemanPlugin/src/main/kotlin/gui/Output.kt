package gui

import apeman_core.pipes.CandidatesWithFeaturesAndProba
import com.intellij.openapi.ui.Messages

fun showInfoDialog(candidates: List<CandidatesWithFeaturesAndProba>) {
    val sortedCandidates = candidates.sortedBy { -it.probability }
    var info = ""
    for ((cand, features, proba) in sortedCandidates) {
        info += "\n\n$cand:\n proba = $proba\n\n"
        for ((name, value) in features) {
            info += "$name = $value\n"
        }
    }

    Messages.showInfoMessage(info, "checked")
}