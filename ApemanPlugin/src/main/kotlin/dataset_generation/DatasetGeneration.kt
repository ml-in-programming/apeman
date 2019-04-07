package dataset_generation

import com.intellij.openapi.application.ApplicationStarter
import java.util.logging.Logger

class DatasetGeneration : ApplicationStarter {
    val log = Logger.getGlobal()
    val BASE_PATH = "/home/snyss/Prog/mm/diploma/train_dataset3/"

    override fun getCommandName() = "dataset_generator"
    override fun premain(args: Array<out String>?) {}
    override fun main(args: Array<out String>?) {

        InlineMethodsProcessor(listOf(
//                "antlr4",
//                "buck",
//                "deeplearning4j"
//                "elasticsearch-6.6.0",
//                "guava",
                "intellij-community"
//                "mockito",
//                "presto",
//                "RxJava"
        ).map { BASE_PATH + it })

        log.info("inlining success!")

//        val antlr4 = OneProjectDatasetGenerator(BASE_PATH + "antlr4")
//        val bulk = OneProjectDatasetGenerator(BASE_PATH + "bulk")
//        val deeplearning4j = OneProjectDatasetGenerator(BASE_PATH + "deeplearning4j")
//        val elasticsearch = OneProjectDatasetGenerator(BASE_PATH + "elasticsearch")
//        val facebookAndroidSdk = OneProjectDatasetGenerator(BASE_PATH + "facebook-android-sdk")
//        val guava = OneProjectDatasetGenerator(BASE_PATH + "guava")
//        val intellijCommunity = OneProjectDatasetGenerator(BASE_PATH + "intellij-community")
//        val mockito = OneProjectDatasetGenerator(BASE_PATH + "mockito")
//        val pocketHub = OneProjectDatasetGenerator(BASE_PATH + "PocketHub")
//        val presto = OneProjectDatasetGenerator(BASE_PATH + "presto")
//        val rxJava = OneProjectDatasetGenerator(BASE_PATH + "RxJava")
    }
}
