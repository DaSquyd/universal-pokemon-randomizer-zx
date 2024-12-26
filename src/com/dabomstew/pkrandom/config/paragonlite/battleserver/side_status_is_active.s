; r0: side
; r1: status

    push    {r3, lr}
    mov     r2, #(SIDE_STATUS_COUNT * SideStatus.size)
    mul     r2, r0
    mov     r0, #SideStatus.size
    mul     r1, r0
    ldr     r0, =BattleServer::SideStatuses
    add     r0, r2
    ldr     r0, [r1, r0]
    cmp     r0, #0 ; doubles as FALSE
    beq     Return

    mov     r0, #TRUE

Return:
    pop     {r3, pc}
    