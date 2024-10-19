; 1206

#DEFINE VAR_ATTACKING_MON 0x03
#DEFINE EFFECT_MESSAGE 0x04

    push    {r4-r6, lr}
    mov     r5, r1
    mov     r6, r2
    
    mov     r0, #VAR_ATTACKING_MON
    bl      Battle::EventVar_GetValue
    cmp     r6, r0
    bne     Return
    
    mov     r0, r5
    mov     r1, #EFFECT_MESSAGE
    mov     r2, r6
    bl      Battle::Handler_PushWork
    mov     r4, r0
    
    ldr     r2, =BTLTXT_ElectroShot_Charge
    add     r0, r4, #4
    mov     r1, #2 ; file 0x12
    bl      Battle::Handler_StrSetup
    
    add     r0, r4, #4
    mov     r1, r6
    bl      Battle::Handler_AddArg
    
    mov     r0, r5
    mov     r1, r4
    bl      Battle::Handler_PopWork
    
Return:
    pop     {r4-r6, pc}