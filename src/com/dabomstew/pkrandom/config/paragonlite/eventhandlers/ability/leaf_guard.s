; 0x47
    push    {r4, r5, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #4
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     End
    
    mov     r0, r5
    bl      Battle::GetWeather
    cmp     r0, #1 ; sun
    bne     End
    
    mov     r0, #53 ; damage
    ldr     r1, =2732 ; 2/3x
    bl      Battle::EventVar_MulValue

End:
    pop     {r4, r5, pc}