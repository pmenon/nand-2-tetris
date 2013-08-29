package assembler

import java.io.File
import scala.io.Source

sealed trait CommandType { override def toString: String = this.getClass.getSimpleName }
object ACommand extends CommandType
object CCommand extends CommandType
object LCommand extends CommandType

class Parser(src: File) {
  def this(src: String) = this(new File(src))

  if (src == null || !src.exists()) throw new RuntimeException("No existing source file provided for parsing ...")

  private val source = Source.fromFile(src).getLines().toIterator.filter(line => !line.startsWith("//") && line.length > 0)

  private var line: String = null
  private var _commandType: CommandType = null
  private var _symbol: Option[String] = None
  private var _dest: Option[String] = None
  private var _comp: Option[String] = None
  private var _jump: Option[String] = None

  private def reset() {
    _commandType = null
    _symbol = None
    _dest = None
    _comp = None
    _jump = None
  }

  private def parseNextLine() {
    if (!source.hasNext)
      return

    // reset state variables
    reset()

    line = source.next().trim()
    if(line.startsWith("@")) {
      parseAInstruction(line)
    } else if (line.contains("=") || line.contains(";")) {
      parseCInstruction(line)
    } else if (line.startsWith("(")) {
      parseLInstruction(line)
    } else {
      throw new RuntimeException("Cannot parse line [\"%s\"] ...".format(line))
    }
  }

  def parseAInstruction(line: String) {
    _commandType = ACommand
    _symbol = Some(line.substring(1, line.length).trim())
  }

  def parseCInstruction(line: String) {
    // Multiple formats:
    // dest=comp;jump
    // dest=comp
    // comp;jump
    // comp

    _commandType = CCommand
    val eqIdx = line.indexOf("=")
    val semiIdx = line.indexOf(";")
    val lineEnd = line.indexWhere(Character.isWhitespace(_), if (semiIdx > 0) semiIdx else if (eqIdx > 0) eqIdx else 0)
    _dest = if (eqIdx > 0) Some(line.substring(0, eqIdx).trim()) else None
    _comp = Some(line.substring(if (eqIdx > 0) eqIdx + 1 else 0, if (semiIdx > 0) semiIdx else if (lineEnd > 0) lineEnd else line.length).trim())
    _jump = if (semiIdx > 0) Some(line.substring(semiIdx + 1, if (lineEnd > 0) lineEnd else line.length)) else None
  }

  def parseLInstruction(line: String) {
    _commandType = LCommand
    _symbol = Some(line.substring(1, line.indexOf(")")))
  }

  def hasMoreCommands : Boolean = {
    if (line == null)
      parseNextLine()
    line != null
  }

  def advance() = {
    if (!hasMoreCommands)
      None
    else {
      val currLine = line
      line = null
      Some(currLine)
    }
  }

  // API
  def commandType = _commandType
  def symbol = _symbol.getOrElse(throw new RuntimeException("No symbol for type %s on command [%s]".format(commandType, line)))
  def dest = _dest.getOrElse("null") // destination is optional in C-Instructions
  def comp = _comp.getOrElse(throw new RuntimeException("No comp for type %s on command [%s]".format(commandType, line)))
  def jump = _jump.getOrElse("null") // jump is optional in C-Instructions
}
