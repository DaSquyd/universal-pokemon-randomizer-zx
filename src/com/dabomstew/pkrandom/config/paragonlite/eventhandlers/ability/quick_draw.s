#define S_Action 0x00
#define S_MoveId 0x04
#define S_AttackerTargets 0x08 ; 6 bytes (realistically, only 5 can get used)
#define S_AttackerTargetCount 0x0F ; byte
#define S_AttackerIteration 0x10

    push    {r3-r7, lr}
    sub     sp, #0x14
    mov     r5, r1
    mov     r4, r2
    mov     r6, r3 ; work
    
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
    ; ensure the poke has not already activated Quick Draw once while on the field
    ldrb    r0, [r6]
    cmp     r0, #FALSE
    bne     Return
    
    ; ensure the poke has not already moved this turn TODO: is this necessary for the attacker?
    mov     r0, r4
    bl      Battle::GetPoke
    mov     r1, #TF_MoveProcDone
    bl      Battle::Poke_GetTurnFlag
    cmp     r0, #FALSE
    bne     Return
    
    ; obtain attacker action
    mov     r0, r5
    mov     r1, r4
    add     r2, sp, #S_Action
    bl      Battle::Handler_GetThisTurnAction
    cmp     r0, #FALSE
    beq     Return
    
    ; get move from action
    add     r0, sp, #S_Action
    bl      Battle::Action_GetMoveId
    str     r0, [sp, #S_MoveId]
    cmp     r0, #0 ; null move id
    beq     Return
    
    ; ensure move is damaging
    bl      ARM9::IsMoveDamaging
    cmp     r0, #FALSE
    beq     Return
    
    ; ensure action is fight
    ldr     r0, [sp, #S_Action]
    lsl     r0, #28
    lsr     r0, #28
    cmp     r0, #BA_Fight
    bne     Return
    
    mov     r0, r5
    mov     r1, r4
    ldr     r2, [sp, #S_Action]
    add     r3, sp, #S_AttackerTargets
    bl      Battle::QuickDrawHandler_GetTarget
    strb    r0, [sp, #S_AttackerTargetCount]
    
    
    
    
    
    
    
    ldr     r0, [sp, #S_MoveId]
    mov     r1, #MVD_Target
    bl      ARM9::GetMoveData    
    cmp     r0, #MT_RandomAdjacentFoe
    bhi     Return
    
    ldr     r1, [sp, #S_Action]
    lsl     r1, #25
    lsr     r1, #29 ; target pos
    
    #switch r0
    #case Attacker_SelectFoe ; AnyAdjacent
    #case Attacker_SelectAlly ; UserOrAlly, only used by Acupressure
    #case Attacker_SelectAlly ; AnyAdjacentAlly, only used by Helping Hand
    #case Attacker_SelectFoe ; AnyAdjacentFoe, only used by Me First
    #case Attacker_AllAdjacent
    #case Attacker_AllAdjacentFoes
    #case Return ; UserAndAllies
    #case Return ; User, used by Bide but we'll ignore that I guess
    #case Return ; All
    #case Attacker_SelectFoe ; Thrash, Petal Dance, Outrage, Uproar, and Raging Fury
    
Attacker_SelectFoe:
    mov     r7, #EXND_AdjacentFoe
    b       Attacker_SingleSelected
    
Attacker_SelectAlly:
    mov     r7, #EXND_AdjacentAllies_Ally

Attacker_SingleSelected:
    cmp     r1, #6 ; null value
    bcs     Attacker_Expand ; there is no selected target, find one by expanding
    
    strb    r1, [sp, #S_AttackerTargets]
    mov     r0, #1
    strb    r0, [sp, #S_AttackerTargetCount]
    b       AttackerLoop_Setup

Attacker_AllAdjacent:
    mov     r7, #EXND_Adjacent
    b       Attacker_Expand
    
Attacker_AllAdjacentFoes:
    mov     r7, #EXND_AdjacentFoes
    b       Attacker_Expand

Attacker_Expand:
    mov     r0, r5 ; ServerFlow
    mov     r1, r4 ; attacker pokeId
    bl      Battle::Handler_PokeIDToPokePos
    mov     r1, r7
    lsl     r1, #8
    orr     r1, r0
    mov     r0, r5 ; server_flow
    add     r2, sp, #S_AttackerTargets
    bl      Battle::Handler_ExpandPokeID
    strb    r0, [sp, #S_AttackerTargetCount]
    
    cmp     r0, #0
    beq     Return ; no targets found
    
AttackerLoop_Setup:
    mov     r0, #0
    str     r0, [sp, #S_AttackerIteration]
    
; iterates through all targets of the attacker to find one who is also targetting the user
AttackerLoop_Start:
    
AttackerLoop_End:
    ldr     r0, [sp, #S_AttackerIteration]
    add     r0, #1 ; increment
    str     r0, [sp, #S_AttackerIteration]
    ldr     r1, [sp, #S_AttackerTargetCount]
    cmp     r0, r1
    blt     AttackerLoop_Start
    
Return:
    add     sp, #0x14
    pop     {r3-r7, pc}