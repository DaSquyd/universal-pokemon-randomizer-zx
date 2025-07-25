    push {r3-r5, lr}
    mov  r5, r1
    mov  r4, r2
    mov  r0, r5
    mov  r1, r4
    mov  r2, #4 ; Ground-type
    bl   Battle::CommonTypeImmuneCheck
    cmp  r0, #0
    beq  Return
    
    mov  r0, r5
    mov  r1, r4
    mov  r2, #4 ; 1/4
    bl   Battle::CommonTypeRecoverHP
    
Return:
    pop  {r3-r5, pc}
    