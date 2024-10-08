    push    {r3-r5, lr}
    mov     r5, r2
    mov     r4, r3
    
    mov     r0, #0x03
    bl      Battle::EventVar_GetValue
    cmp     r5, r0
    bne     End
    
; get iteration count
    ldr     r2, [r4]
    
; power   = i * 10 + 20
; 1st hit = 20
; 2nd hit = 30
; 3rd hit = 40
; total   = 90
    mov     r1, #10
    mul     r1, r2
    add     r1, #20
    
; increment iteration count
    add     r2, #1
    str     r2, [r4]
    
    mov     r0, #0x30 ; move base power
    bl      Battle::EventVar_RewriteValue
    
End:
    pop     {r3-r5, pc}