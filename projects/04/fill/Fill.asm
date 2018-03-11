// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

// R0 will hold color bits
(SETW)
    @R0 
    M=0
    @DRAW
    0;JMP
    
(SETB)
    @R0 
    M=-1
    @DRAW
    0;JMP

(LOOP)
    // init i for drawing loop
    @i
    M=0

    // if no key is pressed, set bits to white and draw
    @KBD
    D=M
    @SETW
    D;JEQ
    // else to black
    @SETB
    0;JMP
    
    (DRAW)
        // current pixel address
        @i
        D=M
        @SCREEN
        D=D+A
        
        // store for later
        @R1
        M=D
        
        @KBD
        D=A-D // last address of screen
        @LOOP
        D;JLE
        
        // write color bits
        @R0
        D=M
        @R1
        A=M
        M=D
        
        @i
        M=M+1
        
        @DRAW
        0;JMP
    
