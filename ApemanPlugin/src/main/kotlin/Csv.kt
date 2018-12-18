import org.apache.commons.csv.CSVFormat
import org.jetbrains.research.groups.ml_methods.utils.ExtractionCandidate
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

class Csv(header: Collection<String>, data: Collection<Collection<String>>) {

    val header = ArrayList<String>()
    val data = ArrayList<ArrayList<String>>()

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
    for ((cand, features) in candToFeatures) {

        val featureVector = arrayListOf(cand.toString())
        featureVector.addAll(features.map { it.toString() })

        data.add(featureVector)
    }
    return Csv(featureNames, data)
}
