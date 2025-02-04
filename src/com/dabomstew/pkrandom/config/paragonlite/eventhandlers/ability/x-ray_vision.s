#define S_PokeId 0x00
#define S_EnemyPokeCount 0x04
#define S_EnemyPokes 0x08 ; up to 3 bytes
#define S_HeldItems 0x0C ; up to 6 bytes (2 bytes each)

    push    {r4-r7, lr}
    sub     sp, #0x14
    str     r2, [sp, #S_PokeId]
    mov     r7, r1 ; ServerFlow*
    
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_GetValue
    ldr     r1, [sp, #S_PokeId]
    cmp     r1, r0
    bne     Return
    
    mov     r0, r7
    add     r2, sp, #S_EnemyPokes
    bl      Battle::Handler_GetAllEnemyFrontPokeIds
    str     r0, [sp, #S_EnemyPokeCount]
    
    mov     r4, #0 ; item count
    mov     r5, #0 ; idx
    cmp     r0, #0
    bls     CheckItemCount
    
LoopStart:
    add     r1, sp, #S_EnemyPokes
    ldrb    r1, [r1, r5]
    mov     r0, r7
    bl      Battle::GetPoke
    lsl     r6, r4, #1
    bl      Battle::Poke_GetHeldItem
    add     r1, sp, #S_HeldItems
    strh    r0, [r1, r6]
    cmp     r0, #0
    beq     LoopEnd
    
    add     r4, #1 ; increment item count
    
LoopEnd:
    add     r5, #1
    ldr     r0, [sp, #S_EnemyPokeCount]
    cmp     r5, r0
    bcc     LoopStart
    
CheckItemCount:
    cmp     r4, #0
    beq     Return
    
    mov     r0, r4
    bl      BattleRandom
    lsl     r5, r0, #1 ; item offset
    
    mov     r0, r7
    mov     r1, #HE_AbilityPopup_Add
    ldr     r2, [sp, #S_PokeId]
    bl      Battle::Handler_PushRun
    
    mov     r0, r7
    mov     r1, #HE_Message
    ldr     r2, [sp, #S_PokeId]
    bl      Battle::Handler_PushWork
    mov     r4, r0
    
    add     r0, r4, #HandlerParam_Message.exStr
    mov     r1, #2 ; file num
    ldr     r2, =BTLTXT_XrayVision_Activate
    bl      Battle::Handler_StrSetup
    
    add     r0, r4, #HandlerParam_Message.exStr
    ldr     r1, [sp, #S_PokeId]
    bl      Battle::Handler_AddArg
    
    add     r0, r4, #HandlerParam_Message.exStr
    add     r1, sp, #S_HeldItems
    ldrh    r1, [r1, r5]
    bl      Battle::Handler_AddArg
    
    mov     r0, r7
    mov     r1, r4
    bl      Battle::Handler_PopWork
    
    mov     r0, r7
    mov     r1, #HE_AbilityPopup_Remove
    ldr     r2, [sp, #S_PokeId]
    bl      Battle::Handler_PushRun
    
Return:
    add     sp, #0x14
    pop     {r4-r7, pc}
    