; Max size: 72

    push    {r3-r7, lr}
    mov     r6, r0
    mov     r0, #3
    mov     r5, r1
    mov     r4, r2
    bl      Battle::EventVar_GetValue
    cmp     r5, r0
    bne     End
    
    mov     r0, r6
    mov     r1, r5
    bl      Battle::GetPoke
    mov     r6, r0
    
; Get Target HP
    mov     r1, #2 ; 1/x HP
    bl      Battle::DivideMaxHP
    mov     r5, r0
    
; Get current health
    mov     r0, r6
    mov     r1, #13
    bl      Battle::GetPokeStat
    
; Branch if current hp > target
    cmp     r0, r5
    bhi     End
    
; Check move type (passed in as r2 initially)
    mov     r0, #22 ; move type
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     End
    
    mov     r0, #53 ; offensive stat
    mov     r1, #5325 ; 1.3x
    bl      Battle::EventVar_MulValue
    
End:
    pop     {r3-r7, pc}