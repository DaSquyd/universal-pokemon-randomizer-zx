    push {r3-r5, lr}
    mov  r5, r1
    mov  r4, r2
    mov  r0, r5
    mov  r1, r4
    mov  r2, #6 ; Bug-type
    bl   Battle::CommonTypeImmuneCheck
    cmp  r0, #0
    beq  End
    
    mov  r0, r5
    mov  r1, r4
    mov  r2, #4 ; 1/4
    bl   Battle::CommonTypeRecoverHP
    
End:
    pop  {r3-r5, pc}
    