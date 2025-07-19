#if ABILITY_GALE_WINGS_HP_FRACTION <= 1
    push    {r4-r5, lr}
#else
    push    {r4-r7, lr}
#endif
    
    mov     r5, r1
    mov     r4, r2
    
    mov     r0, #VAR_AttackingPoke
    bl      Battle::EventVar_GetValue
    cmp     r4, r0
    bne     Return
    
#if ABILITY_GALE_WINGS_HP_FRACTION == 1
    mov     r0, r5
    mov     r1, r4
    bl      Battle::GetPoke
    bl      Battle::Poke_IsFullHP
    cmp     r0, #FALSE
    beq     Return
#elif ABILITY_GALE_WINGS_HP_FRACTION > 1
    mov     r0, r5
    mov     r1, r4
    bl      Battle::GetPoke
    mov     r6, r0
    mov     r1, #ABILITY_GALE_WINGS_HP_FRACTION
    bl      Battle::DivideMaxHP
    mov     r7, r0
    
    mov     r0, r6
    mov     r1, #BPV_CurrentHP
    bl      Battle::GetPokeStat
    cmp     r0, r7
    bcc     Return
#endif
    
    
    mov     r0, #VAR_MoveId
    bl      Battle::EventVar_GetValue
    bl      ARM9::GetMoveType ; base (unmodified) move type
    cmp     r0, #TYPE_Flying
    bne     Return
    
    mov     r0, #VAR_MovePriority
    bl      Battle::EventVar_GetValue
    mov     r1, r0
    mov     r0, #VAR_MovePriority
    add     r1, #1
    bl      Battle::EventVar_RewriteValue
    
Return:
#if ABILITY_GALE_WINGS_HP_FRACTION <= 1
   push    {r4-r5, pc}
#else
   push    {r4-r7, pc}
#endif
