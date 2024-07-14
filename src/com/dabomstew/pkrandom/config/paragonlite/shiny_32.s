; r0 = tid (0xABCDEFGH)
; r1 = pid (0xIJKLMNOP)
    
    ldr     r3, =0x0000FFFF
    lsr     r2, r1, #16         ; r2 := 0x0000IJKL
    and     r1, r3              ; r1 := 0x0000MNOP
    and     r3, r0              ; r3 := 0x0000EFGH
    lsr     r0, #16             ; r0 := 0x0000ABCD
    eor     r0, r1
    eor     r0, r2
    eor     r0, r3
    ldr     r1, =2048 ; 2048/65536 = 1/32 odds
    cmp     r0, r1
    bcs     IsNotShiny
    
; IsShiny
    mov     r0, #1
    bx      lr
    
IsNotShiny:
    mov     r0, #0
    bx      lr