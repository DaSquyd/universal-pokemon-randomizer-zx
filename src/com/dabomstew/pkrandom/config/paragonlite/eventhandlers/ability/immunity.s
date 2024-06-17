    push    {r4-r6, lr}
    mov     r0, #4
    mov     r5, r1
    mov     r4, r2
    mov     r6, #4
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     End
    
    mov     r0, #0x16 ; move type
    bl      Battle::EventVar_GetValue
    cmp     r0, #3; Poison-type
    bne     End
    
; Is Poison-type
    mov     r0, #64
    mov     r1, #1
    bl      Battle::EventVar_RewriteValue
    cmp     r0, #0
    beq     End
    
    mov     r0, r5
    mov     r1, #2
    mov     r2, r4
    bl      Battle::Handler_PushRun
    
    mov     r0, r5
    mov     r1, r6
    mov     r2, r4
    bl      Battle::Handler_PushWork
    
    mov     r6, r0
    add     r0, r6, #4
    mov     r1, #2
    mov     r2, #210
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
    pop     {r4-r6, pc}