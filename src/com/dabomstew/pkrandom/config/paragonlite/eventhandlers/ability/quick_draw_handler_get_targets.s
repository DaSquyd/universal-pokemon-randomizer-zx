#define S_MoveId 0x00

    push    {r3-r7, lr}
    mov     r5, r0 ; *ServerFlow
    mov     r4, r1 ; pokeId
    mov     r6, r2 ; action
    mov     r7, r3 ; *targets
    
    lsl     r0, r6, #28
    lsr     r0, #28
    cmp     r0, #BA_Fight
    bne     ReturnZero
    
    lsl     r0, r6, #9
    lsr     r0, #16
    str     r0, [sp, #S_MoveId]
    
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
    
ReturnZero:
    mov     r0, #0
    
Return:
    pop     {r3-r7, pc}