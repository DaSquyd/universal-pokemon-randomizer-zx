#define VAR_ITEM_ID 0x00

    push    {r4-r7, lr}
    sub     sp, #0x04
    
    mov     r5, r1
    mov     r4, r2
    mov     r6, r3
    
    bl      Battle::EventObject_GetSubId
    str     r0, [sp, #VAR_ITEM_ID]
    
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    ; Text
    mov     r0, r5
    mov     r1, #HE_Message
    mov     r2, r4
    bl      Battle::Handler_PushWork
    mov     r7, r0

    add     r0, r7, #HandlerParam_Message.exStr
    mov     r1, #2 ; File 0x12
    ldr     r2, =1191 ; "[poke]'s [item] began to glow!"
    bl      Battle::Handler_StrSetup

    ; Pokémon
    add     r0, r7, #HandlerParam_Message.exStr
    mov     r1, r4
    bl      Battle::Handler_AddArg

    ; Item
    add     r0, r7, #HandlerParam_Message.exStr
    ldr     r1, [sp, #VAR_ITEM_ID]
    bl      Battle::Handler_AddArg

    ; Pop Text
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