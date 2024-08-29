    push    {r3-r5, lr}
    mov     r5, r2
    mov     r4, r3
    
    mov     r0, #0x03
    bl      Battle::EventVar_GetValue
    cmp     r5, r0
    bne     End
    
; get iteration count
    ldr     r2, [r4]
    
; power   = i * 10 + 25
; 1st hit = 25
; 2nd hit = 35
; 3rd hit = 45
; total   = 105
    mov     r1, #10
    mul     r1, r2
    add     r1, #25
    
; increment iteration count
    add     r2, #1
    str     r2, [r4]
    
    mov     r0, #0x30 ; move base power
    bl      Battle::EventVar_RewriteValue
    
End:
    pop     {r3-r5, pc}