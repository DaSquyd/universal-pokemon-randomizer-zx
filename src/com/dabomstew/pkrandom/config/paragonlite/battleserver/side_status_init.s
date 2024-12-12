    ldr     r3, =ARM9::sys_memset
    ldr     r0, =BattleServer::SideStatuses
    mov     r1, #0
    mov     r2, #((SIDE_STATUS_COUNT * SideStatus.size * 2) >> 1)
    lsl     r2, #1
    bx      r3
    