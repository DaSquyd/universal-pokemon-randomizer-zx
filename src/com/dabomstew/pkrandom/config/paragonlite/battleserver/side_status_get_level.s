; r0: side
; r1: status

    mov     r2, #(SIDE_STATUS_COUNT * SideStatus.size)
    mul     r2, r0
    mov     r0, #SideStatus.size
    mul     r1, r0
    ldr     r0, =(BattleServer::SideStatuses + SideStatus.level)
    add     r0, r2
    ldrh    r0, [r1, r0]
    bx      lr
    