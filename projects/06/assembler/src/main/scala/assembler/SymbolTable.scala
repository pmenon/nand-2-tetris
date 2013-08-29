package assembler

import java.util

class SymbolTable {
  private val symbols = new util.HashMap[String, Integer]()

  initSymbolTable()

  def initSymbolTable() {
    0.to(15).foreach(i => symbols.put("R%d".format(i), i))
    symbols.put("SCREEN", 16384)
    symbols.put("KBD", 24576)
    symbols.put("SP", 0)
    symbols.put("LCL", 1)
    symbols.put("ARG", 2)
    symbols.put("THIS", 3)
    symbols.put("THAT", 4)
    symbols.put("WRITE", 18)
    symbols.put("END", 22)
  }

  def addEntry(symbol: String, address: Int) {
    symbols.put(symbol, address)
  }

  def contains(symbol: String) = symbols.containsKey(symbol)

  def getAddress(symbol: String) = symbols.get(symbol)
}
