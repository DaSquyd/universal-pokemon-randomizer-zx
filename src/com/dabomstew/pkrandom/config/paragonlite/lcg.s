; params
; r0        current low
; r1        current high
;
; return
; r0        new low
; r1        new high
    
    push    {r3, lr}
    
    ldr     r2, =0x6C078965 ; multiplier
    ldr     r3, =0x5D588B65
    blx     ARM9::Multiply64
    
    ldr     r2, =0x269EC3 ; increment
    mov     r3, #0
    
    add     r0, r2
    adc     r1, r3 ; if overflowed low, add 1 to high
    
    pop     {r3, pc}