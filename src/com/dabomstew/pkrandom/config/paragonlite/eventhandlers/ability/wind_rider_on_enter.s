#DEFINE StatOffset 0x04
#DEFINE BoostAmountOffset 0x0C 

    push    {r3-r7, lr}
    mov     r0, #2
    mov     r5, r1
    mov     r4, r2
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     End
    
    mov     r0, r4
    bl      Battle::GetPokeId
    mov     r1, r0
    ldr     r0, [r4]
    mov     r2, #4 ; Tailwind
    bl      Battle::PokeSideHasCondition
    cmp     r0, #0
    beq     End
    
; Apply boost
    mov     r0, r5
    mov     r1, #2
    mov     r2, r4
    bl      Battle::Handler_PushRun
    
    mov     r0, r5
    mov     r1, #14
    mov     r2, r4
    bl      Battle::Handler_PushWork
    
    mov     r1, r0
    mov     r6, #1
    strb    r6, [r1, #15]
    strb    r4, [r1, #16]
    strb    r6, [r1, #14]
    mov     r0, #1 ; attack, +1
    str     r0, [r1, #StatOffset]
    strb    r0, [r1, #BoostAmountOffset]
    mov     r0, r5
    bl      Battle::Handler_PopWork
    
    mov     r0, r5
    mov     r1, #3
    mov     r2, r4
    bl      Battle::Handler_PushRun
    
End:
    pop     {r3-r7, pc}