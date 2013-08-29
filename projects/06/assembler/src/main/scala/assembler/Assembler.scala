package assembler

import java.io.File

object Assembler {

  def isInteger(s: String) = s.forall(Character.isDigit(_))

  def assemble(file: File): String = {
    val code = new Code()
    val symbolTable = new SymbolTable()

    // first pass generate symbol address
    var address = 0
    var availableRamAddress = 16
    val firstPassParser = new Parser(file)
    while(firstPassParser.hasMoreCommands) {
      firstPassParser.advance()
      firstPassParser.commandType match {
        case LCommand => symbolTable.addEntry(firstPassParser.symbol, address)
        case _ => address += 1
      }
    }

    // second pass spit out assembly
    val assemblyCode = new StringBuilder()
    val parser = new Parser(file)
    while (parser.hasMoreCommands) {
      parser.advance()
      val instruction = parser.commandType match {
        case ACommand =>
          val address = if (!isInteger(parser.symbol)) {
            if (!symbolTable.contains(parser.symbol)) {
              symbolTable.addEntry(parser.symbol, availableRamAddress)
              availableRamAddress += 1
            }
            symbolTable.getAddress(parser.symbol)
          } else {
            Integer.valueOf(parser.symbol)
          }
          code.codeAInstruction(address)
        case CCommand => code.codeCInstruction(parser.comp, parser.dest, parser.jump)
        case _ => null
      }
      if (instruction != null)
        assemblyCode.append(instruction).append("\n")
    }
    assemblyCode.toString()
  }

  def main(args: Array[String]) {
    val src = new File("/Users/pmenon/Desktop/nand2tetris/projects/06/pong/Pong.asm")
    val assembly = assemble(src)
    println(assembly)
  }

}
