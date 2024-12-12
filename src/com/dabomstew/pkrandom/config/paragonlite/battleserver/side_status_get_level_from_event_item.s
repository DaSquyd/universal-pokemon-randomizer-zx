; r0: *eventItem
; r1: side

    push    {r4, lr}
    mov     r4, r1
    bl      Battle::EventObject_GetSubId
    cmp     r0, #SIDE_STATUS_COUNT
    bge     ReturnFalse
    
    cmp     r4, #2 ; max teams
    bcs     ReturnFalse
    
    mov     r1, #(SIDE_STATUS_COUNT * SideStatus.size)
    mul     r4, r1 ; r4 := team offset
    
    mov     r1, #SideStatus.size
    mul     r0, r1 ; r0 := condition offset
    
    add     r0, r4
    
    ldr     r1, =(BattleServer::SideStatuses + SideStatus.level)
    ldrh    r0, [r1, r0]
    pop     {r4, pc}
    
ReturnFalse:
    mov     r0, #FALSE
    pop     {r4, pc}