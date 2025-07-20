    push    {r4-r7, lr}
    mov     r6, r0
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #4
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     End
    
    mov     r0, #0x46 ; move cancelled? not actually sure...
    bl      Battle::EventVar_GetValue
    cmp     r0, #0
    bne     End
    
    mov     r0, #0x38 ; effectiveness
    bl      Battle::EventVar_GetValue
    bl      Battle::GetEffectivenessAdvantage
    cmp     r0, #2 ; super effective
    bne     End
    
    mov     r0, r5
    mov     r1, r4
    bl      Battle::GetPoke
    mov     r7, r0
    
    mov     r1, #1 ; attack
    mov     r2, #1
    bl      Battle::IsStatChangeValid
    cmp     r0, #0
    bne     PushRun
    
    mov     r0, r7
    mov     r1, #3 ; sp. atk
    mov     r2, #1
    bl      Battle::IsStatChangeValid
    cmp     r0, #0
    beq     End
    
PushRun:
    mov     r0, r6
    mov     r1, r5
    mov     r2, r4
    bl      Battle::ItemEvent_PushRun
    
End:
    pop     {r4-r7, pc}