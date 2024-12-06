; r0 := array
; r1 := array_count
; r2 := element_size
; r3 := search_value

    push    {r4-r6, lr}
    mov     r6, r0
    sub     r4, r1, #1
    mov     r7, r2
    mov     r5, r3
    
    mov     r3, #0 ; low
    
    cmp     r3, r4
    bhi     ReturnZero
    
Loop_Start:
    sub     r0, r4, r3 ; mid := high - low
    lsr     r0, #1 ; mid /= 2
    add     r0, r3 ; mid += low
    
    mov     r1, r0
    mul     r1, r7
    ldrh    r1, [r6, r1]
    cmp     r5, r1
    beq     Return
    bgt     Loop_Right
    
Loop_Left:
    sub     r4, r0, #1
    b       Loop_End
    
Loop_Right:
    add     r3, r0, #1
    
Loop_End:
    cmp     r3, r4
    bhi     ReturnZero
    
    
ReturnZero:
    mov     r0, #0
    
Return:
    pop     {r4-r6, pc}