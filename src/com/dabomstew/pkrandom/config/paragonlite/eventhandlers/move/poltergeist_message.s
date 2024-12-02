    push    {r3-r7, lr}
    mov     r5, r1
    mov     r4, r2
    mov     r6, r3
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, r5
    mov     r1, #HE_Message
    ldr     r2, [r6, #0x00]
    bl      Battle::Handler_PushWork
    mov     r7, r0
    
    add     r0, r7, #HandlerParam_Message.exStr
    mov     r1, #2
    ldr     r2, =BTLTXT_Poltergeist_Hit
    bl      Battle::Handler_StrSetup
    
    add     r0, r7, #HandlerParam_Message.exStr
    ldr     r1, [r6, #0x00]
    bl      Battle::Handler_AddArg
    
    add     r0, r7, #HandlerParam_Message.exStr
    ldr     r1, [r6, #0x04]
    bl      Battle::Handler_AddArg
    
    mov     r0, r5
    mov     r1, r7
    bl      Battle::Handler_PopWork
    
    
Return:
    pop     {r3-r7, pc}