; r6 = is crit
; r7 = damage

    lsr     r0, r7, #1
    mul     r0, r6
    add     r7, r0, r7
