#DEFINE POKE 0x00

    push    {r3-r6, lr}
    sub     sp, #0x04
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #0x02
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    beq     Return ; cannot be user
    
    mov     r1, r4
    bl      Battle::IsAllyPokeId
    cmp     r0, #0
    beq     Return ; must be ally
    
    mov     r1, r0
    mov     r0, r5
    bl      Battle::GetPoke
    str     r0, [sp, #POKE]
    bl      Battle::IsPokeFainted
    cmp     r0, #0
    beq     Return ; must be fainted

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
    ldr     r1, [sp, #POKE]
    cmp     r0, #0
    bne     DisplayMessage ; found
    
    add     r7, #1 ; increment fainted count

LoopCheckContinue:
    ldrb    r0, [r3, #0x18] ; party size
    add     r6, #1
    cmp     r6, r0
    blt     LoopStart
    b       Return ; was not in the party
    
DisplayMessage:
    mov     r0, r5
    mov     r1, #0x02
    mov     r2, r4
    bl      Battle::Handler_PushRun
    
    mov     r0, r5
    mov     r1, #4
    mov     r2, r4
    bl      Battle::Handler_PushWork
    
    mov     r6, r0
    ldr     r2, =BTLTXT_SupremeOverlord_Activate
    add     r0, r6, #4
    mov     r1, #2
    bl      Battle::Handler_StrSetup
    
    add     r0, r6, #4
    mov     r1, r4
    bl      Battle::Handler_AddArg
    
    mov     r0, r5
    mov     r1, r6
    bl      Battle::Handler_PopWork
    
    mov     r0, r5
    mov     r1, #3
    mov     r2, r4
    bl      Battle::Handler_PushRun
    
Return:
    add     sp, #0x04
    pop     {r3-r6, pc}