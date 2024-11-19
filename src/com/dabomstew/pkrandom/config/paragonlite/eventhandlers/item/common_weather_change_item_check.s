#define VAR_ITEM_ID 0x00

    push    {r4-r7, lr}
    sub     sp, #0x04
    
    mov     r7, r0
    mov     r5, r1
    mov     r4, r2
    mov     r6, r3
    
    bl      Battle::EventObject_GetSubId
    str     r0, [sp, #VAR_ITEM_ID]
    
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, r5
    bl      Battle::ServerEvent_GetWeather
    cmp     r0, r6
    beq     Return
    
PushRun:
    mov     r0, r7
    mov     r1, r5
    mov     r2, r4
    bl      Battle::ItemEvent_PushRun
    
Return:
    add     sp, #0x04
    pop     {r4-r7, pc}