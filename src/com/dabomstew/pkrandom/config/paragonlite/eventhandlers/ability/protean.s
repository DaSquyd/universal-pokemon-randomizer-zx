    push    {r3-r7, lr}
    mov     r5, r1
    mov     r4, r2
        
    mov     r0, #0x03
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, r5
    mov     r1, r4
    bl      Battle::GetPoke
    mov     r7, r0
    bl      Battle::IsPokeFainted
    cmp     r0, #0
    bne     Return
    
    mov     r0, #0x16 ; move type
    bl      Battle::EventVar_GetValue
    lsl     r0, #24
    lsr     r6, r0, #24
    cmp     r6, #18 ; no type, case of Struggle
    beq     Return
    
    mov     r0, r5
    mov     r1, #0x02
    mov     r2, r4
    bl      Battle::Handler_PushRun
    
    mov     r0, r5
    mov     r1, #0x14
    mov     r2, r4
    bl      Battle::Handler_PushWork
    mov     r7, r0
    
    mov     r0, r6
    bl      Battle::MakeSetTypeData1
    strh    r0, [r7, #0x04]
    
    mov     r0, r5
    mov     r1, r7
    strb    r4, [r7, #0x06]
    bl      Battle::Handler_PopWork
    
    mov     r0, r5
    mov     r1, #0x03
    mov     r2, r4
    bl      Battle::Handler_PushRun
    
Return:
    pop     {r3-r7, pc}