package assembler

import scala.collection.immutable.HashMap

class Code {
   private val destCodes = HashMap[String, String](
     "null" -> "000",
     "M" -> "001",
     "D" -> "010",
     "MD" -> "011",
     "A" -> "100",
     "AM" -> "101",
     "AD" -> "110",
     "AMD" -> "11"
   )

  private val compCodes = HashMap[String, String](
    "0" -> "0101010",
    "1" -> "0111111",
    "-1" -> "0111010",
    "D" -> "0001100",
    "A" -> "0110000",
    "M" -> "1110000",
    "!D" -> "0001101",
    "!A" -> "0110001",
    "!M" -> "1110001",
    "-D" -> "0001111",
    "-A" -> "0110011",
    "-M" -> "1110011",
    "D+1" -> "0011111",
    "A+1" -> "0110111",
    "M+1" -> "1110111",
    "D-1" -> "0001110",
    "A-1" -> "0110010",
    "M-1" -> "1110010",
    "D+A" -> "0110010",
    "D+M" -> "10110010",
    "D-A" -> "0010011",
    "D-M" -> "1010011",
    "A-D" -> "0000111",
    "M-D" -> "1000111",
    "D&A" -> "0000000",
    "D&M" -> "1000000",
    "D|A" -> "0010101",
    "D|M" -> "1010101"
  )

  private val jumpCodes = HashMap[String, String](
    "null" -> "000",
    "JGT" -> "001",
    "JEQ" -> "010",
    "JGE" -> "011",
    "JLT" -> "100",
    "JNE" -> "101",
    "JLE" -> "110",
    "JMP" -> "111"
  )

  def dest(mnemonic: String): String = destCodes(mnemonic)
  def comp(mnemonic: String): String = compCodes(mnemonic)
  def jump(mnemonic: String): String = jumpCodes(mnemonic)

  def codeAInstruction(address: Int) = "0%15s".format(address.toBinaryString).replace(" ", "0")
  def codeCInstruction(compS: String, destS: String, jumpS: String) = "111%s%s%s".format(comp(compS), dest(destS), jump(jumpS))
}
