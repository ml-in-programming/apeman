package proof_of_concept

import com.intellij.openapi.application.ApplicationStarter

class ProofOfConceptLauncher : ApplicationStarter {

    override fun getCommandName() = "proof-launcher"

    override fun premain(args: Array<out String>?) {}

    override fun main(args: Array<String>) {
        val subjectDir = "/home/snyss/Prog/mm/diploma/gems_datasets/subjects/"

        val junit = OneProjectAnalyzer(subjectDir + "junit3.8")
        junit.analyze()
    }
}
