    push    {r3-r7, lr}
    mov     r0, #4
    mov     r5, r1
    mov     r4, r2
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r0, #0x1A
    bl      Battle::EventVar_GetValue
    cmp     r0, #1
    bne     Return
    
    mov     r0, #0x46
    bl      Battle::EventVar_GetValue
    cmp     r0, #0
    bne     Return
    
    mov     r0, r5
    mov     r1, r4
    bl      Battle::GetPoke
    
; Defense drop check
    mov     r1, #2 ; defense
    sub     r2, r1, #3 ; r2 := -1
    mov     r7, r0
    mov     r6, #0 ; success
    bl      Battle::IsStatChangeValid
    cmp     r0, #0
    bne     SetSuccess
    
; Speed boost check
    mov     r0, r7
    mov     r1, #5 ; speed
    mov     r2, #1
    bl      Battle::IsStatChangeValid
    cmp     r0, #0
    beq     CheckSuccess
    
SetSuccess:
    mov     r6, #1
    
CheckSuccess:
    cmp     r6, #0
    beq     Return
    
CheckIsFainted:
    mov     r0, r7
    bl      Battle::IsPokeFainted
    cmp     r0, #0
    bne     Return
    
    mov     r0, r5
    mov     r1, #2
    mov     r2, r4
    bl      Battle::Handler_PushRun
    
ApplyDefenseReduction:
    mov     r0, r5
    mov     r1, #0x0E
    mov     r2, r4
    bl      Battle::Handler_PushWork
    
    mov     r1, r0
    mov     r6, #1
    strb    r6, [r1, #0x0F]
    strb    r4, [r1, #0x10]
    strb    r6, [r1, #0x0E]
    mov     r0, #2 ; Defense stat
    str     r0, [r1, #0x04]
    sub     r0, r6, #2 ; -1 Boost amount
    strb    r0, [r1, #0x0C]
    mov     r0, r5
    bl      Battle::Handler_PopWork
    
ApplySpeedBoost:
    mov     r0, r5
    mov     r1, #0x0E
    mov     r2, r4
    bl      Battle::Handler_PushWork
    
    mov     r1, r0
    strb    r6, [r1, #0x0F]
    strb    r4, [r1, #0x10]
    strb    r6, [r1, #0x0E]
    mov     r0, #5 ; Speed stat
    str     r0, [r1, #0x04]
    mov     r0, #2 ; +2 boost
    str     r0, [r1, #0x0C]
    mov     r0, r5
    bl      Battle::Handler_PopWork
    
PushRun:
    mov     r0, r5
    mov     r1, #0x03
    mov     r2, r4
    bl      Battle::Handler_PushRun

Return:
    pop     {r3-r7, pc}