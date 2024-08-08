    push    {r3-r5, lr}
    mov     r4, r0
    mov     r5, r1
    bl      Battle::CheckCondition
    cmp     r0, #0
    beq     ReturnZero
    cmp     r5, #10
    bhi     ReturnZero
    
    #SWITCH r5
    #CASE   ReturnZero
    #CASE   ReturnZero
    #CASE   ReturnZero
    #CASE   ReturnZero      ; Freeze
    #CASE   Burn            ; Burn
    #CASE   PoisonAndToxic  ; Poison/Toxic
    #CASE   ReturnZero      
    #CASE   ReturnZero
    #CASE   ReturnZero
    #CASE   Nightmare       ; Nightmare
    #CASE   Curse           ; Curse
    
PoisonAndToxic:
    lsl     r0, r5, #2
    add     r0, r4
    add     r0, #28
    ldr     r0, [r0]
    bl      Battle::GetTurnsFromConditionData
    cmp     r0, #0
    beq     Poison
    
Toxic:
    mov     r0, r4
    mov     r1, #16 ; 1/16
    bl      Battle::DivideMaxHPZeroCheck
    add     r1, r4, r5
    add     r1, #172
    ldrb    r1, [r1]
    mul     r0, r1
    pop     {r3-r5, pc}
    
Poison:
    mov     r0, r4
    mov     r1, #8 ; 1/8
    bl      Battle::DivideMaxHPZeroCheck
    pop     {r3-r5, pc}
    
Burn:
    mov     r0, r4
    mov     r1, #16 ; 1/16
    bl      Battle::DivideMaxHPZeroCheck
    pop     {r3-r5, pc}

Nightmare:
    mov     r0, r4
    mov     r1, #2 ; sleep
    bl      Battle::CheckCondition
    cmp     r0, #0
    beq     ReturnZero
    
    mov     r0, r4
    mov     r1, #4 ; 1/4
    bl      Battle::DivideMaxHPZeroCheck
    pop     {r3-r5, pc}
    
Curse:
    mov     r0, r4
    mov     r1, #4 ; 1/4
    bl      Battle::DivideMaxHPZeroCheck
    pop     {r3-r5, pc}
    
ReturnZero:
    mov     r0, #0
    pop     {r3-r5, pc}