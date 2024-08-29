    push    {r3-r7, lr}
    mov     r7, r0
    mov     r6, r1
    mov     r5, r2
    
    mov     r0, #0x02
    bl      Battle::EventVar_GetValue
    mov     r7, r0
    cmp     r5, r0
    beq     Return ; cannot be user
    
    mov     r1, r5
    bl      Battle::IsAllyPokeId
    cmp     r0, #0
    beq     Return ; must be ally
    
    mov     r0, r6
    mov     r1, #0x05
    mov     r2, r5
    bl      Battle::Handler_PushWork
    mov     r4, r0
    
    mov     r0, r6
    mov     r1, r7
    bl      Battle::GetPoke
    mov     r1, #16
    bl      Battle::DivideMaxHPZeroCheck
    strh    r0, [r4, #0x04]
    strb    r7, [r4, #0x06]
    ldr     r1, [r4, #0x00]
    mov     r0, #8
    lsl     r0, #20 ; 0x00800000
    orr     r0, r1
    str     r0, [r4, #0x00]
    mov     r0, r6
    mov     r1, r4
    bl      Battle::Handler_PopWork
    
Return:
    pop     {r3-r7, pc}