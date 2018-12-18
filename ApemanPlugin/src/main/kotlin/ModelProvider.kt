import java.io.*
import java.lang.ProcessBuilder
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate
import java.io.FileReader

const val PYTHON_PATH = "python3"
const val MODEL_DIR = "../../../../Model/"
const val TRAINING_CSV_POSITIVE_REAL = MODEL_DIR + "pos_real.csv"
const val TRAINING_CSV_POSITIVE_AUGMENTED = MODEL_DIR + "pos_aug.csv"
const val TRAINING_CSV_NEGATIVE_REAL = MODEL_DIR + "neg_real.csv"
const val TRAINING_CSV_NEGATIVE_AUGMENTED = MODEL_DIR + "neg_aug.csv"

const val GEMS_CSV_POSITIVE_REAL = "../../../../GemsDataset/real_set/con_pos404.csv"
const val GEMS_CSV_POSITIVE_AUGMENTED = "../../../../GemsDataset/augmented_set/con_pos404.csv"
const val GEMS_CSV_NEGATIVE_REAL = "../../../../GemsDataset/real_set/con_neg404.csv"
const val GEMS_CSV_NEGATIVE_AUGMENTED = "../../../../GemsDataset/augmented_set/con_neg404.csv"

const val TRAINING_SCRIPT_NAME = "train_model.py"
const val PREDICTING_SCRIPT_NAME = "predict.py"
const val STORE_CANDIDATES = MODEL_DIR + "candidates.csv"
const val PREDICTED_PROBABILITIES = "probabilities.cvs"


class ModelProvider {

    private fun trainModel(columns: ArrayList<String>) {
        saveTrainedCsv(from=GEMS_CSV_POSITIVE_REAL, to=TRAINING_CSV_POSITIVE_REAL, columns=columns)
        saveTrainedCsv(from=GEMS_CSV_NEGATIVE_REAL, to=TRAINING_CSV_NEGATIVE_REAL, columns=columns)

        saveTrainedCsv(from=GEMS_CSV_POSITIVE_AUGMENTED, to=TRAINING_CSV_POSITIVE_AUGMENTED, columns=columns)
        saveTrainedCsv(from=GEMS_CSV_NEGATIVE_AUGMENTED, to=TRAINING_CSV_NEGATIVE_AUGMENTED, columns=columns)

        callPythonProcess(scriptName=TRAINING_SCRIPT_NAME)
    }

    private fun saveTrainedCsv(from: String, to: String, columns: ArrayList<String>) {
        val csv = importCsvFrom(from)
        csv.remainColumns(columns)
        csv.export(to)
    }

    private fun predictCandidates(candToFeatures: HashMap<ExtractionCandidate, FeatureVector>,
                                  featureNames: ArrayList<String>)
    {
        val csv = importCsvFrom(candToFeatures, featureNames)
        csv.export(STORE_CANDIDATES)
        callPythonProcess(PREDICTING_SCRIPT_NAME)
    }

    private fun loadProbabilities(): IntArray {

        val lines = File(MODEL_DIR + PREDICTED_PROBABILITIES).useLines { it }
        val array = lines.map { it.toInt() }.toList()
        return IntArray(array.size) {i: Int -> array[i]}
    }

    private fun callPythonProcess(scriptName: String) {

        val processBuilder = ProcessBuilder(PYTHON_PATH, MODEL_DIR + scriptName)

        processBuilder.directory(File(MODEL_DIR))
        val process = processBuilder.start()
        process.waitFor()

        val stdError = BufferedReader(InputStreamReader(process.errorStream))
        val lines = stdError.lineSequence()
        stdError.close()

        if (lines != emptySequence<String>()) {
            lines.forEach { println(it) }
            throw IOException("Python process failed")
        }
    }
}

//data class Csv (val data: ArrayList<LinkedHashMap<String, String>>)
