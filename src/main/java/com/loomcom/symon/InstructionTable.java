/*
 * Copyright (c) 2016 Seth J. Morabito <web@loomcom.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.loomcom.symon;

public interface InstructionTable {

	/**
	 * Enumeration of Addressing Modes.
	 */
	enum Mode {
		ACC("Accumulator", 1),
		IMM("Immediate", 2),
		IMW("Immediate (word)", 3),
		ZPG("Zeropage", 2),
		ZPX("Zeropage, X-indexed", 2),
		ZPY("Zeropage, Y-indexed", 2),
		ABS("Absolute", 3),
		ABX("Absolute, X-indexed", 3),
		ABY("Absolute, Y-indexed", 3),
		IMP("Implied", 1),
		REL("Relative", 2),
		REW("Relative (Word)", 3),
		XIN("X-indexed Indirect", 2),
		INY("Indirect, Y-indexed", 2),
		IND("Absolute Indirect", 3),
		IAX("Absolute Indirect, X-indexed", 3),
		IZP("Zeropage Indirect", 2),
		ZPR("Zeropage Relative", 3),
		SAY("Stack Relative Indirect, Y-indexed", 2);

		private final String text;
		private final int length;

		Mode(final String text, final int length) {
			this.text = text;
			this.length = length;
		}

		public int getLength() {
			return length;
		}

		@Override
		public String toString() {
			return text;
		}
	}

	// 65CE02 opcodes.

	/**
	 * Instruction opcode names.
	 */
	String[] opcodeNames = {
		"BRK", "ORA", "CLE", "SEE", "TSB", "ORA", "ASL", "RMB0", "PHP", "ORA", "ASL", "TSY", "TSB", "ORA", "ASL", "BBR0",
		"BPL", "ORA", "ORA", "BPL", "TRB", "ORA", "ASL", "RMB1", "CLC", "ORA", "INC", "INZ", "TRB", "ORA", "ASL", "BBR1",
		"JSR", "AND", "JSR", "JSR", "BIT", "AND", "ROL", "RMB2", "PLP", "AND", "ROL", "TYS", "BIT", "AND", "ROL", "BBR2",
		"BMI", "AND", "AND", "BMI", "BIT", "AND", "ROL", "RMB3", "SEC", "AND", "DEC", "DEZ", "BIT", "AND", "ROL", "BBR3",
		"RTI", "EOR", "NEG", "ASR", "ASR", "EOR", "LSR", "RMB4", "PHA", "EOR", "LSR", "TAZ", "JMP", "EOR", "LSR", "BBR4",
		"BVC", "EOR", "EOR", "BVC", "ASR", "EOR", "LSR", "RMB5", "CLI", "EOR", "PHY", "TAB", "AUG", "EOR", "LSR", "BBR5",
		"RTS", "ADC", "RTN", "BSR", "STZ", "ADC", "ROR", "RMB6", "PLA", "ADC", "ROR", "TZA", "JMP", "ADC", "ROR", "BBR6",
		"BVS", "ADC", "ADC", "BVS", "STZ", "ADC", "ROR", "RMB7", "SEI", "ADC", "PLY", "TBA", "JMP", "ADC", "ROR", "BBR7",
		"BRA", "STA", "STA", "BRA", "STY", "STA", "STX", "SMB0", "DEY", "BIT", "TXA", "STY", "STY", "STA", "STX", "BBS0",
		"BCC", "STA", "STA", "BBC", "STY", "STA", "STX", "SMB1", "TYA", "STA", "TXS", "STZ", "STZ", "STA", "STZ", "BBS1",
		"LDY", "LDA", "LDX", "LDZ", "LDY", "LDA", "LDX", "SMB2", "TAY", "LDA", "TAX", "LDZ", "LDY", "LDA", "LDX", "BBS2",
		"BCS", "LDA", "LDA", "BCS", "LDY", "LDA", "LDX", "SMB3", "CLV", "LDA", "TSX", "LDZ", "LDY", "LDA", "LDX", "BBS3",
		"CPY", "CMP", "CPZ", "DEW", "CPY", "CMP", "DEC", "SMB4", "INY", "CMP", "DEX", "ASW", "CPY", "CMP", "DEC", "BBS4",
		"BNE", "CMP", "CMP", "BNE", "CPZ", "CMP", "DEC", "SMB5", "CLD", "CMP", "PHX", "PHZ", "CPZ", "CMP", "DEC", "BBS5",
		"CPX", "SBC", "LDA", "INW", "CPX", "SBC", "INC", "SMB6", "INX", "SBC", "NOP", "ROW", "CPX", "SBC", "INC", "BBS6",
		"BEQ", "SBC", "SBC", "BEQ", "PHW", "SBC", "INC", "SMB7", "SED", "SBC", "PLX", "PLZ", "PHW", "SBC", "INC", "BBS7"
	};

	/**
	 * Instruction addressing modes.
	 */
	Mode[] instructionModes = {
		Mode.IMP,Mode.XIN,Mode.IMP,Mode.IMP,Mode.ZPG,Mode.ZPG,Mode.ZPG,Mode.ZPG, // 0x00-0x07
		Mode.IMP,Mode.IMM,Mode.ACC,Mode.IMP,Mode.ABS,Mode.ABS,Mode.ABS,Mode.ZPR, // 0x08-0x0f
		Mode.REL,Mode.INY,Mode.IZP,Mode.REW,Mode.ZPG,Mode.ZPX,Mode.ZPX,Mode.ZPG, // 0x10-0x17
		Mode.IMP,Mode.ABY,Mode.ACC,Mode.IMP,Mode.ABS,Mode.ABX,Mode.ABX,Mode.ZPR, // 0x18-0x1f
		Mode.ABS,Mode.XIN,Mode.IND,Mode.IAX,Mode.ZPG,Mode.ZPG,Mode.ZPG,Mode.ZPG, // 0x20-0x27
		Mode.IMP,Mode.IMM,Mode.ACC,Mode.IMP,Mode.ABS,Mode.ABS,Mode.ABS,Mode.ZPR, // 0x28-0x2f
		Mode.REL,Mode.INY,Mode.IZP,Mode.REW,Mode.ZPX,Mode.ZPX,Mode.ZPX,Mode.ZPG, // 0x30-0x37
		Mode.IMP,Mode.ABY,Mode.ACC,Mode.IMP,Mode.ABX,Mode.ABX,Mode.ABX,Mode.ZPR, // 0x38-0x3f
		Mode.IMP,Mode.XIN,Mode.ACC,Mode.ZPG,Mode.ZPG,Mode.ZPG,Mode.ZPG,Mode.ZPG, // 0x40-0x47
		Mode.IMP,Mode.IMM,Mode.ACC,Mode.IMP,Mode.ABS,Mode.ABS,Mode.ABS,Mode.ZPR, // 0x48-0x4f
		Mode.REL,Mode.INY,Mode.IZP,Mode.REW,Mode.ZPX,Mode.ZPX,Mode.ZPX,Mode.ZPG, // 0x50-0x57
		Mode.IMP,Mode.ABY,Mode.IMP,Mode.IMP,Mode.IMP,Mode.ABX,Mode.ABX,Mode.ZPR, // 0x58-0x5f
		Mode.IMP,Mode.XIN,Mode.IMM,Mode.REW,Mode.ZPG,Mode.ZPG,Mode.ZPG,Mode.ZPG, // 0x60-0x67
		Mode.IMP,Mode.IMM,Mode.ACC,Mode.IMP,Mode.IND,Mode.ABS,Mode.ABS,Mode.ZPR, // 0x68-0x6f
		Mode.REL,Mode.INY,Mode.IZP,Mode.REW,Mode.ZPX,Mode.ZPX,Mode.ZPX,Mode.ZPG, // 0x70-0x77
		Mode.IMP,Mode.ABY,Mode.IMP,Mode.IMP,Mode.IAX,Mode.ABX,Mode.ABX,Mode.ZPR, // 0x78-0x7f
		Mode.REL,Mode.XIN,Mode.SAY,Mode.REW,Mode.ZPG,Mode.ZPG,Mode.ZPG,Mode.ZPG, // 0x80-0x87
		Mode.IMP,Mode.IMM,Mode.IMP,Mode.ABX,Mode.ABS,Mode.ABS,Mode.ABS,Mode.ZPR, // 0x88-0x8f
		Mode.REL,Mode.INY,Mode.IZP,Mode.REW,Mode.ZPX,Mode.ZPX,Mode.ZPY,Mode.ZPG, // 0x90-0x97
		Mode.IMP,Mode.ABY,Mode.IMP,Mode.ABY,Mode.ABS,Mode.ABX,Mode.ABX,Mode.ZPR, // 0x98-0x9f
		Mode.IMM,Mode.XIN,Mode.IMM,Mode.IMM,Mode.ZPG,Mode.ZPG,Mode.ZPG,Mode.ZPG, // 0xa0-0xa7
		Mode.IMP,Mode.IMM,Mode.IMP,Mode.ABS,Mode.ABS,Mode.ABS,Mode.ABS,Mode.ZPR, // 0xa8-0xaf
		Mode.REL,Mode.INY,Mode.IZP,Mode.REW,Mode.ZPX,Mode.ZPX,Mode.ZPY,Mode.ZPG, // 0xb0-0xb7
		Mode.IMP,Mode.ABY,Mode.IMP,Mode.ABX,Mode.ABX,Mode.ABX,Mode.ABY,Mode.ZPR, // 0xb8-0xbf
		Mode.IMM,Mode.XIN,Mode.IMM,Mode.ZPG,Mode.ZPG,Mode.ZPG,Mode.ZPG,Mode.ZPG, // 0xc0-0xc7
		Mode.IMP,Mode.IMM,Mode.IMP,Mode.ABS,Mode.ABS,Mode.ABS,Mode.ABS,Mode.ZPR, // 0xc8-0xcf
		Mode.REL,Mode.INY,Mode.IZP,Mode.REW,Mode.ZPX,Mode.ZPX,Mode.ZPX,Mode.ZPG, // 0xd0-0xd7
		Mode.IMP,Mode.ABY,Mode.IMP,Mode.IMP,Mode.ABS,Mode.ABX,Mode.ABX,Mode.ZPR, // 0xd8-0xdf
		Mode.IMM,Mode.XIN,Mode.SAY,Mode.ZPG,Mode.ZPG,Mode.ZPG,Mode.ZPG,Mode.ZPG, // 0xe0-0xe7
		Mode.IMP,Mode.IMM,Mode.IMP,Mode.ABS,Mode.ABS,Mode.ABS,Mode.ABS,Mode.ZPR, // 0xe8-0xef
		Mode.REL,Mode.INY,Mode.IZP,Mode.IMW,Mode.ZPX,Mode.ZPX,Mode.ZPX,Mode.ZPG, // 0xf0-0xf7
		Mode.IMP,Mode.ABY,Mode.IMP,Mode.ABS,Mode.ABS,Mode.ABX,Mode.ABX,Mode.ZPR  // 0xf8-0xff
	};

	/**
	 * Number of clock cycles required for each instruction
	 */
	int[] instructionClocks = {
		2, 2, 1, 1, 2, 2, 2, 2, 1, 2, 1, 1, 3, 3, 3, 3,
		2, 2, 2, 3, 2, 2, 2, 2, 1, 3, 1, 1, 3, 3, 3, 3,
		3, 2, 3, 3, 2, 2, 2, 2, 1, 2, 1, 1, 3, 3, 3, 3,
		2, 2, 2, 3, 2, 2, 2, 2, 1, 3, 1, 1, 3, 3, 3, 3,
		1, 2, 1, 1, 2, 2, 2, 2, 1, 2, 1, 1, 3, 3, 3, 3,
		2, 2, 2, 3, 2, 2, 2, 2, 1, 3, 1, 1, 4, 3, 3, 3,
		1, 2, 2, 3, 2, 2, 2, 2, 1, 2, 1, 1, 3, 3, 3, 3,
		2, 2, 2, 3, 2, 2, 2, 2, 1, 3, 1, 1, 3, 3, 3, 3,
		2, 2, 2, 3, 2, 2, 2, 2, 1, 2, 1, 3, 3, 3, 3, 3,
		2, 2, 2, 3, 2, 2, 2, 2, 1, 3, 1, 3, 3, 3, 3, 3,
		2, 2, 2, 2, 2, 2, 2, 2, 1, 2, 1, 3, 3, 3, 3, 3,
		2, 2, 2, 3, 2, 2, 2, 2, 1, 3, 1, 3, 3, 3, 3, 3,
		2, 2, 2, 2, 2, 2, 2, 2, 1, 2, 1, 3, 3, 3, 3, 3,
		2, 2, 2, 3, 2, 2, 2, 2, 1, 3, 1, 1, 3, 3, 3, 3,
		2, 2, 2, 2, 2, 2, 2, 2, 1, 2, 1, 3, 3, 3, 3, 3,
		2, 2, 2, 3, 3, 2, 2, 2, 1, 3, 1, 1, 3, 3, 3, 3
	};

}
