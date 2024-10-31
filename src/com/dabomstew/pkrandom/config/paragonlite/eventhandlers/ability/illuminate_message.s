    push    {r3-r6, lr}
    mov     r5, r1
    mov     r4, r2
    
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
    mov     r6, r0
    
    add     r0, r6, #HandlerParam_Message.exStr
    mov     r1, #2
    ldr     r2, =BTLTXT_Illuminate_Activate
    bl      Battle::Handler_StrSetup
    
    add     r0, r6, #HandlerParam_Message.exStr
    mov     r1, r4
    bl      Battle::Handler_AddArg
    
    mov     r0, r5
    mov     r1, r6
    bl      Battle::Handler_PopWork
    
    mov     r0, r5
    mov     r1, #HE_AbilityPopup_Remove
    mov     r2, r4
    bl      Battle::Handler_PushRun
    
Return:
    pop     {r3-r6, pc}