    push    {r3-r7, lr}
    mov     r7, r1
    mov     r5, r2
    
    mov     r0, #2
    bl      Battle::EventVar_GetValue
    cmp     r5, r0
    bne     Return
    
    mov     r0, r7
    mov     r1, r5
    bl      Battle::GetPoke
    mov     r6, r0
    bl      Battle::IsPokeFainted
    cmp     r0, #0
    bne     Return
    
    mov     r0, r6
    bl      Battle::Poke_IsFullHP
    cmp     r0, #0
    bne     Return
    
    mov     r0, r6
    mov     r1, #4 ; 25%
    bl      Battle::DivideMaxHPZeroCheck
    mov     r4, r0
    
    mov     r0, r6
    mov     r1, #0x0E ; total HP
    bl      Battle::Poke_GetParam
    str     r0, [sp, #0x00]
    mov     r0, r6
    mov     r1, #0x0D ; current HP
    bl      Battle::Poke_GetParam
    ldr     r1, [sp, #0x00]
    sub     r0, r1, r0
    cmp     r4, r0
    bls     DoWork
    mov     r4, r0
    
DoWork:
    mov     r0, r7
    mov     r1, #8
    mov     r2, r5
    bl      Battle::Handler_PushWork
    
    mov     r1, r0
    strb    r5, [r1, #0x07]
    str     r4, [r1, #0x10]
    mov     r0, #1
    strb    r0, [r1, #0x04]
    strb    r0, [r1, #0x05]
    mov     r0, r7
    bl      Battle::Handler_PopWork
    
Return:
    pop     {r3-r7, pc}