; Moxie but for Sp. Atk
    push    {r3-r7, lr}
    mov     r0, #3
    mov     r5, r1
    mov     r6, r2
    bl      Battle::EventVar_GetValue
    cmp     r6, r0
    bne     End
    
    mov     r0, #5
    bl      Battle::EventVar_GetValue
    mov     r7, r0
    ldr     r4, =0 ; avoids updating CPSR
    beq     End
    
LoopStart:
    add     r0, r4, #6
    bl      Battle::EventVar_GetValue
    mov     r1, r0
    lsl     r1, #24
    mov     r0, r5
    lsr     r1, #24
    bl      Battle::GetPoke
    bl      Battle::IsPokeFainted
    cmp     r0, #0
    beq     EndLoop
    
    mov     r0, r5
    mov     r1, #14
    mov     r2, r6
    bl      Battle::Handler_PushWork
    
    mov     r1, r0
    mov     r0, #2
    ldr     r2, [r1, #0]
    lsl     r0, #22
    orr     r0, r2
    str     r0, [r1, #0]
    mov     r0, #1
    strb    r0, [r1, #0x0F]
    strb    r6, [r1, #0x10]
    mov     r0, #3 ; Sp. Atk stat
    str     r0, [r1, #0x04]
    mov     r0, #1 ; boost amount
    strb    r0, [r1, #0x0C]
    mov     r0, r5
    bl      Battle::Handler_PopWork
    
EndLoop:
    add     r4, r4, #1
    cmp     r4, r7
    bcc     LoopStart
    
End:
    pop     {r3-r7, pc}
