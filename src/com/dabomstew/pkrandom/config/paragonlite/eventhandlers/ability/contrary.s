    push    {r3-r6,LR}
    mov     r6, r1
    mov     r5, r2
    mov     r4, r3
    
    mov     r0, #2
    bl      Battle::EventVar_GetValue
    cmp     r5, r0
    bne     Return

    ; Popup Add
    mov     r0, r6
    mov     r1, #HE_AbilityPopup_Add
    mov     r2, r5
    bl      Battle::Handler_PushRun

    mov     r0, #VAR_Volume
    bl      Battle::EventVar_GetValue
    mov     r1, r0
    mov     r0, #VAR_Volume
    neg     r1, r1
    bl      Battle::EventVar_RewriteValue
    str     r0, [r4]

    ; Popup Remove
    mov     r0, r6
    mov     r1, #HE_AbilityPopup_Remove
    mov     r2, r5
    bl      Battle::Handler_PushRun

Return:
    pop     {r3-r6,PC}