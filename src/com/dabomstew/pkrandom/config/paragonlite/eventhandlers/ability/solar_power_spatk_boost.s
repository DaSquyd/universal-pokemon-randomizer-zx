; 1.5x -> 1.3x
    push    {r4-r6, lr}
    mov     r0, #3
    mov     r5, r1
    mov     r4, r2
    mov     r6, #3
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     End
    
    mov     r0, r5
    bl      Battle::ServerEvent_GetWeather
    cmp     r0, #1 ; sun
    bne     End
    
    mov     r0, #18 ; move
    bl      Battle::EventVar_GetValue
    lsl     r0, #16
    lsr     r0, #16
    
    bl      ARM9::GetMoveCategory
    cmp     r0, #2 ; special
    bne     End
    
    mov     r0, #53 ; stat
    ldr     r1, =5324 ; 1.3, mirrors Life Orb (instead of 5325)
    bl      Battle::EventVar_MulValue
    
End:
    pop     {r4-r6, pc}
