; r0: *battleClientWk
; r1: pokeId

    push    {r3-r6, lr}
    mov     r6, r0 ; r6 := *battleClientWk
    mov     r5, r1 ; r5 := pokeId
    
    
    
    pop     {r3-r6, pc}