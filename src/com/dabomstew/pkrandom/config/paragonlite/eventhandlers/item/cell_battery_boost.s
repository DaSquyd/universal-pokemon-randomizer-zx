    push    {r3-r7, lr}
    add     sp, #-8
    
    mov     r6, r1
    mov     r5, r2
    mov     r4, r3
    mov     r3, #198
    str     r3, [sp, #0]
    
    mov     r0, #2
    bl      Battle::EventVar_GetValue
    cmp     r5, r0
    bne     End
    
    ldr     r0, [r4]
    cmp     r0, #0
    beq     End
    
    mov     r0, #0x19
    bl      Battle::EventVar_GetValue
    mov     r7, r0
    beq     Skip
    
    ldr     r0, [r4, #4]
    cmp     r0, r7
    beq     Fail
    
Skip:
    mov     r0, r6
    mov     r1, #4
    mov     r2, r5
    bl      Battle::Handler_PushRun
    
    mov     r0, r6
    mov     r1, #4
    mov     r2, r5
    bl      Battle::Handler_PushWork
    
    str     r0, [sp, #4]
    ldr     r2, [sp, #0]
    add     r0, #4
    mov     r1, #2 ; File 0x12
    bl      Battle::Handler_StrSetup
    
    ldr     r0, [sp, #4]
    mov     r1, r5
    add     r0, #4
    bl      Battle::Handler_AddArg
    ldr     r1, [sp, #4]
    
    mov     r0, r6
    bl      Battle::Handler_PopWork
    mov     r0, r6
    mov     r1, #4
    mov     r2, r5
    bl      Battle::Handler_PushRun
    str     r7, [r4, #4]
    
Fail:
    mov     r0, #0
    str     r0, [r4]

End:
    add     sp, #8
    pop     {r3-r7, pc}