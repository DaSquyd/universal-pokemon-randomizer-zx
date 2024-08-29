    push    {r4-r7, lr}
    mov     r4, r2
    mov     r5, r1
    
    mov     r0, #0x03
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    bl      Battle::GetTeamIdFromPokePos
    mov     r1, r0
    ldr     r0, [r5, #0x08]
    bl      Battle::GetParty
    mov     r3, r0
    
    ; get party size
    ldrb    r0, [r3, #0x18]
    mov     r6, #0 ; iteration
    mov     r7, #0 ; fainted count
    cmp     r0, #0
    ble     Return
    
LoopStart:
    lsl     r0, r6, #2
    ldr     r0, [r3, r0]
    bl      Battle::IsPokeFainted
    cmp     r0, #0
    beq     LoopCheckContinue
    
    add     r7, #1 ; increment fainted count

LoopCheckContinue:
    ldrb    r0, [r3, #0x18] ; party size
    add     r6, #1
    cmp     r6, r0
    blt     LoopStart

    cmp     r7, #5
    bls     Switch
    
    mov     r7, #5 ; failsafe
    
Switch:
    #SWITCH r7
    #CASE Return
    #CASE Fainted_1
    #CASE Fainted_2
    #CASE Fainted_3
    #CASE Fainted_4
    #CASE Fainted_5
    
Fainted_1:
    ldr     r1, =4506 ; 1.1x
    b       ApplyMultiplier
    
Fainted_2:
    ldr     r1, =4915 ; 1.2x
    b       ApplyMultiplier
    
Fainted_3:
    ldr     r1, =5325 ; 1.3x
    b       ApplyMultiplier
    
Fainted_4:
    ldr     r1, =5734 ; 1.4x
    b       ApplyMultiplier
    
Fainted_5:
    mov     r1, #6
    lsl     r1, #10 ; 6144 (1.5x)
    
ApplyMultiplier:
    mov     r0, #0x31
    bl      Battle::EventVar_MulValue
    
Return:
    pop     {r4-r7, pc}