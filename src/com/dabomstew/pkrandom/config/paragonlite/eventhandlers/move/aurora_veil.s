#DEFINE S_Side 0x00
#DEFINE S_ConditionType 0x04
#DEFINE S_ConditionData 0x08
#DEFINE S_MessageId 0x0C
#DEFINE S_Work 0x10

    push    {r4-r7, lr}
    sub     sp, #0x1C
    mov     r6, r0
    mov     r7, r1
    mov     r5, r2
    str     r3, [sp, #S_Work]
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    bl      Battle::GetPoke
    mov     r1, r0
    mov     r0, r7
    bl      Battle::GetEffectiveWeather
    cmp     r0, #WEATHER_Hail
    bne     Return
    
    mov     r0, #5 ; turns
    bl      Battle::MakeTurnCondition_Turns
    str     r0, [sp, #S_ConditionData]
    
    mov     r0, r5
    bl      Battle::GetTeamIdFromPokePos
    str     r0, [sp, #S_Side]
    
    mov     r0, #SC_AuroraVeil
    str     r0, [sp, #S_ConditionType]
    
    ldr     r0, =BTLTXT_AuroraVeil_Activated
    str     r0, [sp, #S_MessageId]
    
    mov     r0, r6
    mov     r1, r7
    mov     r2, r5
    ldr     r3, [sp, #S_Work]
    bl      Battle::HandlerCommon_CreateSideStatus
    
Return:
    add     sp, #0x1C
    pop     {r4-r7, pc}