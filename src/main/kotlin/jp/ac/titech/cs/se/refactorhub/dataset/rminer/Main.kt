package jp.ac.titech.cs.se.refactorhub.dataset.rminer

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jp.ac.titech.cs.se.refactorhub.dataset.rminer.converter.refactoring.convert
import jp.ac.titech.cs.se.refactorhub.dataset.rminer.miner.Miner
import jp.ac.titech.cs.se.refactorhub.dataset.rminer.oracle.Oracle
import java.io.File
import java.io.FileWriter

const val PATH = "outputs/dataset.ndjson"

fun main(args: Array<String>) {
    createDataset("Extract Method", 5, 1, false)
    createDataset("Rename Method", 5, 3, false)
    createDataset("Move Method", 5, 1, false)
    createDataset("Rename Variable", 5, 3, false)
    createDataset("Parameterize Variable", 5, 3, false)
    createDataset("Extract And Move Method", 5, 5, false)
}

fun createDataset(type: String, n: Int, m: Int, random: Boolean) {
    val file = File(PATH)
    if (!file.exists()) file.createNewFile()
    val dataset = Oracle.getDataset(type, n * 2, m, random)
    var count = 0
    for (metadata in dataset) {
        if (count >= n) break
        try {
            Miner.reDetect(metadata) {
                FileWriter(file, true).use { out ->
                    out.appendln(jacksonObjectMapper().writeValueAsString(
                        convert(
                            it,
                            metadata
                        )
                    ))
                }
            }
            count++
        } catch (e: Exception) {
            println(e)
        }
    }
}