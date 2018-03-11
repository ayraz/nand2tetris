// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)

// i = 0
// a = R0
// b = R1
// product = 0;
// while (i < b) {
//     product += a;
// }
// R2 = A

@i
M=0

@sum
M=0

(LOOP)
    @i
    D=M
    @R0
    D=M-D
    @END
    D;JLE

    @R1
    D=M
    @sum
    M=D+M
    
    @i
    M=M+1
    
    @LOOP
    0;JMP
(END)
    @sum
    D=M
    @R2
    M=D
    @END
    0;JMP