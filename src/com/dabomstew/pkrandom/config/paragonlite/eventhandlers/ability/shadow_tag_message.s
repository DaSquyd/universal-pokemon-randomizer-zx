    push    {r3-r7, lr}
    mov     r5, r1
    mov     r4, r2
    mov     r7, r3
    
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
;    ; Check already announced
;    ldr     r0, [r7]
;    cmp     r0, #FALSE
;    bne     Return
;    
;    mov     r0, #TRUE
;    str     r0, [r7]
    
    mov     r0, r5
    mov     r1, #HE_AbilityPopup_Add
    mov     r2, r4
    bl      Battle::Handler_PushRun
    
    mov     r0, r5
    mov     r1, #HE_Message
    mov     r2, r4
    bl      Battle::Handler_PushWork
    
    mov     r6, r0
    add     r0, r6, #4
    mov     r1, #2
    ldr     r2, =BTLTXT_ShadowTag_Activate
    bl      Battle::Handler_StrSetup
    
    add     r0, r6, #4
    mov     r1, r4
    bl      Battle::Handler_AddArg
    
    mov     r0, r5
    mov     r1, r6
    bl      Battle::Handler_PopWork
    
    mov     r0, r5
    mov     r1, #3
    mov     r2, r4
    bl      Battle::Handler_PushRun
    
Return:
    pop     {r3-r7, pc}