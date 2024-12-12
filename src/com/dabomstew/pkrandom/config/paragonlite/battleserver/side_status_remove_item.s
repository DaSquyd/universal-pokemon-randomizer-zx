; r0: side
; r1: status

    push    {r4, lr}
    mov     r2, #(SIDE_STATUS_COUNT * SideStatus.size)
    mul     r2, r0 ; r2 := team offset
    
    ldr     r3, =BattleServer::SideStatuses
    
    mov     r0, #SideStatus.size
    mul     r0, r1
    
    add     r2, r3 ; r2 := team addr
    add     r4, r2, r0 ; r4 := team addr + status offset
    ldr     r0, [r4, #SideStatus.eventItem]
    cmp     r0, #0 ; doubles as false return
    beq     Return
    
    bl      Battle::EventObject_Remove
    
    mov     r0, #0
    str     r0, [r4, #SideStatus.eventItem]
    strh    r0, [r4, #SideStatus.level]
    
    ldr     r1, [r4, #SideStatus.condition]
    mov     r0, #0x07
    bic     r1, r0
    str     r1, [r4, #SideStatus.condition]
    mov     r0, #TRUE
    
Return:
    pop     {r4, pc}