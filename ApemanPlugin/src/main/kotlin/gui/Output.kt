package gui

import apeman_core.base_entities.CandidatesWithFeaturesAndProba
import com.intellij.openapi.ui.Messages

fun showInfoDialog(candidates: List<CandidatesWithFeaturesAndProba>) {
    val sortedCandidates = candidates.sortedBy { -it.probability }
    var info = StringBuilder()
    for ((cand, features, proba) in sortedCandidates) {
        info.append("\n\n$cand:\n proba = $proba\n\n")
        for ((name, value) in features) {
            info.append("$name = $value\n")
        }
    }

    Messages.showInfoMessage(info.toString(), "checked")
}
