#DEFINE SP_R3 0x00

    push    {r3-r7, lr}
    mov     r4, r0
    ldr     r0, =ServerFlow.heManager
    mov     r5, r1
    mov     r6, r2
    mov     r7, r3
    add     r0, r4
    bl      Battle::HEManager_PushState
    str     r0, [sp, #SP_R3]
    
    mov     r0, r4
    mov     r1, r5
    mov     r2, r6
    mov     r3, r7
    bl      Battle::ServerEvent_ConditionDamage
    mov     r7, r0
    beq     Return
    
    mov     r0, r4
    mov     r1, r5
    mov     r2, r7
    bl      Battle::ServerControl_CheckSimpleDamageEnabled
    cmp     r0, #0
    beq     Return
    
    cmp     r6, #10
    bhi     Condition_None
    
    #SWITCH r0 r6
    #CASE Condition_None
    #CASE Condition_None
    #CASE Condition_None
    #CASE Condition_Frostbite
    #CASE Condition_Burn
    #CASE Condition_Poison
    #CASE Condition_None
    #CASE Condition_None
    #CASE Condition_None
    #CASE Condition_Nightmare
    #CASE Condition_Curse
    
Condition_Frostbite:
    ldr     r2, =BTLANM_Poison
    add     r2, #(BTLANM_Frostbite - BTLANM_Poison)
    b       AddEffect
    
Condition_Burn:
    ldr     r2, =BTLANM_Poison
    add     r2, #(BTLANM_Burn - BTLANM_Poison)
    b       AddEffect
    
Condition_Poison:
    ldr     r2, =BTLANM_Poison
    b       AddEffect
    
Condition_Curse:
    ldr     r2, =BTLANM_Curse
    b       AddEffect
    
Condition_Nightmare:
    ldr     r2, =BTLANM_Curse
    add     r2, #(BTLANM_Nightmare - BTLANM_Curse)
    
AddEffect:
    mov     r0, r4
    mov     r1, r5
    bl      Battle::ServerDisplay_AddEffectAtPosition
    
    
Condition_None:
    ldr     r0, =ServerFlow.strParam
    add     r0, r4
    bl      Battle::Handler_StrClear
    
    ldr     r0, =ServerFlow.strParam
    mov     r1, r5
    add     r0, r4
    mov     r2, r6
    bl      BattleServer::Condition_CreateDamageMessage
    
    ldr     r3, =ServerFlow.strParam
    mov     r0, r4
    mov     r1, r5
    mov     r2, r7
    add     r3, r4
    bl      Battle::ServerControl_SimpleDamageCore
    
Return:
    ldr     r0, =ServerFlow.heManager
    ldr     r1, [sp, #SP_R3]
    add     r0, r4
    bl      Battle::HEManager_PopState
    pop     {r3-r7, pc}