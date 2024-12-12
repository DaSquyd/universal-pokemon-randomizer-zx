#define S_Result 0x00

    push    {r3-r6, lr}
    sub     sp, #0x04
    mov     r5, r0
    mov     r4, r2
    
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #VAR_SideCondition
    bl      Battle::EventVar_GetValue
    ; NEW
    cmp     r0, #SC_AuroraVeil
    beq     Apply
    ; ~NEW
    cmp     r0, #SC_LightScreen
    bhi     Return
    
Apply:
    mov     r0, #VAR_ConditionAddress
    bl      Battle::EventVar_GetValue
    str     r0, [sp, #S_Result]
    mov     r0, r5
    mov     r1, r6
    bl      Battle::CommonGetItemParam
    mov     r1, r0
    add     r0, sp, #S_Result
    bl      Battle::ConditionPtr_AddTurns
    ldr     r1, [sp, #S_Result]
    mov     r0, #VAR_ConditionAddress
    bl      Battle::EventVar_RewriteValue
    
Return:
    add     sp, #0x04
    pop     {r3-r6, pc}