import java.io.*
import java.lang.ProcessBuilder

const val PYTHON_PATH = "python3"
const val MODEL_DIR = "../../../../Model/"

//val TRAINING_SCRIPT_NAME = "train_model.py"
const val PREDICTING_SCRIPT_NAME = "predict.py"

const val STORE_CANDIDATES = "candidates.csv"
const val PREDICTED_PROBABILITIES = "probabilities.cvs"

//fun trainModel() = callPythonProcess(TRAINING_SCRIPT_NAME)

fun predictCandidates() = callPythonProcess(PREDICTING_SCRIPT_NAME)

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

fun loadProbabilities(): IntArray {

    val lines = File(MODEL_DIR + PREDICTED_PROBABILITIES).useLines { it }
    val array = lines.map { it.toInt() }.toList()
    return IntArray(array.size) {i: Int -> array[i]}
}