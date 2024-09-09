#DEFINE SP_R3 0x00

    push    {r3-r7, lr}
    mov     r4, r0
    ldr     r0, =SERVER_FLOW_HE_MANAGER
    mov     r5, r1
    ldr     r1, =0x2624 ; TODO: unused?
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
    ldr     r2, =CONDITION_ANIM_POISON
    add     r2, #(CONDITION_ANIM_FROSTBITE - CONDITION_ANIM_POISON)
    b       AddEffect
    
Condition_Burn:
    ldr     r2, =CONDITION_ANIM_POISON
    add     r2, #(CONDITION_ANIM_BURN - CONDITION_ANIM_POISON)
    b       AddEffect
    
Condition_Poison:
    ldr     r2, =CONDITION_ANIM_POISON
    b       AddEffect
    
Condition_Curse:
    ldr     r2, =CONDITION_ANIM_CURSE
    b       AddEffect
    
Condition_Nightmare:
    ldr     r2, =CONDITION_ANIM_CURSE
    add     r2, #(CONDITION_ANIM_NIGHTMARE - CONDITION_ANIM_CURSE)
    
AddEffect:
    mov     r0, r4
    mov     r1, r5
    bl      Battle::ServerDisplay_AddEffectAtPosition
    
    
Condition_None:
    ldr     r0, =SERVER_FLOW_STR_PARAM
    add     r0, r4
    bl      Battle::Handler_StrClear
    
    ldr     r0, =SERVER_FLOW_STR_PARAM
    mov     r1, r5
    add     r0, r4
    mov     r2, r6
    bl      BattleServer::CreateDamageMessage
    
    ldr     r3, =SERVER_FLOW_STR_PARAM
    mov     r0, r4
    mov     r1, r5
    mov     r2, r7
    add     r3, r4
    bl      Battle::ServerControl_SimpleDamageCore
    
Return:
    ldr     r0, =SERVER_FLOW_HE_MANAGER
    ldr     r1, [sp, #SP_R3]
    ldr     r2, =0x2644
    add     r0, r4
    bl      Battle::HEManager_PopState
    pop     {r3-r7, pc}