    push    {r3-r7, lr}
    mov     r0, #2
    mov     r5, r1
    mov     r4, r2
    mov     r7, #2
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     End
    
    mov     r0, r5
    mov     r1, r7
    mov     r2, r4
    bl      Battle::Handler_PushRun
    
    mov     r0, r5
    mov     r1, #4
    mov     r2, r4
    bl      Battle::Handler_PushWork
    
    mov     r6, r0
    ldr     r2, =1165 ; message id
    add     r0, r6, #4
    mov     r1, r7
    bl      Battle::Handler_StrSetup
    
    add     r0, r6, #4
    mov     r1, r4
    bl      Battle::Handler_AddArg
    
    mov     r0, r5
    mov     r1, r6
    bl      Battle::Handler_PopWork
    
    mov     r0, r5
    mov     r1, #3
    mov     r2, r4
    bl      Battle::Handler_PushRun
    
End:
    pop     {r3-r7, pc}