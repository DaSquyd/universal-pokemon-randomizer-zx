    push    {r3-r7, lr}
    mov     r6, r1
    mov     r7, r2
    
    mov     r0, #0x02
    bl      Battle::EventVar_GetValue
    cmp     r7, r0
    bne     Return
    
    mov     r0, r6
    mov     r1, r7
    bl      Battle::Unk_21AB880
    str     r0, [sp]
    mov     r0, r6
    mov     r5, #6
    ldr     r1, =0x1FF0
    add     r4, r1
    
    ldr     r1, [sp]
    add     r5, #0xFA ; 11111010
    orr     r1, r5
    lsl     r1, #16
    lsr     r1, #16
    mov     r0, r6
    mov     r2, r4
    bl      Battle::GetFoeSideCount
    mov     r5, r0
    beq     Return ; r5 == 0
    
    mov     r0, r6
    mov     r1, #0x02
    mov     r2, r7
    bl      Battle::Handler_PushRun
    
    
Return:
    pop     {r3-r7, pc}