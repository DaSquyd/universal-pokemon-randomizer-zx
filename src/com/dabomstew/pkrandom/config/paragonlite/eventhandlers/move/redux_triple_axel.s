    push    {r3-r5, lr}
    mov     r5, r2
    mov     r4, r3
    
    mov     r0, #0x03
    bl      Battle::EventVar_GetValue
    cmp     r5, r0
    bne     End
    
; get iteration count
    ldr     r0, [r4]
    
; power   = (i+1) * 20
; 1st hit = 20
; 2nd hit = 40
; 3rd hit = 60
; total   = 120
    add     r2, r0, #1 ; increment
    str     r2, [r4]
    
    mov     r1, #20
    mul     r1, r2
    
    mov     r0, #0x30 ; move base power
    bl      Battle::EventVar_RewriteValue
    
End:
    pop     {r3-r5, pc}