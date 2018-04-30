// StackTest.vm
// push constant 17
@17
D=A
@SP
A=M
M=D
@SP
M=M+1
// push constant 17
@17
D=A
@SP
A=M
M=D
@SP
M=M+1
// eq
@SP
AM=M-1
D=M
A=A-1
D=M-D
M=0 // true
@IF_0
D;JEQ
@ELSE_0
0;JMP
(IF_0)
@SP
A=M-1
M=-1 // false
(ELSE_0)
// push constant 17
@17
D=A
@SP
A=M
M=D
@SP
M=M+1
// push constant 16
@16
D=A
@SP
A=M
M=D
@SP
M=M+1
// eq
@SP
AM=M-1
D=M
A=A-1
D=M-D
M=0 // true
@IF_1
D;JEQ
@ELSE_1
0;JMP
(IF_1)
@SP
A=M-1
M=-1 // false
(ELSE_1)
// push constant 16
@16
D=A
@SP
A=M
M=D
@SP
M=M+1
// push constant 17
@17
D=A
@SP
A=M
M=D
@SP
M=M+1
// eq
@SP
AM=M-1
D=M
A=A-1
D=M-D
M=0 // true
@IF_2
D;JEQ
@ELSE_2
0;JMP
(IF_2)
@SP
A=M-1
M=-1 // false
(ELSE_2)
// push constant 892
@892
D=A
@SP
A=M
M=D
@SP
M=M+1
// push constant 891
@891
D=A
@SP
A=M
M=D
@SP
M=M+1
// lt
@SP
AM=M-1
D=M
A=A-1
D=M-D
M=0 // true
@IF_3
D;JLT
@ELSE_3
0;JMP
(IF_3)
@SP
A=M-1
M=-1 // false
(ELSE_3)
// push constant 891
@891
D=A
@SP
A=M
M=D
@SP
M=M+1
// push constant 892
@892
D=A
@SP
A=M
M=D
@SP
M=M+1
// lt
@SP
AM=M-1
D=M
A=A-1
D=M-D
M=0 // true
@IF_4
D;JLT
@ELSE_4
0;JMP
(IF_4)
@SP
A=M-1
M=-1 // false
(ELSE_4)
// push constant 891
@891
D=A
@SP
A=M
M=D
@SP
M=M+1
// push constant 891
@891
D=A
@SP
A=M
M=D
@SP
M=M+1
// lt
@SP
AM=M-1
D=M
A=A-1
D=M-D
M=0 // true
@IF_5
D;JLT
@ELSE_5
0;JMP
(IF_5)
@SP
A=M-1
M=-1 // false
(ELSE_5)
// push constant 32767
@32767
D=A
@SP
A=M
M=D
@SP
M=M+1
// push constant 32766
@32766
D=A
@SP
A=M
M=D
@SP
M=M+1
// gt
@SP
AM=M-1
D=M
A=A-1
D=M-D
M=0 // true
@IF_6
D;JGT
@ELSE_6
0;JMP
(IF_6)
@SP
A=M-1
M=-1 // false
(ELSE_6)
// push constant 32766
@32766
D=A
@SP
A=M
M=D
@SP
M=M+1
// push constant 32767
@32767
D=A
@SP
A=M
M=D
@SP
M=M+1
// gt
@SP
AM=M-1
D=M
A=A-1
D=M-D
M=0 // true
@IF_7
D;JGT
@ELSE_7
0;JMP
(IF_7)
@SP
A=M-1
M=-1 // false
(ELSE_7)
// push constant 32766
@32766
D=A
@SP
A=M
M=D
@SP
M=M+1
// push constant 32766
@32766
D=A
@SP
A=M
M=D
@SP
M=M+1
// gt
@SP
AM=M-1
D=M
A=A-1
D=M-D
M=0 // true
@IF_8
D;JGT
@ELSE_8
0;JMP
(IF_8)
@SP
A=M-1
M=-1 // false
(ELSE_8)
// push constant 57
@57
D=A
@SP
A=M
M=D
@SP
M=M+1
// push constant 31
@31
D=A
@SP
A=M
M=D
@SP
M=M+1
// push constant 53
@53
D=A
@SP
A=M
M=D
@SP
M=M+1
// add
@SP
AM=M-1
D=M
A=A-1
M=M+D
// push constant 112
@112
D=A
@SP
A=M
M=D
@SP
M=M+1
// sub
@SP
AM=M-1
D=M
A=A-1
M=M-D
// neg
@SP
A=M-1
M=-M
// and
@SP
AM=M-1
D=M
A=A-1
M=M&D
// push constant 82
@82
D=A
@SP
A=M
M=D
@SP
M=M+1
// or
@SP
AM=M-1
D=M
A=A-1
M=M|D
// not
@SP
A=M-1
M=!M
