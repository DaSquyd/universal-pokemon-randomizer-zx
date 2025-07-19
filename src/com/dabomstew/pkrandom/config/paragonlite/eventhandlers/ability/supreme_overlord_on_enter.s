    push    {r3-r7, lr}
    sub     sp, #0x04
    mov     r5, r1
    mov     r4, r2
    mov     r6, r3
    
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    ldr     r0, [r6, #0x00]
    cmp     r0, #FALSE
    bne     Return
    
    mov     r0, #TRUE
    str     r0, [r6, #0x00]
    
    bl      Battle::GetTeamIdFromPokePos
    mov     r1, r0
    ldr     r0, [r5, #0x08]
    bl      Battle::GetParty
    mov     r3, r0
    
    ; get party size
    ldrb    r0, [r3, #0x18]
    mov     r7, #0 ; iteration
    str     r7, [sp, #0x04] ; fainted count
    cmp     r0, #0
    ble     Return
    
LoopStart:
    lsl     r0, r7, #2
    ldr     r0, [r3, r0]
    bl      Battle::IsPokeFainted
    cmp     r0, #0
    bne     DisplayMessage ; found
    
    ldr     r0, [r6, #0x04]
    add     r0, #1
    str     r0, [r6, #0x04] ; increment fainted count

LoopCheckContinue:
    ldrb    r0, [r3, #0x18] ; party size
    add     r7, #1
    cmp     r7, r0
    blt     LoopStart
    b       Return ; no fainted allies were found
    
DisplayMessage:
    mov     r0, r5
    mov     r1, #0x02
    mov     r2, r4
    bl      Battle::Handler_PushRun
    
    mov     r0, r5
    mov     r1, #4
    mov     r2, r4
    bl      Battle::Handler_PushWork
    
    mov     r7, r0
    ldr     r2, =BTLTXT_SupremeOverlord_Activate
    add     r0, r7, #4
    mov     r1, #2
    bl      Battle::Handler_StrSetup
    
    add     r0, r7, #4
    mov     r1, r4
    bl      Battle::Handler_AddArg
    
    mov     r0, r5
    mov     r1, r7
    bl      Battle::Handler_PopWork
    
    mov     r0, r5
    mov     r1, #3
    mov     r2, r4
    bl      Battle::Handler_PushRun
    
Return:
    add     sp, #0x04
    pop     {r3-r7, pc}