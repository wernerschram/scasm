package assembler.x86.operands

import assembler.x86.ParameterPosition
import assembler.x86.ProcessorMode
import assembler.x86.RexExtendedRequirement

trait Operand {
  def getRexRequirements(position: ParameterPosition): List[RexExtendedRequirement]
  def canInteractWith(register: Operand): Boolean = true
  def isValidForMode(processorMode: ProcessorMode): Boolean = true
}