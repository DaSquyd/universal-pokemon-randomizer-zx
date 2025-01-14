; The original Adaptability is now called "Specialized".
; This is the new version where non-STAB moves get a boost of 1.3x

    push    {r3-r6, lr}
    mov     r0, #3
    mov     r5, r2
    mov     r6, r1
    mov     r4, #3
    bl      Battle::EventVar_GetValue
    cmp     r5, r0
    bne     End
    
    mov     r0, #0x16 ; move type
    bl      Battle::EventVar_GetValue
    mov     r4, r0

    mov     r0, r6
    mov     r1, r5
    bl      Battle::GetPoke

    mov     r1, r4
    bl      Battle::PokeHasType
    cmp     r0, #0
    bne     End
    
    mov     r0, #53 ; damage
    ldr     r1, =(4096 * 1.3)
    bl      Battle::EventVar_MulValue
    
End:
    pop     {r3-r6, pc}
