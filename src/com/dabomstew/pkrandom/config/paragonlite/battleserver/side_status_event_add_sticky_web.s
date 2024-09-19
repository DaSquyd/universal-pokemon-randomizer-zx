    mov     r1, #1
    str     r1, [r0]
    ldr     r0, =Battle::HandlerSide_StickyWeb_EventTable
    bx      lr