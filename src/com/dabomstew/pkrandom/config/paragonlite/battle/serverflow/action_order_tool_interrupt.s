; r0: ServerFlow*
; r1: ActionOrderWork*
; r2: sortedIndex

    ldr     r0, [r1, #ActionOrderWork.priority]
    mov     r2, #0x04
    lsl     r2, #24
    orr     r0, r2
    str     r0, [r1, #ActionOrderWork.priority]
    bx      lr