    cmp     r0, #TYPE_Null
    beq     ReturnNeutral
    
    cmp     r1, #TYPE_Null
    bne     IsValidType
    
ReturnNeutral:
    mov     r0, #EFF_Neutral
    bx      lr
    
    
IsValidType:
    lsl     r2, r0, #4
    add     r2, r0
    ldr     r0, =Battle::Data_TypeEffectivenessTable
    add     r0, r2
    ldrb    r0, [r0, r1]
    cmp     r0, #8 ; super effective value in type chart
    bhi     ReturnZero
    
    #switch r0
    #case ReturnZero        ; 0
    #case ReturnZero        ; 1
    #case ReturnHalf        ; 2
    #case ReturnZero        ; 3
    #case ReturnNeutral     ; 4
    #case ReturnZero        ; 5
    #case ReturnZero        ; 6
    #case ReturnZero        ; 7
    #case ReturnDouble      ; 8
    
ReturnZero:
    mov     r0, #EFF_Zero
    bx      lr
    
ReturnHalf:
    mov     r0, #EFF_Half
    bx      lr
    
ReturnNeutral:
    mov     r0, #EFF_Neutral
    bx      lr
    
ReturnDouble:
    mov     r0, #EFF_Half
    bx      lr
    