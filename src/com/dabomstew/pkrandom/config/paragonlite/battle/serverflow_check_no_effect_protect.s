#DEFINE EFF_REC 0x00
#DEFINE EVENT_TYPE 0x04
#DEFINE MOVE_PARAM 0x08
#DEFINE IN_EFF_REC 0x20

    push    {r4-r7, lr}
    sub     sp, #0x0C
    mov     r5, r0
    mov     r4, r3
    mov     r6, r2
    str     r1, [sp, #MOVE_PARAM]
    
    mov     r0, r4
    bl      BattleServer::PokeSet_SeekStart
    mov     r0, r4
    bl      BattleServer::PokeSet_SeekNext
    mov     r7, r0
    beq     NoEffectLoop_Setup

RedirectLoop_Start:
    mov     r0, r5
    mov     r1, r6
    mov     r2, r7
    bl      Battle::ServerControl_IsGuaranteedHit
    cmp     r0, #0
    bne     RedirectLoop_CheckContinue
    ldr     r0, [sp, #IN_EFF_REC]
    ldr     r1, [sp, #MOVE_PARAM]
    str     r0, [sp, #EFF_REC]
    mov     r0, #EVENT_OnRedirectTargetEnd
    str     r0, [sp, #EVENT_TYPE]
    mov     r0, r5
    mov     r2, r6
    mov     r3, r7
    bl      Battle::ServerControl_CheckNoEffectCore
    cmp     r0, #0
    beq     RedirectLoop_CheckContinue
    mov     r0, r4
    mov     r1, r7
    bl      BattleServer::PokeSet_Remove

RedirectLoop_CheckContinue:
    mov     r0, r4
    bl      BattleServer::PokeSet_SeekNext
    mov     r7, r0
    bne     RedirectLoop_Start


; No Effect
NoEffectLoop_Setup:
    mov     r0, r4
    bl      BattleServer::PokeSet_SeekStart
    mov     r0, r4
    bl      BattleServer::PokeSet_SeekNext
    mov     r7, r0
    beq     ProtectLoop_Setup

NoEffectLoop_Start:
    ldr     r0, [sp, #IN_EFF_REC]
    ldr     r1, [sp, #MOVE_PARAM]
    str     r0, [sp, #EFF_REC]
    mov     r0, #EVENT_OnNoEffectCheck
    str     r0, [sp, #EVENT_TYPE]
    mov     r0, r5
    mov     r2, r6
    mov     r3, r7
    bl      Battle::ServerControl_CheckNoEffectCore
    cmp     r0, #0
    beq     NoEffectLoop_CheckContinue
    mov     r0, r4
    mov     r1, r7
    bl      BattleServer::PokeSet_Remove

NoEffectLoop_CheckContinue:
    mov     r0, r4
    bl      BattleServer::PokeSet_SeekNext
    mov     r7, r0
    bne     NoEffectLoop_Start

ProtectLoop_Setup:
    ldr     r0, [sp, #MOVE_PARAM]
    ldrh    r0, [r0, #MoveParam.moveId]
    mov     r1, #MF_BlockedByProtect
    bl      ARM9::MoveHasFlag
    cmp     r0, #0
    beq     AbilityNoEffectLoop_Setup
    
    mov     r0, r4
    bl      BattleServer::PokeSet_SeekStart
    mov     r0, r4
    bl      BattleServer::PokeSet_SeekNext
    mov     r7, r0
    beq     AbilityNoEffectLoop_Setup

ProtectLoop_Start:
    mov     r0, r7
    mov     r1, #7
    bl      Battle::Poke_GetTurnFlag
    cmp     r0, #0
    beq     ProtectLoop_CheckContinue
    
    ldr     r3, [sp, #MOVE_PARAM]
    mov     r0, r5
    ldrh    r3, [r3, #MoveParam.moveId]
    mov     r1, r6
    mov     r2, r7
    bl      Battle::ServerEvent_CheckProtectBreak
    cmp     r0, #0
    bne     ProtectLoop_CheckContinue
    
    mov     r0, r4
    mov     r1, r7
    bl      BattleServer::PokeSet_Remove
    
    mov     r0, r7
    bl      Battle::GetPokeId
    mov     r3, r0
    ldr     r0, =0xFFFF0000
    ldr     r2, =0x020B
    str     r0, [sp, #EFF_REC]
    ldr     r0, [r5, #ServerFlow.serverCommandQueue]
    mov     r1, #91
    bl      Battle::ServerDisplay_AddMessageImpl

ProtectLoop_CheckContinue:
    mov     r0, r4
    bl      BattleServer::PokeSet_SeekNext
    mov     r7, r0
    bne     ProtectLoop_Start

AbilityNoEffectLoop_Setup:
    mov     r0, r4
    bl      BattleServer::PokeSet_SeekStart
    mov     r0, r4
    bl      Unk_21A31A0
    mov     r7, r0
    beq     GrassPowderLoop_Setup

AbilityNoEffectLoop_Start:
    ldr     r0, [sp, #IN_EFF_REC]
    ldr     r1, [sp, #MOVE_PARAM]
    str     r0, [sp, #EFF_REC]
    mov     r0, #EVENT_OnAbilityCheckNoEffect
    str     r0, [sp, #EVENT_TYPE]
    mov     r0, r5
    mov     r2, r6
    mov     r3, r7
    bl      Battle::ServerControl_CheckNoEffectCore
    cmp     r0, #0
    beq     AbilityNoEffectLoop_CheckContinue
    
    mov     r0, r4
    mov     r1, r7
    bl      BattleServer::PokeSet_Remove

AbilityNoEffectLoop_CheckContinue:
    mov     r0, r4
    bl      BattleServer::PokeSet_SeekNext
    mov     r7, r0
    bne     AbilityNoEffectLoop_Start
    

; NEW - Grass-type immunity to Powder moves
GrassPowderLoop_Setup:
    ldr     r0, [sp, #MOVE_PARAM]
    ldrh    r0, [r0, #MoveParam.moveId]
    mov     r1, #MF_Powder
    bl      ARM9::MoveHasFlag
    cmp     r0, #0
    beq     DarkPranksterLoop_Setup

    mov     r0, r4
    bl      BattleServer::PokeSet_SeekStart
    mov     r0, r4
    bl      BattleServer::PokeSet_SeekNext
    mov     r7, r0
    beq     Return
    
GrassPowderLoop_Start:
    ; check if Grass
    mov     r0, r7
    mov     r1, #TYPE_Grass
    bl      Battle::PokeHasType
    cmp     r0, #0
    beq     GrassPowderLoop_CheckContinue
    
    ; remove
    mov     r0, r4
    mov     r1, r7
    bl      BattleServer::PokeSet_Remove
    
GrassPowderLoop_CheckContinue:
    mov     r0, r4
    bl      BattleServer::PokeSet_SeekNext
    mov     r7, r0
    bne     GrassPowderLoop_Setup
    

; NEW - Dark-type immunity to Prankster
DarkPranksterLoop_Setup:
    ; check if prankster
    ldr     r0, [sp, #MOVE_PARAM]
    ldr     r0, [r0, #MoveParam.flags]
    tst     r0, #MPF_Prankster
    beq     Return

    mov     r0, r4
    bl      BattleServer::PokeSet_SeekStart
    mov     r0, r4
    bl      BattleServer::PokeSet_SeekNext
    mov     r7, r0
    beq     Return
    
DarkPranksterLoop_Start:
    ; check if ally
    mov     r0, r6
    bl      Battle::GetPokeId
    mov     r1, r0
    mov     r0, r7
    bl      Battle::GetPokeId
    bl      Battle::IsAllyPokeId
    cmp     r0, #0
    bne     DarkPranksterLoop_CheckContinue
    
    ; check if Dark
    mov     r0, r7
    mov     r1, #TYPE_Dark
    bl      Battle::PokeHasType
    cmp     r0, #0
    beq     DarkPranksterLoop_CheckContinue
    
    ; remove
    mov     r0, r4
    mov     r1, r7
    bl      BattleServer::PokeSet_Remove
    
DarkPranksterLoop_CheckContinue:
    mov     r0, r4
    bl      BattleServer::PokeSet_SeekNext
    mov     r7, r0
    bne     DarkPranksterLoop_Start

Return:
    add     sp, #0x0C
    pop     {r4-r7, pc}
    
    