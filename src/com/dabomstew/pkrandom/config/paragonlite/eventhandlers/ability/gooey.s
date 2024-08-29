    push    {r3-r7, lr}
    mov     r5, r1
    mov     r4, r2 ; poke

    mov     r0, #0x04 ; defender
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return

    mov     r0, #0x46
    bl      Battle::EventVar_GetValue
    cmp     r0, #0
    bne     Return

    mov     r0, #0x12 ; move id
    bl      Battle::EventVar_GetValue
    lsl     r0, #16
    lsr     r0, #16
    mov     r1, #0 ; contact flag
    bl      ARM9::MoveHasFlag
    cmp     r0, #0
    beq     Return

    mov     r0, #3 ; attacker
    bl      Battle::EventVar_GetValue
    lsl     r0, #24
    lsr     r6, r0, #24

    mov     r0, r5
    mov     r1, r6
    bl      Battle::GetPoke
    mov     r7, r0
    bl      Battle::IsPokeFainted
    cmp     r0, #0
    bne     Return
    
    mov     r0, r7
    mov     r1, #5 ; speed stat
    sub     r2, r1, #6 ; -1
    bl      Battle::IsStatChangeValid
    cmp     r0, #0
    beq     Return

    mov     r0, r5
    mov     r1, #2
    mov     r2, r4
    bl      Battle::Handler_PushRun

    mov     r0, r5
    mov     r1, #0x0E
    mov     r2, r4
    bl      Battle::Handler_PushWork
    mov     r1, r0

    mov     r0, #5 ; speed stat
    str     r0, [r1, #0x04]
    sub     r0, #6 ; -1
    strb    r0, [r1, #0x0C]
    mov     r0, #1
    strb    r0, [r1, #0x0E]
    strb    r0, [r1, #0x0F]
    strb    r6, [r1, #0x10]
    mov     r0, r5
    bl      Battle::Handler_PopWork

    mov     r0, r5
    mov     r1, #0x03
    mov     r2, r4
    bl      Battle::Handler_PushRun
    
Return:
    pop     {r3-r7, pc}