#define S_Targets 0x00
#define S_TargetCount 0x07
#define S_HeldItem 0x08

    push    {r3-r7, lr}
    sub     sp, #0x0C
    mov     r5, r1
    mov     r4, r2
    
CheckContact:
    mov     r0, #VAR_MoveId
    bl      Battle::EventVar_GetValue
    mov     r1, #MF_Contact
    bl      ARM9::MoveHasFlag
    cmp     r0, #FALSE
    beq     Return
    
CheckHeldItem:
    mov     r0, r5
    mov     r1, r4
    bl      Battle::GetPoke
    bl      Battle::Poke_GetHeldItem
    cmp     r0, #0 ; null item
    bne     Return
    
CheckIsAttacker:
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     CheckIsDefender
    
    mov     r0, #VAR_TargetCount
    bl      Battle::EventVar_GetValue
    mov     r1, sp
    strb    r0, [r1, #S_TargetCount]
    
StoreDefendersLoop_Setup:
    mov     r6, #0 ; iterator
    
StoreDefendersLoop_Start:
    mov     r0, #VAR_TargetPokeId_0
    add     r0, r6
    bl      Battle::EventVar_GetValue
    add     r1, sp, #S_Targets
    strb    r0, [r1, r6]
    
StoreDefendersLoop_End:
    add     r6, #1
    mov     r0, sp
    ldrb    r0, [r0, #S_TargetCount]
    cmp     r6, r0
    blt     StoreDefendersLoop_Start
    b       TargetLoop_Start
    
    
CheckIsDefender:
    mov     r0, r4
    bl      Battle::HandlerCommon_CheckTargetMonID
    cmp     r0, #FALSE
    beq     Return
    
    ; define the attacker as the one and only target
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    add     r1, sp, #S_Targets
    strb    r0, [r1]
    mov     r0, #1
    mov     r1, sp
    strb    r0, [r1, #S_TargetCount]
    
    
TargetLoop_Start:
    ; iterative Fisher-Yates
    mov     r0, sp
    ldrb    r0, [r0, #S_TargetCount]
    bl      Battle::Random ; r0 := rand
    add     r1, sp, #S_Targets ; r1 := &targets
    ldrb    r7, [r1, r0] ; r7 := targets[rand]
    
    ; FY: store last value into selected index
    mov     r2, sp
    ldrb    r2, [r2, #S_TargetCount] ; r2 := targetCount
    sub     r2, #1 ; r2 := maxIndex
    ldrb    r2, [r1, r2] ; r2 := targets[maxIndex]
    strb    r2, [r1, r0] ; targets[rand] := targets[maxIndex]

    mov     r0, r5
    mov     r1, r4
    mov     r2, r7
    bl      Battle::HandlerCommon_IsUnstealableItem
    cmp     r0, #FALSE
    bne     TargetLoop_End
    
    mov     r0, r5
    mov     r1, r7
    bl      Battle::GetPoke
    bl      Battle::Poke_GetHeldItem
    str     r0, [sp, #S_HeldItem]
    cmp     r0, #0 ; null item
    beq     TargetLoop_End
    
    mov     r0, r5
    mov     r1, #HE_SwapItem
    mov     r2, r4
    bl      Battle::Handler_PushWork
    mov     r6, r0
    
    mov     r0, #(BHP_AbilityPopup >> 16)
    lsl     r0, #16
    ldr     r1, [r6, #HandlerParam_SwapItem.header]
    orr     r0, r1
    str     r0, [r6, #HandlerParam_SwapItem.header]
    
    strb    r7, [r6, #HandlerParam_SwapItem.pokeId]
    
    mov     r0, r6
    add     r0, #HandlerParam_SwapItem.exStr
    mov     r1, #2
    mov     r2, #(460 >> 2) ; "It stole [poke]'s [item]!"
    lsl     r2, #2
    bl      Battle::Handler_StrSetup
    
    mov     r0, r6
    add     r0, #HandlerParam_SwapItem.exStr
    mov     r1, r7
    bl      Battle::Handler_AddArg
    
    mov     r0, r6
    add     r0, #HandlerParam_SwapItem.exStr
    ldr     r1, [sp, #S_HeldItem]
    bl      Battle::Handler_AddArg
    
    mov     r0, r5
    mov     r1, r6
    bl      Battle::Handler_PopWork
    b       Return

TargetLoop_End:
    mov     r0, sp
    ldrb    r0, [r0, #S_TargetCount]
    sub     r0, #1
    mov     r1, sp
    strb    r0, [r1, #S_TargetCount]
    cmp     r0, #0
    bhi     TargetLoop_Start
    
Return:
    add     sp, #0x0C
    pop     {r3-r7, pc}