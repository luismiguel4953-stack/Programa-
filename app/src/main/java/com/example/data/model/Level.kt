package com.example.data.model

enum class TrackType(
    val title: String,
    val description: String,
    val startLevel: Int,
    val endLevel: Int,
    val iconName: String
) {
    KOTLIN_BASICS("Kotlin Syntax & Variables", "Variables, types, null-safety & basic expressions", 1, 45, "Code"),
    CONTROL_FLOW("Logic & Control Flow", "If/else, when expressions, loops & ranges", 46, 90, "AltRoute"),
    FUNCTIONS_LAMBDAS("Functions & Lambdas", "Functions, parameters, tailrec, HOF & scope functions", 91, 135, "Functions"),
    COLLECTIONS("Collections & Data Streams", "Lists, Maps, Sets, filter, map, fold & sequences", 136, 180, "ViewList"),
    OOP_CLASSES("Object-Oriented & Types", "Classes, interfaces, sealed classes, generics & delegates", 181, 225, "Class"),
    COROUTINES_ASYNC("Coroutines & Asynchronous", "Dispatchers, launch, async, Flow, Channels & Mutex", 226, 270, "Speed"),
    GIT_GITHUB("Git & GitHub Mastery", "Commits, branching, PRs, merge conflicts & GitHub APIs", 271, 315, "AccountTree"),
    JETPACK_COMPOSE("Jetpack Compose & UI", "Composables, State, Recomposition, Modifiers & Layouts", 316, 360, "Smartphone"),
    REST_APIS("REST APIs & Architecture", "Retrofit, Moshi, Clean Architecture & Room Persistence", 361, 405, "Cloud"),
    ALGORITHMS_PUZZLES("Algorithms & Logic Puzzles", "Recursion, searching, sorting, graph & puzzle challenges", 406, 450, "Psychology");

    companion object {
        fun forLevel(levelId: Int): TrackType {
            return entries.find { levelId in it.startLevel..it.endLevel } ?: KOTLIN_BASICS
        }
    }
}

enum class Difficulty {
    EASY, MEDIUM, HARD, EXPERT, MASTER
}

enum class QuestionType {
    MULTIPLE_CHOICE,   // Choose correct output or answer
    REORDER_BLOCKS,    // Drag/tap code blocks into correct order
    FILL_BLANKS,       // Choose correct keyword/symbol for missing blank
    BUG_FIX,           // Identify the line with error or fix it
    CODE_SIMULATOR     // Interactive code sandbox level with target output
}

data class Level(
    val id: Int,
    val title: String,
    val track: TrackType,
    val difficulty: Difficulty,
    val questionType: QuestionType,
    val prompt: String,
    val codeSnippet: String = "",
    val codeBlocks: List<String> = emptyList(), // For reordering or fill options
    val options: List<String> = emptyList(),     // For multiple choice
    val correctAnswer: String,                  // Correct string answer or index sequence e.g. "0,1,2"
    val explanation: String,
    val hint: String,
    val xpReward: Int = 100,
    val coinReward: Int = 20
)
