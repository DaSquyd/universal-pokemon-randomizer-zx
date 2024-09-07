; r0: *battleClientWk

    push    {r4, lr}
    add     r0, #BTL_CLIENT_WK_AI_MEM
    ldr     r0, [r0]
    cmp     r0, #0
    beq     Create
    
    pop     {r4, pc}
    
    
Create:
    mov     r0, 
    
    pop     {r4, pc}