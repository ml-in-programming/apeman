package apeman_core.prediction

import apeman_core.pipes.CandidateWithFeatures
import apeman_core.pipes.CandidatesWithFeaturesAndProba
import java.io.*
import java.lang.ProcessBuilder

const val PYTHON_PATH = "python3"
const val MODEL_DIR = "/home/snyss/Prog/mm/diploma/main/apeman/Model/"
const val TRAINING_CSV_POSITIVE_REAL = MODEL_DIR + "pos_real.csv"
const val TRAINING_CSV_POSITIVE_AUGMENTED = MODEL_DIR + "pos_aug.csv"
const val TRAINING_CSV_NEGATIVE_REAL = MODEL_DIR + "neg_real.csv"
const val TRAINING_CSV_NEGATIVE_AUGMENTED = MODEL_DIR + "neg_aug.csv"

const val GEMS_BASE = "/home/snyss/Prog/mm/diploma/main/apeman/GemsDataset/"
const val GEMS_CSV_POSITIVE_REAL = GEMS_BASE + "real_set/con_pos404.csv"
const val GEMS_CSV_POSITIVE_AUGMENTED = GEMS_BASE + "augmented_set/con_pos404.csv"
const val GEMS_CSV_NEGATIVE_REAL = GEMS_BASE + "real_set/con_neg404.csv"
const val GEMS_CSV_NEGATIVE_AUGMENTED = GEMS_BASE + "augmented_set/con_pos404.csv"

const val TRAINING_SCRIPT_NAME = "train_model.py"
const val PREDICTING_SCRIPT_NAME = "predict.py"
const val STORE_CANDIDATES = MODEL_DIR + "candidates.csv"
const val PREDICTED_PROBABILITIES = MODEL_DIR + "probabilities.csv"


class ModelProvider(
        private val candidates: List<CandidateWithFeatures>
) {

    fun trainModel() {
        saveTrainingCsv(from = GEMS_CSV_POSITIVE_REAL, to = TRAINING_CSV_POSITIVE_REAL)
        saveTrainingCsv(from = GEMS_CSV_NEGATIVE_REAL, to = TRAINING_CSV_NEGATIVE_REAL)

        saveTrainingCsv(from = GEMS_CSV_POSITIVE_AUGMENTED, to = TRAINING_CSV_POSITIVE_AUGMENTED)
        saveTrainingCsv(from = GEMS_CSV_NEGATIVE_AUGMENTED, to = TRAINING_CSV_NEGATIVE_AUGMENTED)

        callPythonProcess(scriptName = TRAINING_SCRIPT_NAME)
    }

    private fun saveTrainingCsv(from: String, to: String) {
        val csv = importCsvFrom(from)
        csv.remainColumns(ArrayList(getColumnNames()))
        csv.export(to)
    }

    private fun getColumnNames(): List<String> {
        assert(candidates.isNotEmpty())
        return candidates[0].features.map { it.key.name }.toList()
    }

    fun predictCandidates(): List<CandidatesWithFeaturesAndProba> {

        val csv = importCsvFrom(ArrayList(candidates), ArrayList(getColumnNames()))
        csv.export(STORE_CANDIDATES)
        callPythonProcess(PREDICTING_SCRIPT_NAME)
        return loadProbabilities().toList()
    }

    private fun loadProbabilities(): ArrayList<CandidatesWithFeaturesAndProba> {
        val result = ArrayList<CandidatesWithFeaturesAndProba>()

        val csv = importCsvFrom(PREDICTED_PROBABILITIES)
        if (csv.data.isEmpty())
            return arrayListOf()

        assert(csv.data[0].size == 1)
        for ((probability, cand) in csv.data.zip(candidates)) {
            result.add(CandidatesWithFeaturesAndProba(
                    cand.candidate,
                    cand.features,
                    probability[0].toDouble()
            ))
        }
        return result
    }

    private fun callPythonProcess(scriptName: String) {

        val processBuilder = ProcessBuilder(PYTHON_PATH, MODEL_DIR + scriptName)

        processBuilder.directory(File(MODEL_DIR))
        val process = processBuilder.start()
        process.waitFor()

        val stdError = BufferedReader(InputStreamReader(process.errorStream) as Reader)
        val lines = stdError.lineSequence().toList()
        stdError.close()

        if (lines.isNotEmpty()) {
            lines.forEach { println(it) }
            throw IOException("Python process failed")
        }
    }
}
