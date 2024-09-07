    push    {r3, lr}
    mov     r0, r2
    bl      Battle::HandlerCommon_CheckTargetMonID
    cmp     r0, #0
    beq     End
    
    mov     r0, #5
    bl      Battle::EventVar_GetValue
    cmp     r0, #1
    bne     End
    
    mov     r0, #0x47
    mov     r1, #1
    bl      Battle::EventVar_RewriteValue
    
End:
    pop     {r3, pc}