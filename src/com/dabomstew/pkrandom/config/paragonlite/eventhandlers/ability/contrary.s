    push    {r3-r5,LR}
    mov     r0, #2
    mov     r5, r2
    mov     r4, r3
    bl      Battle::EventVar_GetValue
    cmp     r5, r0
    bne     Return

    ; Ability Prompt
    mov     r0, r6
    mov     r1, #HE_AbilityPopup_Add
    mov     r2, r7
    bl      Battle::Handler_PushRun

    mov     r0, #VAR_Volume
    bl      Battle::EventVar_GetValue
    mov     r1, r0
    mov     r0, #VAR_Volume
    neg     r1, r1
    bl      Battle::EventVar_RewriteValue
    str     r0, [r4]

Return:
    pop     {r3-r5,PC}