    push    {r4-r6, lr}
    mov     r4, r1
    mov     r5, r2
    
    mov     r0, #3
    bl      Battle::EventVar_GetValue
    cmp     r5, r0
    bne     End
    
; get target speed
    mov     r0, #4
    bl      Battle::EventVar_GetValue
    mov     r1, r0
    lsl     r1, #24
    lsr     r1, #24
    mov     r0, r4
    bl      Battle::GetPoke
    mov     r1, r0
    mov     r0, r4
    mov     r2, #0
    bl      Battle::ServerEvent_CalcSpeed
    mov     r6, r0
    
; get user speed
    mov     r0, r4
    mov     r1, r5
    bl      Battle::GetPoke
    mov     r1, r0
    mov     r0, r4
    mov     r2, #0
    bl      Battle::ServerEvent_CalcSpeed
    
; 60 * user speed / target speed
    mov     r1, #50
    mul     r0, r1
    mov     r1, r6
    blx     ARM9::DivideModSigned
    mov     r1, r0
    
CheckMaxValue:
    cmp     r1, #150
    bls     CheckMinValue
    mov     r1, #150
    
CheckMinValue:
    cmp     r1, #0
    bhi     WriteValue
    mov     r1, #1
    
WriteValue:
    mov     r0, #0x30 ; move base power
    bl      Battle::EventVar_RewriteValue
    
End:
    pop     {r4-r6, pc}
    