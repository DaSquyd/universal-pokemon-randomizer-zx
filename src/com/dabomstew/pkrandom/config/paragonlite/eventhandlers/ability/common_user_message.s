    push    {r3-r7, lr}
    mov     r5, r0
    mov     r4, r1
    mov     r6, r2
    
    ; ensure one-time only
    ldr     r0, [r3]
    cmp     r0, #FALSE
    bne     Return
    
    mov     r0, #TRUE
    str     r0, [r3]
    
    
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, r5
    mov     r1, #HE_AbilityPopup_Add
    mov     r2, r4
    bl      Battle::Handler_PushRun
    
    mov     r0, r5
    mov     r1, #HE_Message
    mov     r2, r4
    bl      Battle::Handler_PushWork
    mov     r7, r0
    
    add     r0, r7, #(HandlerParam_Message.exStr)
    mov     r1, #2
    mov     r2, r6 ; messageId
    bl      Battle::Handler_StrSetup
    
    add     r0, r7, #(HandlerParam_Message.exStr)
    mov     r1, r4
    bl      Battle::Handler_AddArg
    
    mov     r0, r5
    mov     r1, r7
    bl      Battle::Handler_PopWork
    
    mov     r0, r5
    mov     r1, #HE_AbilityPopup_Remove
    mov     r2, r4
    bl      Battle::Handler_PushRun
    
Return:
    pop     {r3-r7, pc}