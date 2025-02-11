    push    {r3-r5, lr}    
    mov     r1, #0
    mov     r2, #0
    bl      ARM9::Poke_GetParam ; numerator
    mov     r1, #17 ; denominator (total number of types besides Normal)
    blx     ARM9::DivideModUnsigned
    add     r0, r1, #1 ; 0 -> Fighting (1), 1 -> Flying (2) (no Normal)
    pop     {r3-r5, pc}