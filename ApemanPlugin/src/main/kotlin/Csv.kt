import org.apache.commons.csv.CSVFormat
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate
import java.io.FileReader
import java.io.FileWriter

class Csv(header: Collection<String>, data: Collection<Collection<String>>) {

    private val header = ArrayList<String>()
    private val data = ArrayList<ArrayList<String>>()

    init {
        header.toCollection(this.header)
        for (collection in data) {
            this.data.add(ArrayList(collection))
        }
    }

    fun export(filepath: String) {
        val output = FileWriter(filepath)
        val printer = CSVFormat.DEFAULT.print(output)!!

        printer.printRecord(header)
        printer.printRecords(data)
    }

    fun addIndicesColumn(columnName: String, indices: Collection<String>) {
        assert(data.size == indices.size)

        header.add(0, columnName)
        for ((arr, elemToAdd) in data.zip(indices)) {
            arr.add(0, elemToAdd)
        }
    }

    fun remainColumns(columnNames: ArrayList<String>) {

        assert(header.containsAll(columnNames))

        val columnsIndices = ArrayList<Int>()
        columnNames.forEach { columnsIndices.add(header.indexOf(it)) }

        header.clear()
        header.addAll(columnNames)

        for (arr in data) {
            val temp = ArrayList<String>()
            for (i in 0 until arr.size)
                if (columnsIndices.contains(i))
                    temp.add(arr[i])

            arr.clear()
            arr.addAll(temp)

            assert(arr.size == header.size)
        }
    }
}

fun importCsvFrom(filepath: String): Csv {
    val input = FileReader(filepath)
    val records = CSVFormat.DEFAULT.parse(input)

    val header = ArrayList<String>()
    for ((colName, _) in records.headerMap) {
        header.add(colName)
    }

    val data = ArrayList<ArrayList<String>>()
    for (record in records) {
        data.add(ArrayList(record.toMutableList()))
    }

    return Csv(header, data)
}

fun importCsvFrom(candToFeatures: HashMap<ExtractionCandidate, FeatureVector>, featureNames: ArrayList<String>): Csv
{

    val data = ArrayList<ArrayList<String>>()

    for (features in candToFeatures.values) {
        val featuresStr = ArrayList(features.map { it.toString() })
        data.add(featuresStr)
    }
    val csv = Csv(featureNames, data)
    val candidateNames = ArrayList(candToFeatures.keys.map { it.toString() })

    csv.addIndicesColumn("Names", candidateNames)

    return Csv(featureNames, data)
}
