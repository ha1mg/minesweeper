import kotlin.random.Random

fun main() {
    println("How many mines do you want on the field?")
    Minesweeper(readln().toInt())
}

class Minesweeper(_mines: Int) {
    var mines: Int = _mines
        set(value) {
            if (value < 0) println("Number of mines can't be negative") else field = value
        }

    val originalField = MutableList(12) { MutableList(12) { ' ' } } //create 2D list sizeR by SizeC with '.'
    var outputField = MutableList(12) { MutableList(12) { ' ' } }

    var numExploredCells = 0
    var numEmptyCells = 0

    var quantityNumbers = 0
    var quantityOutputNumbers = 0

    var outputMines = 0

    init {
        createFields()
    }

    fun placeNums(row: Int,col: Int) {
        for (r in maxOf((row - 1), 2)..minOf((row + 1), 10)) {
            for (c in maxOf((col - 1), 2)..minOf((col + 1), 10)) {
                when (originalField[r][c]) {
                    '.' -> {
                        originalField[r][c] = '1'
                        quantityNumbers++
                    }
                    in '1'..'7' -> originalField[r][c]++
                }
            }
        }
    }

    fun createEmptyField(field: MutableList<MutableList<Char>>) {
        for (row in field.indices) {
            for (col in field[row].indices) {
                when {
                    col == 1 || col == 11 -> field[row][col] = '|'
                    row == 1 || row == 11 -> field[row][col] = '-'
                    row in 2..11 && col == 0 ->  field[row][col] = (row - 1).digitToChar()
                    col in 2..11 && row == 0 ->  field[row][col] = (col - 1).digitToChar()
                    row in 2..11 && col in 2..11 -> field[row][col] = '.'
                }
            }
        }
    }

    fun printField() {
        for (row in outputField) {
            for (cell in row) {
                print(cell)
            }
            println()
        }
    }

    fun exploredCells(row: Int,col: Int) {
        outputField[row][col] = '/'
        numExploredCells++
        for (r in maxOf((row - 1), 2)..minOf((row + 1), 10)) {
            for (c in maxOf((col - 1), 2)..minOf((col + 1), 10)) {
                when  {
                    originalField[r][c] == '.' && outputField[r][c] != '/' -> exploredCells(r, c)
                    originalField[r][c] in '1'..'8' && outputField[r][c] !in '1'..'8' -> {
                        outputField[r][c] = originalField[r][c]
                        quantityOutputNumbers++
                    }
                }
            }
        }
    }

    fun createFields() {
        createEmptyField(originalField)
        var i = mines
        while (i > 0) {
            val row = Random.nextInt(2,11)
            val col = Random.nextInt(2, 11)
            if (originalField[row][col] != 'X') {
                if (originalField[row][col] in '1'..'8') {
                    quantityNumbers--
                }
                originalField[row][col] = 'X'
                placeNums(row, col)
                i--
            }
        }

        for (row in originalField) {
            for (cell in row) {
               if (cell == '.') numEmptyCells++
            }
        }

        createEmptyField(outputField)
        start()
    }

    fun start() {
        var firstMove = true
        do {
            printField()
            if (numExploredCells == numEmptyCells && quantityOutputNumbers == quantityNumbers || outputMines == mines) {
                println("Congratulations! You found all the mines!")
                break
            }
            println("Set/unset mines marks or claim a cell as free:")
            val (a, b, e) = readln().split(' ')
            val col = a.toInt() + 1
            val row = b.toInt() + 1

            when (e){
                "mine" -> {
                    when (outputField[row][col]) {
                        '.' -> {
                            outputField[row][col] = '*'
                            if (originalField[row][col] == 'X') outputMines++
                            else outputMines--
                        }
                        in '1'..'7' -> println("There is a number here!")
                        '*' -> {
                            outputField[row][col] = '.'
                            if (originalField[row][col] == 'X') outputMines--
                            else outputMines++
                        }
                    }
                }
                "free" -> {
                    when (originalField[row][col]) {
                        'X' -> {
                            //if (firstMove) continue
                            for (r in originalField.indices) {
                                for (c in originalField[r].indices){
                                    if (originalField[r][c] == 'X') outputField[r][c] = 'X'
                                }
                            }
                            printField()
                            println("You stepped on a mine and failed!")
                            break
                        }
                        in '1'..'7' -> {
                            outputField[row][col] = originalField[row][col]
                            quantityOutputNumbers++
                        }
                        '.' -> {
                            exploredCells(row, col)
                        }
                    }
                }
            }
            firstMove = false
        } while (true)
    }
}
