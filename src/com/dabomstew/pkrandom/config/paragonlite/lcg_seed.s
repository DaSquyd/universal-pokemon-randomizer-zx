; params
; r0        seed low
; r1        seed high
;
; return
; r0        state low
; r1        state high
    
    push    {r3-r4, lr}
    
    mov     r3, #0 ; current iterations
    mov     r4, #8 ; total iterations
    
LoopStart:
    bl      ARM9::LCG
    add     r3, #1
    cmp     r3, r4
    blt     LoopStart
    
    pop     {r3-r4, pc}