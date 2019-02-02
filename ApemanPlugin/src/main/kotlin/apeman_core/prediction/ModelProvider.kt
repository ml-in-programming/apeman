package apeman_core.prediction

import apeman_core.features_extraction.FeatureVector
import java.io.*
import java.lang.ProcessBuilder
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate

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


class ModelProvider {

    fun trainModel(columns: ArrayList<String>) {
        saveTrainingCsv(from = GEMS_CSV_POSITIVE_REAL, to = TRAINING_CSV_POSITIVE_REAL, columns = columns)
        saveTrainingCsv(from = GEMS_CSV_NEGATIVE_REAL, to = TRAINING_CSV_NEGATIVE_REAL, columns = columns)

        saveTrainingCsv(from = GEMS_CSV_POSITIVE_AUGMENTED, to = TRAINING_CSV_POSITIVE_AUGMENTED, columns = columns)
        saveTrainingCsv(from = GEMS_CSV_NEGATIVE_AUGMENTED, to = TRAINING_CSV_NEGATIVE_AUGMENTED, columns = columns)

        callPythonProcess(scriptName = TRAINING_SCRIPT_NAME)
    }

    private fun saveTrainingCsv(from: String, to: String, columns: ArrayList<String>) {
        val csv = importCsvFrom(from)
        csv.remainColumns(columns)
        csv.export(to)
    }

    fun predictCandidates(candToFeatures: HashMap<ExtractionCandidate, FeatureVector>,
                          featureNames: ArrayList<String>): ArrayList<Double> {
        val csv = importCsvFrom(candToFeatures, featureNames)
        csv.export(STORE_CANDIDATES)
        callPythonProcess(PREDICTING_SCRIPT_NAME)
        return loadProbabilities()
    }

    private fun loadProbabilities(): ArrayList<Double> {

        val csv = importCsvFrom(PREDICTED_PROBABILITIES)
        if (csv.data.isEmpty())
            return arrayListOf()

        assert(csv.data[0].size == 1)
        return ArrayList(csv.data.map { it[0].toDouble() })
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
