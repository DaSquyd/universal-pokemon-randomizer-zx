#DEFINE S_Side 0x00
#DEFINE S_ConditionType 0x04
#DEFINE S_ConditionData 0x08
#DEFINE S_MessageId 0x0C

    push    {r4-r7, lr}
    sub     sp, #0x18
    mov     r5, r1
    mov     r4, r2
    mov     r6, r3
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    mov     r1, r0
    mov     r0, r5
    bl      Battle::GetPoke
    mov     r1, r0
    mov     r0, r5
    bl      Battle::GetEffectiveWeather
    cmp     r0, #WEATHER_Hail
    bne     Return
    
    mov     r0, #5 ; turns
    bl      Battle::MakeTurnCondition_Turns
    str     r0, [sp, #S_ConditionData]
    
    mov     r0, r4
    bl      Battle::GetTeamIdFromPokePos
    str     r0, [sp, #S_Side]
    
    mov     r0, #SC_AuroraVeil
    str     r0, [sp, #S_ConditionType]
    
    ldr     r0, =BTLTXT_AuroraVeil_Activated
    str     r0, [sp, #S_MessageId]
    
    mov     r1, r5
    mov     r2, r4
    mov     r3, r6
    bl      Battle::HandlerCommon_CreateSideStatus
    
Return:
    add     sp, #0x18
    pop     {r4-r7, pc}