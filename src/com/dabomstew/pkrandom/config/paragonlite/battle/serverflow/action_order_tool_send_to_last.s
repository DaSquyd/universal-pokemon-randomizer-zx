; r0: ServerFlow*
; r1: ActionOrderWork*


    ldr     r0, [r1, #ActionOrderWork.priority]
    ldr     r2, =0x01FFFFFF ; 00000001_11111111_11111111_11111111
    and     r0, r2
    str     r0, [r1, #ActionOrderWork.priority]
    bx      lr