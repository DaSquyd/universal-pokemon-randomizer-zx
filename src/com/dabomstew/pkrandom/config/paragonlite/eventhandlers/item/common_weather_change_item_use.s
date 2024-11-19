#define S_ItemId 0x00

    push    {r4-r7, lr}
    sub     sp, #0x04
    
    mov     r5, r1
    mov     r4, r2
    mov     r6, r3
    
    bl      Battle::EventObject_GetSubId
    str     r0, [sp, #S_ItemId]
    
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    ; Consume
    mov     r0, r5
    mov     r1, #HE_ConsumeItem
    mov     r2, r4
    bl      Battle::Handler_PushWork
    mov     r7, r0
    
    ldr     r2, =BTLTXT_Common_GlowItem_Activate
    add     r0, #HandlerParam_ConsumeItem.exStr
    mov     r1, #2
    bl      Battle::Handler_StrSetup
    
    ; Pokémon
    mov     r0, r7
    add     r0, #HandlerParam_ConsumeItem.exStr
    mov     r1, r4
    bl      Battle::Handler_AddArg

    ; Item
    mov     r0, r7
    add     r0, #HandlerParam_ConsumeItem.exStr
    ldr     r1, [sp, #S_ItemId]
    bl      Battle::Handler_AddArg

    ; Pop Consume
    mov     r0, r5
    mov     r1, r7
    bl      Battle::Handler_PopWork
    
    
    ; Set weather
    mov     r0, r5
    mov     r1, #HE_ChangeWeather
    mov     r2, r4
    bl      Battle::Handler_PushWork
    mov     r7, r0
    
    strb    r6, [r7, #HandlerParam_ChangeWeather.weather]
    
    mov     r0, #3 ; turn count
    strb    r0, [r7, #HandlerParam_ChangeWeather.turns]
    
    mov     r0, r5
    mov     r1, r7
    bl      Battle::Handler_PopWork
    
Return:
    add     sp, #0x04
    pop     {r4-r7, pc}