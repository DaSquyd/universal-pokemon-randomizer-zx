    push    {r4-r5, lr}
    mov     r4, r0
    mov     r5, r1
    
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #VAR_MoveType
    bl      Battle::EventVar_GetValue
    cmp     r0, #TYPE_Normal
    bne     Return
    
    mov     r0, #VAR_MoveId
    bl      Battle::EventVar_GetValue
    
    cmp     r0, #165 ; struggle
    beq     Return
    
    mov     r1, #237 ; Hidden Power
    cmp     r0, r1
    beq     Return
    
    add     r1, #(311 - 237) ; Weather Ball
    cmp     r0, r1
    beq     Return
    
    add     r1, #(363 - 311) ; Natural Gift
    cmp     r0, r1
    beq     Return
    
    add     r1, #(449 - 363) ; Judgment
    cmp     r0, r1
    beq     Return
    
    add     r1, #(546 - 449) ; Techno Blast
    cmp     r0, r1
    beq     Return
    
    mov     r0, #VAR_MoveType
    mov     r1, r5
    bl      Battle::EventVar_RewriteValue
    
Return:
    pop     {r4-r5, pc}
