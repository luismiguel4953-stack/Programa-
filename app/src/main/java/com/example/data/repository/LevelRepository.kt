package com.example.data.repository

import com.example.data.model.Difficulty
import com.example.data.model.Level
import com.example.data.model.QuestionType
import com.example.data.model.TrackType

class LevelRepository {

    private val levelCache = mutableMapOf<Int, Level>()

    init {
        generateAllLevels()
    }

    fun getLevel(id: Int): Level? {
        return levelCache[id]
    }

    fun getAllLevels(): List<Level> {
        return levelCache.values.sortedBy { it.id }
    }

    fun getLevelsForTrack(track: TrackType): List<Level> {
        return levelCache.values.filter { it.track == track }.sortedBy { it.id }
    }

    fun searchLevels(query: String): List<Level> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return getAllLevels()

        val levelNumber = trimmed.toIntOrNull()
        if (levelNumber != null) {
            val exactLevel = getLevel(levelNumber)
            if (exactLevel != null) return listOf(exactLevel)
        }

        return getAllLevels().filter { level ->
            level.title.contains(query, ignoreCase = true) ||
                    level.prompt.contains(query, ignoreCase = true) ||
                    level.track.title.contains(query, ignoreCase = true)
        }
    }

    private fun generateAllLevels() {
        for (i in 1..450) {
            val track = TrackType.forLevel(i)
            val levelInTrack = i - track.startLevel + 1
            val level = createLevelForTrack(i, track, levelInTrack)
            levelCache[i] = level
        }
    }

    private fun createLevelForTrack(id: Int, track: TrackType, levelInTrack: Int): Level {
        val difficulty = when {
            levelInTrack <= 10 -> Difficulty.EASY
            levelInTrack <= 25 -> Difficulty.MEDIUM
            levelInTrack <= 35 -> Difficulty.HARD
            levelInTrack <= 42 -> Difficulty.EXPERT
            else -> Difficulty.MASTER
        }

        // Custom handcrafted logic for key levels across tracks
        return when (id) {
            1 -> Level(
                id = 1,
                title = "Hello Kotlin World",
                track = TrackType.KOTLIN_BASICS,
                difficulty = Difficulty.EASY,
                questionType = QuestionType.MULTIPLE_CHOICE,
                prompt = "What keyword is used to declare an immutable variable in Kotlin?",
                codeSnippet = "___ message: String = \"Hello, CodeQuest!\"",
                options = listOf("val", "var", "let", "const"),
                correctAnswer = "val",
                explanation = "In Kotlin, `val` declares a read-only (immutable) local variable that cannot be reassigned.",
                hint = "Think of 'value' - it stays constant!",
                xpReward = 100,
                coinReward = 20
            )
            2 -> Level(
                id = 2,
                title = "Mutable Variables",
                track = TrackType.KOTLIN_BASICS,
                difficulty = Difficulty.EASY,
                questionType = QuestionType.FILL_BLANKS,
                prompt = "Complete the code to allow reassigning the variable `score`.",
                codeSnippet = "___ score: Int = 10\nscore = 15",
                codeBlocks = listOf("var", "val", "def", "mut"),
                correctAnswer = "var",
                explanation = "`var` defines a mutable variable in Kotlin whose value can be reassigned later.",
                hint = "'var' stands for variable!",
                xpReward = 100,
                coinReward = 20
            )
            3 -> Level(
                id = 3,
                title = "Reorder String Concatenation",
                track = TrackType.KOTLIN_BASICS,
                difficulty = Difficulty.EASY,
                questionType = QuestionType.REORDER_BLOCKS,
                prompt = "Reorder the blocks to construct a valid Kotlin print statement using string templates.",
                codeSnippet = "// Goal: Output \"Player Score: 100\"",
                codeBlocks = listOf("val score = 100", "println(\"Player Score: \$score\")"),
                correctAnswer = "0,1",
                explanation = "Variables must be defined before they are referenced inside string template expressions like `$score`.",
                hint = "Declare the variable first, then print it.",
                xpReward = 110,
                coinReward = 22
            )
            4 -> Level(
                id = 4,
                title = "Spot the Nullability Bug",
                track = TrackType.KOTLIN_BASICS,
                difficulty = Difficulty.EASY,
                questionType = QuestionType.BUG_FIX,
                prompt = "Which line causes a Kotlin compilation error?",
                codeSnippet = "Line 1: val name: String = \"Alice\"\nLine 2: name = null\nLine 3: val age: Int? = null\nLine 4: println(age)",
                options = listOf("Line 1", "Line 2", "Line 3", "Line 4"),
                correctAnswer = "Line 2",
                explanation = "Line 2 tries to assign `null` to a non-nullable type `String`. To allow null, use `String?`.",
                hint = "Regular String types cannot hold null values.",
                xpReward = 120,
                coinReward = 25
            )
            5 -> Level(
                id = 5,
                title = "Elvis Operator Mastery",
                track = TrackType.KOTLIN_BASICS,
                difficulty = Difficulty.EASY,
                questionType = QuestionType.MULTIPLE_CHOICE,
                prompt = "What is the output of this code?",
                codeSnippet = "val username: String? = null\nval displayName = username ?: \"Guest\"\nprintln(displayName)",
                options = listOf("Guest", "null", "username", "Error"),
                correctAnswer = "Guest",
                explanation = "The Elvis operator `?:` returns the left operand if it is not null; otherwise, it evaluates and returns the right operand (\"Guest\").",
                hint = "Look at the Elvis operator ?: when null is encountered.",
                xpReward = 120,
                coinReward = 25
            )
            46 -> Level(
                id = 46,
                title = "When Expressions",
                track = TrackType.CONTROL_FLOW,
                difficulty = Difficulty.EASY,
                questionType = QuestionType.MULTIPLE_CHOICE,
                prompt = "What value is assigned to `status` when `level = 5`?",
                codeSnippet = "val level = 5\nval status = when (level) {\n    1 -> \"Beginner\"\n    in 2..9 -> \"Intermediate\"\n    else -> \"Master\"\n}",
                options = listOf("Intermediate", "Beginner", "Master", "null"),
                correctAnswer = "Intermediate",
                explanation = "5 falls in the range `2..9`, so `status` evaluates to \"Intermediate\".",
                hint = "Check the range in 2..9.",
                xpReward = 130,
                coinReward = 25
            )
            91 -> Level(
                id = 91,
                title = "Single-Expression Functions",
                track = TrackType.FUNCTIONS_LAMBDAS,
                difficulty = Difficulty.EASY,
                questionType = QuestionType.FILL_BLANKS,
                prompt = "Fill in the syntax to declare a concise single-expression function.",
                codeSnippet = "fun double(x: Int): Int ___ x * 2",
                codeBlocks = listOf("=", "->", ":=", "{return}"),
                correctAnswer = "=",
                explanation = "Single-expression functions in Kotlin use the `=` sign instead of curly braces and explicit return type declaration.",
                hint = "Use the equals sign for concise functions.",
                xpReward = 140,
                coinReward = 30
            )
            136 -> Level(
                id = 136,
                title = "Filtering Collections",
                track = TrackType.COLLECTIONS,
                difficulty = Difficulty.EASY,
                questionType = QuestionType.MULTIPLE_CHOICE,
                prompt = "What will `evenNumbers` contain?",
                codeSnippet = "val numbers = listOf(1, 2, 3, 4, 5)\nval evenNumbers = numbers.filter { it % 2 == 0 }",
                options = listOf("[2, 4]", "[1, 3, 5]", "[2, 4, 6]", "[]"),
                correctAnswer = "[2, 4]",
                explanation = "`.filter` keeps elements matching the predicate `it % 2 == 0`, selecting 2 and 4.",
                hint = "Numbers divisible by 2 with remainder 0.",
                xpReward = 150,
                coinReward = 30
            )
            181 -> Level(
                id = 181,
                title = "Data Classes in Kotlin",
                track = TrackType.OOP_CLASSES,
                difficulty = Difficulty.EASY,
                questionType = QuestionType.FILL_BLANKS,
                prompt = "Which keyword automatically generates `copy()`, `equals()`, and `toString()`?",
                codeSnippet = "___ class User(val name: String, val xp: Int)",
                codeBlocks = listOf("data", "sealed", "open", "object"),
                correctAnswer = "data",
                explanation = "`data class` automatically generates componentN(), copy(), equals(), hashCode(), and toString() functions.",
                hint = "Short for data holder!",
                xpReward = 160,
                coinReward = 35
            )
            226 -> Level(
                id = 226,
                title = "Coroutines Dispatchers",
                track = TrackType.COROUTINES_ASYNC,
                difficulty = Difficulty.MEDIUM,
                questionType = QuestionType.MULTIPLE_CHOICE,
                prompt = "Which CoroutineDispatcher is optimized for disk I/O or network API requests?",
                codeSnippet = "viewModelScope.launch(Dispatchers.___) {\n    val data = repository.fetchRemoteData()\n}",
                options = listOf("IO", "Main", "Default", "Unconfined"),
                correctAnswer = "IO",
                explanation = "`Dispatchers.IO` uses a shared pool of threads designed for offloading blocking I/O operations such as network and database calls.",
                hint = "IO stands for Input/Output!",
                xpReward = 180,
                coinReward = 40
            )
            271 -> Level(
                id = 271,
                title = "Git Branch Creation",
                track = TrackType.GIT_GITHUB,
                difficulty = Difficulty.EASY,
                questionType = QuestionType.MULTIPLE_CHOICE,
                prompt = "Which Git command creates AND switches to a new branch called `feature/level-system`?",
                codeSnippet = "\$ git ___ feature/level-system",
                options = listOf("checkout -b", "branch -new", "commit -b", "switch --create-only"),
                correctAnswer = "checkout -b",
                explanation = "`git checkout -b <branch>` creates a new branch and immediately checks it out (or `git switch -c`).",
                hint = "b stands for branch flag in checkout!",
                xpReward = 150,
                coinReward = 30
            )
            316 -> Level(
                id = 316,
                title = "Compose State Hosting",
                track = TrackType.JETPACK_COMPOSE,
                difficulty = Difficulty.EASY,
                questionType = QuestionType.FILL_BLANKS,
                prompt = "Fill in the delegate keyword to observe Compose state across recomposition.",
                codeSnippet = "@Composable\nfun Counter() {\n    var count by ___ { mutableStateOf(0) }\n}",
                codeBlocks = listOf("remember", "observe", "collect", "retain"),
                correctAnswer = "remember",
                explanation = "`remember` preserves a value across recomposition in Jetpack Compose.",
                hint = "Don't forget it — remember it!",
                xpReward = 170,
                coinReward = 35
            )
            361 -> Level(
                id = 361,
                title = "Retrofit HTTP Annotations",
                track = TrackType.REST_APIS,
                difficulty = Difficulty.MEDIUM,
                questionType = QuestionType.MULTIPLE_CHOICE,
                prompt = "Which annotation fetches GitHub user info in Retrofit?",
                codeSnippet = "interface GitHubApi {\n    @___(\"users/{username}\")\n    suspend fun getUser(@Path(\"username\") user: String): UserDto\n}",
                options = listOf("GET", "POST", "PUT", "FETCH"),
                correctAnswer = "GET",
                explanation = "HTTP `@GET` is used to retrieve resource representations from an API server.",
                hint = "Standard HTTP method to read resources.",
                xpReward = 180,
                coinReward = 40
            )
            406 -> Level(
                id = 406,
                title = "Binary Search Complexity",
                track = TrackType.ALGORITHMS_PUZZLES,
                difficulty = Difficulty.HARD,
                questionType = QuestionType.MULTIPLE_CHOICE,
                prompt = "What is the worst-case time complexity of Binary Search on a sorted array of size N?",
                codeSnippet = "fun binarySearch(arr: IntArray, target: Int): Int",
                options = listOf("O(log N)", "O(N)", "O(N log N)", "O(1)"),
                correctAnswer = "O(log N)",
                explanation = "Binary Search halves the search space with each step, yielding logarithmic time complexity O(log N).",
                hint = "Halving the input size repeatedly produces logarithm.",
                xpReward = 200,
                coinReward = 50
            )
            450 -> Level(
                id = 450,
                title = "The Ultimate Code Quest Boss",
                track = TrackType.ALGORITHMS_PUZZLES,
                difficulty = Difficulty.MASTER,
                questionType = QuestionType.CODE_SIMULATOR,
                prompt = "Write a function `fibonacci(n)` that returns the Nth Fibonacci number (where fib(0)=0, fib(1)=1, fib(2)=1, fib(3)=2, fib(4)=3).",
                codeSnippet = "fun fibonacci(n: Int): Int {\n    if (n <= 1) return n\n    var a = 0\n    var b = 1\n    for (i in 2..n) {\n        val temp = a + b\n        a = b\n        b = temp\n    }\n    return b\n}",
                options = listOf("0,1,1,2,3", "O(N) Time", "O(1) Space", "All of the above"),
                correctAnswer = "All of the above",
                explanation = "Congratulations! You have completed all 450 levels of CodeQuest! You are a true Code Legend!",
                hint = "Compute fibonacci dynamically with iteration.",
                xpReward = 500,
                coinReward = 1000
            )
            else -> generateProceduralLevel(id, track, levelInTrack, difficulty)
        }
    }

    private fun generateProceduralLevel(
        id: Int,
        track: TrackType,
        levelInTrack: Int,
        difficulty: Difficulty
    ): Level {
        val typeIndex = (id % 4)
        val questionType = when (typeIndex) {
            0 -> QuestionType.MULTIPLE_CHOICE
            1 -> QuestionType.FILL_BLANKS
            2 -> QuestionType.REORDER_BLOCKS
            else -> QuestionType.BUG_FIX
        }

        val title = "${track.title} Challenge #$levelInTrack"

        return when (track) {
            TrackType.KOTLIN_BASICS -> {
                val num1 = (levelInTrack * 3) + 2
                val num2 = (levelInTrack * 2) + 5
                Level(
                    id = id,
                    title = title,
                    track = track,
                    difficulty = difficulty,
                    questionType = questionType,
                    prompt = "Evaluate the outcome of Kotlin basic expression #$levelInTrack.",
                    codeSnippet = "val x = $num1\nval y = $num2\nval result = (x + y) * 2\nprintln(result)",
                    options = listOf("${(num1 + num2) * 2}", "${num1 + num2 * 2}", "${(num1 + num2) + 2}", "Error"),
                    codeBlocks = listOf("val x = $num1", "val y = $num2", "val result = (x + y) * 2"),
                    correctAnswer = "${(num1 + num2) * 2}",
                    explanation = "In Kotlin arithmetic, expressions inside parentheses `(x + y)` evaluate first before multiplying by 2.",
                    hint = "Parentheses have the highest precedence.",
                    xpReward = 100 + levelInTrack * 2,
                    coinReward = 20 + levelInTrack / 2
                )
            }
            TrackType.CONTROL_FLOW -> {
                val bound = levelInTrack * 5
                Level(
                    id = id,
                    title = title,
                    track = track,
                    difficulty = difficulty,
                    questionType = questionType,
                    prompt = "How many iterations will this loop execute?",
                    codeSnippet = "var count = 0\nfor (i in 1..$bound step 2) {\n    count++\n}",
                    options = listOf("${(bound + 1) / 2}", "$bound", "${bound / 2}", "${bound + 1}"),
                    codeBlocks = listOf("var count = 0", "for (i in 1..$bound step 2)", "count++"),
                    correctAnswer = "${(bound + 1) / 2}",
                    explanation = "`1..$bound step 2` steps by 2 starting at 1, running exactly ${(bound + 1) / 2} times.",
                    hint = "Notice the step 2 increment in the loop.",
                    xpReward = 110 + levelInTrack * 2,
                    coinReward = 22 + levelInTrack / 2
                )
            }
            TrackType.FUNCTIONS_LAMBDAS -> {
                Level(
                    id = id,
                    title = title,
                    track = track,
                    difficulty = difficulty,
                    questionType = questionType,
                    prompt = "What is the higher-order function return value for level #$levelInTrack?",
                    codeSnippet = "fun execute(x: Int, transform: (Int) -> Int): Int {\n    return transform(x + $levelInTrack)\n}\nval result = execute(10) { it * 2 }",
                    options = listOf("${(10 + levelInTrack) * 2}", "${10 * 2 + levelInTrack}", "${(10 + levelInTrack)}", "20"),
                    codeBlocks = listOf("fun execute...", "val result = execute(10)", "{ it * 2 }"),
                    correctAnswer = "${(10 + levelInTrack) * 2}",
                    explanation = "`execute(10)` calculates `10 + $levelInTrack = ${10 + levelInTrack}` then applies `{ it * 2 }` resulting in ${(10 + levelInTrack) * 2}.",
                    hint = "Add $levelInTrack to 10 first, then multiply by 2.",
                    xpReward = 120 + levelInTrack * 2,
                    coinReward = 25 + levelInTrack / 2
                )
            }
            TrackType.COLLECTIONS -> {
                Level(
                    id = id,
                    title = title,
                    track = track,
                    difficulty = difficulty,
                    questionType = questionType,
                    prompt = "What is the result of collection transformation #$levelInTrack?",
                    codeSnippet = "val items = listOf(1, 2, 3, 4)\nval sum = items.map { it + $levelInTrack }.sum()",
                    options = listOf("${(1 + 2 + 3 + 4) + (4 * levelInTrack)}", "${(1 + 2 + 3 + 4)}", "${4 * levelInTrack}", "Error"),
                    codeBlocks = listOf("val items = listOf(1, 2, 3, 4)", "items.map { it + $levelInTrack }", ".sum()"),
                    correctAnswer = "${(1 + 2 + 3 + 4) + (4 * levelInTrack)}",
                    explanation = "Mapping adds $levelInTrack to each of the 4 items, increasing the total sum by ${4 * levelInTrack}.",
                    hint = "Sum of (1+2+3+4) plus 4 times $levelInTrack.",
                    xpReward = 130 + levelInTrack * 2,
                    coinReward = 26 + levelInTrack / 2
                )
            }
            TrackType.OOP_CLASSES -> {
                Level(
                    id = id,
                    title = title,
                    track = track,
                    difficulty = difficulty,
                    questionType = questionType,
                    prompt = "Which keyword makes a class inherit-able (subclassable) in Kotlin?",
                    codeSnippet = "___ class BaseQuest(val id: Int) {\n    open fun execute() {}\n}",
                    options = listOf("open", "abstract", "public", "final"),
                    codeBlocks = listOf("open", "sealed", "inner", "abstract"),
                    correctAnswer = "open",
                    explanation = "By default all classes in Kotlin are `final`. Use the `open` keyword to allow inheritance.",
                    hint = "Classes in Kotlin are final by default; open them up!",
                    xpReward = 140 + levelInTrack * 2,
                    coinReward = 28 + levelInTrack / 2
                )
            }
            TrackType.COROUTINES_ASYNC -> {
                Level(
                    id = id,
                    title = title,
                    track = track,
                    difficulty = difficulty,
                    questionType = questionType,
                    prompt = "Which function awaits the result of an `Deferred<T>` coroutine job?",
                    codeSnippet = "val deferred: Deferred<Int> = async { $levelInTrack * 100 }\nval result = deferred.___()",
                    options = listOf("await", "join", "get", "fetch"),
                    codeBlocks = listOf("await", "join", "yield", "cancel"),
                    correctAnswer = "await",
                    explanation = "`async` returns a `Deferred<T>` value, which can be unwrapped using the suspend function `.await()`.",
                    hint = "Awaiting the deferred async result.",
                    xpReward = 150 + levelInTrack * 2,
                    coinReward = 30 + levelInTrack / 2
                )
            }
            TrackType.GIT_GITHUB -> {
                Level(
                    id = id,
                    title = title,
                    track = track,
                    difficulty = difficulty,
                    questionType = questionType,
                    prompt = "Which Git command synchronizes remote commits into your local branch?",
                    codeSnippet = "\$ git ___ origin main",
                    options = listOf("pull", "push", "clone", "commit"),
                    codeBlocks = listOf("git", "pull", "origin", "main"),
                    correctAnswer = "pull",
                    explanation = "`git pull` fetches changes from the remote repository and immediately merges them into the current branch.",
                    hint = "Pull down remote updates!",
                    xpReward = 140 + levelInTrack * 2,
                    coinReward = 28 + levelInTrack / 2
                )
            }
            TrackType.JETPACK_COMPOSE -> {
                Level(
                    id = id,
                    title = title,
                    track = track,
                    difficulty = difficulty,
                    questionType = questionType,
                    prompt = "Which Modifier method adds padding surrounding a Composable?",
                    codeSnippet = "Box(modifier = Modifier.___(16.dp))",
                    options = listOf("padding", "margin", "inset", "space"),
                    codeBlocks = listOf("padding", "size", "fillMaxSize", "clickable"),
                    correctAnswer = "padding",
                    explanation = "Jetpack Compose uses `Modifier.padding()` to apply spacing around composable components.",
                    hint = "Standard M3 Compose layout padding modifier.",
                    xpReward = 150 + levelInTrack * 2,
                    coinReward = 30 + levelInTrack / 2
                )
            }
            TrackType.REST_APIS -> {
                Level(
                    id = id,
                    title = title,
                    track = track,
                    difficulty = difficulty,
                    questionType = questionType,
                    prompt = "Which HTTP status code signifies a successfully created resource?",
                    codeSnippet = "Response Header: HTTP/1.1 ___ Created",
                    options = listOf("201", "200", "404", "500"),
                    codeBlocks = listOf("201", "200", "301", "403"),
                    correctAnswer = "201",
                    explanation = "HTTP Status 201 Created indicates that the request was successful and a new resource was created.",
                    hint = "201 Created status code.",
                    xpReward = 160 + levelInTrack * 2,
                    coinReward = 32 + levelInTrack / 2
                )
            }
            TrackType.ALGORITHMS_PUZZLES -> {
                val value = levelInTrack * 7
                Level(
                    id = id,
                    title = title,
                    track = track,
                    difficulty = difficulty,
                    questionType = questionType,
                    prompt = "Solve the algorithm puzzle for index #$levelInTrack.",
                    codeSnippet = "fun puzzle(n: Int): Int = if (n <= 1) n else n + puzzle(n - 1)\nprintln(puzzle(3))",
                    options = listOf("6", "3", "5", "9"),
                    codeBlocks = listOf("fun puzzle", "if (n <= 1) n", "else n + puzzle(n - 1)"),
                    correctAnswer = "6",
                    explanation = "`puzzle(3)` computes 3 + puzzle(2) = 3 + (2 + 1) = 6.",
                    hint = "Trace recursive calls: 3 + 2 + 1.",
                    xpReward = 180 + levelInTrack * 2,
                    coinReward = 35 + levelInTrack / 2
                )
            }
        }
    }
}
