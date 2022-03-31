package search

import java.io.File
import java.io.InputStream
import java.util.*

fun main(args: Array<String>) {

    val fileName = args[1]

    val lineList = mutableListOf<String>()

    val inputStream: InputStream = File(fileName).inputStream()
    inputStream.bufferedReader().forEachLine { lineList.add(it) }

    val dataMap = transformListToMap(lineList)

    val isExit = false
    do {
        println(
            "=== Menu ===\n" +
                    "1. Find a person\n" +
                    "2. Print all people\n" +
                    "0. Exit"
        )
        val menuChoice = readln().toInt()
        if (menuChoice == 0) {
            break
        } else {
            when (menuChoice) {
                1 -> findAPerson(dataMap, lineList)
                2 -> printAllPeople(lineList)
            }
        }
    } while (!isExit)
    println("Bye!")
}

fun transformListToMap(lineList: MutableList<String>): MutableMap<String, MutableSet<Int>> {
    val inputMap = mutableMapOf<String, MutableSet<Int>>()
    lineList.forEachIndexed { index, line ->
        val wordsArray = line.split(" ")
        for (word in wordsArray) {
            if (!inputMap.containsKey(word)) {
                inputMap[word] = mutableSetOf(index)
            } else {
                inputMap[word]?.add(index)
            }
        }
    }
    return inputMap
}

fun findAPerson(dataMap: MutableMap<String, MutableSet<Int>>, lineList: MutableList<String>) {
    println("Select a matching strategy: ALL, ANY, NONE")
    val strategy = readln().uppercase(Locale.getDefault())

    println("Enter a name or email to search all suitable people.")
    val searchToken = readln()

    val results: MutableList<String> = when (strategy) {
        "ALL" -> findAll(searchToken, lineList)
        "ANY" -> findAny(dataMap, searchToken, lineList).toMutableList()
        "NONE" -> findNone(searchToken, lineList, dataMap)
        else -> {
            println("Invalid strategy")
            return
        }
    }

    if (results.isEmpty()) {
        println("No matching people found.")
    } else {
        println("People found:")
        for (result in results) {
            println(result)
        }
    }
}

fun organizeResultsForAny(results: MutableList<String>, lineList: MutableList<String>): List<String> {
    val organizedResults: MutableList<String> = mutableListOf()
    for (result in results) {
        for (line in lineList) {
            if (line.split(" ")[0] == result) { //check that [0]
                organizedResults.add(line)
            }
        }
    }
    return organizedResults.distinct()
}

fun findAny(dataMap: MutableMap<String, MutableSet<Int>>, token: String, lineList: MutableList<String>): List<String> {
    val results = mutableListOf<String>()
    val tokensList = token.split(" ")
    val indexes: MutableSet<Int> = mutableSetOf()
    for (currentToken in tokensList) {
        indexes.addAll(findAllValuesWithToken(currentToken, dataMap))
    }
    for (index in indexes) {
        dataMap.forEach { (key, value) ->
            run {
                for (number in value) {
                    if (number == index) {
                        results.add(key)
                    }
                }
            }
        }
    }
    return organizeResultsForAny(results, lineList)
}

fun findAllValuesWithToken(token: String, dataMap: MutableMap<String, MutableSet<Int>>): MutableSet<Int> {
    val indexSet: MutableSet<Int> = mutableSetOf()
    for ((key) in dataMap) {
        if (token.equals(key, true)) {
            val set = dataMap[key]
            if (set != null) {
                for (number in set) {
                    indexSet.add(number)
                }
            }
        }
    }
    return indexSet
}

fun findAll(token: String, lineList: MutableList<String>): MutableList<String> {
    val results: MutableList<String> = mutableListOf()
    for (line in lineList) {
        if (line.contains(token, true)) {
            results.add(line)
        }
    }
    return results
}

fun findNone(
    searchToken: String,
    lineList: MutableList<String>,
    dataMap: MutableMap<String, MutableSet<Int>>
): MutableList<String> {
    val resultAny = findAny(dataMap, searchToken, lineList)
    val finalResult = mutableListOf<String>()
    for (line in lineList) {
        if (!resultAny.contains(line)) {
            finalResult.add(line)
        }
    }
    return finalResult
}


fun printAllPeople(listOfInput: MutableList<String>) {
    println("=== List of people ===")
    for (line in listOfInput) {
        println(line)
    }
}