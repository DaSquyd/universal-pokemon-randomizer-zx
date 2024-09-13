    push    {r3-r7, lr}
    mov     r4, r1
    mov     r5, r2
    mov     r6, r3
    
    mov     r0, #0x03
    bl      Battle::EventVar_GetValue
    cmp     r5, r0
    bne     Return
    
    ldr     r0, [r6]
    cmp     r0, #0 ; Mode: 0 = Damage, 1 = Heal
    beq     Return
    
    mov     r0, #0x04
    bl      Battle::EventVar_GetValue
    mov     r6, r0
    
    mov     r0, r4
    mov     r1, r6
    bl      Battle::GetPoke
    mov     r7, r0
    bl      Battle::IsPokeFullHP
    cmp     r0, #0
    bne     DisplayFullHPMessage
    
Heal:
    mov     r0, r4
    mov     r1, #0x05
    mov     r2, r5
    bl      Battle::Handler_PushWork
    mov     r5, r0
    
    strb    r6, [r5, #0x06]
    
    mov     r0, r7
    mov     r1, #2 ; 1/2
    bl      Battle::DivideMaxHPZeroCheck
    strh    r0, [r5, #0x04]
    
    mov     r0, r5
    add     r0, #8
    mov     r1, #2 ; file 0x12
    ldr     r2, =387 ; "[poke]'s HP was restored."
    bl      Battle::Handler_StrSetup
    
    mov     r0, r5
    add     r0, #8
    mov     r1, r6
    bl      Battle::Handler_AddArg
    
    b       PopWork
    

DisplayFullHPMessage:
    mov     r0, r4
    mov     r1, #0x04
    mov     r2, r5
    bl      Battle::Handler_PushWork
    mov     r5, r0
    
    add     r0, r5, #4
    mov     r1, #2 ; file 0x12
    ldr     r2, =893 ; "[poke]'s HP is full!"
    bl      Battle::Handler_StrSetup
    
    add     r0, r5, #4
    mov     r1, r6
    bl      Battle::Handler_AddArg

PopWork:
    mov     r0, r4
    mov     r1, r5
    bl      Battle::Handler_PopWork
    
Return:
    pop     {r3-r7, pc}