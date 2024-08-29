#DEFINE FIRE 9

    push    {r4-r6, lr}
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #0x04
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    mov     r6, #4
    lsl     r6, #10
    
CheckFire:
    mov     r0, #0x16 ; move type
    bl      Battle::EventVar_GetValue
    cmp     r0, #FIRE
    bne     CheckContact
    
    lsl     r6, #1 ; x2.0
    
CheckContact:
    mov     r0, #0x12 ; move id
    bl      Battle::EventVar_GetValue
    mov     r1, #0 ; contact flag
    bl      ARM9::MoveHasFlag
    cmp     r0, #0
    beq     ApplyModifier
    
    lsr     r6, #1 ; x0.5
    
ApplyModifier:
    mov     r0, #4
    lsl     r0, #10 ; 4096
    cmp     r0, r6
    beq     Return
    
    mov     r0, #0x35
    bl      Battle::EventVar_MulValue
    
Return:
    pop     {r4-r6, pc}